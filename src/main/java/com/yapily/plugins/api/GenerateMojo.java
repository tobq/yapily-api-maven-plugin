package com.yapily.plugins.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FS;

import lombok.extern.slf4j.Slf4j;
import static org.eclipse.jgit.lib.RepositoryCache.FileKey.isGitRepository;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

@Slf4j
@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE)
public class GenerateMojo extends AbstractMojo {
    @Parameter(required = true)
    String apiVersion;
    @Parameter(required = true)
    String apiType;
    @Parameter(readonly = true, property = "openapi-generator.version")
    String openapiGeneratorVersion;
    @Parameter
    Xpp3Dom openApiOptions;
    @Component
    private MavenProject project;
    @Component
    private MavenSession mavenSession;
    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException {
        var api = new YapilyApi(apiType, apiVersion);
        try {
            fetchApi(api);
        } catch (GitAPIException | IOException e) {
            try {
                Utils.clean(api, project);
            } catch (IOException e1) {
                log.error("Failed to clean up api", e1);
            }
            throw new MojoExecutionException("Failed to fetch api" + e.getClass().getName(), e);
        }

        try {
            executeMojo(
                    plugin(
                            groupId("org.openapitools"),
                            artifactId("openapi-generator-maven-plugin"),
                            version(openapiGeneratorVersion)
                    ),
                    goal("generate"),
                    configuration(api),
                    executionEnvironment(
                            project,
                            mavenSession,
                            pluginManager
                    )
            );
        } catch (MojoExecutionException e) {
            log.error("Failed to generate server stubbing", e);
            throw e;
        }
    }

    private Xpp3Dom configuration(YapilyApi api) throws MojoExecutionException {
        Xpp3Dom openapiMavenPluginConfiguration;
        try (var is = getClass().getResourceAsStream("/openapi-generator.configuration.xml")) {
            if (is == null) {
                throw new IOException("Failed to obtain embdedded openapi-generator configuration");
            }
            openapiMavenPluginConfiguration = Xpp3DomBuilder.build(is, StandardCharsets.UTF_8.toString());
        } catch (XmlPullParserException | IOException e) {
            log.error("Failed to parse embedded openapi-generator configuration", e);
            throw new MojoExecutionException("Failed to parse embedded openapi-generator configuration", e);
        }

        // add the inputSpec (-i) path (from the yapily-api local-repo)
        openapiMavenPluginConfiguration.addChild(element("inputSpec", Utils.getConfig(api, project).toString()).toDom());

        if (openApiOptions != null) {
            log.info("Merging user-defined openapi-generator configuration");
            openapiMavenPluginConfiguration = Xpp3Dom.mergeXpp3Dom(openapiMavenPluginConfiguration, openApiOptions);
        }
        return openapiMavenPluginConfiguration;
    }

    private void fetchApi(YapilyApi api) throws GitAPIException, IOException {
        var apiName = api.toString();
        log.info("Fetching {} API", apiName);

        // TODO: JGit is saying valid git repo is invalid for some reason (even creating a Git object fails)
        boolean repoCloned = isGitRepository(Utils.getPath(api, project).toFile(), FS.DETECTED);

        if (repoCloned) {
            log.info("\t{} API already cached", apiName);
        } else {
            Utils.fetchApi(api, project);
        }
    }
}

