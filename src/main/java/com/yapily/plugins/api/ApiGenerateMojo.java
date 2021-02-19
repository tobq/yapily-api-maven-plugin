package com.yapily.plugins.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;

@Slf4j
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class ApiGenerateMojo extends AbstractMojo {
    public static final String GITIGNORE_ENTRIES_SUFFIX = "### END OF AUTO-GENERATION yapily-api-implementation-maven-plugin";
    @Parameter(required = true)
    String apiVersion;
    @Parameter(required = true)
    String apiType;
    @Parameter(defaultValue = "true")
    boolean autoGitignore;
    @Parameter(property = "version.openapi-generator", defaultValue = "5.0.1")
    String openapiGeneratorVersion;
    @Parameter
    private Map<?, ?> openapiConfigurationOverrides;
    @Component
    private MavenProject project;
    @Component
    private MavenSession mavenSession;
    @Component
    private BuildPluginManager pluginManager;

    @Override
    public void execute() throws MojoExecutionException {
        var api = new YapilyApi(apiType, apiVersion);

        fetchApi(api);

        Configuration configuration = configuration(api);
        try {
            executeMojo(
                    MojoExecutor.plugin("org.openapitools", "openapi-generator-maven-plugin", openapiGeneratorVersion),
                    goal("generate"),
                    configuration.getXml(),
                    executionEnvironment(project, mavenSession, pluginManager)
            );
        } catch (MojoExecutionException e) {
            log.error("Failed to generate server stubbing", e);
            throw e;
        }
        log.debug("Generated stubbing with configuration: {}", configuration);

        log.debug("Adding compile source root: {}", configuration.getOutputDirectory());
        project.addCompileSourceRoot(configuration.getOutputDirectory().toString());


        if (autoGitignore) {
            try {
                autoGitIgnoreArtifacts();
            } catch (IOException e) {
                log.debug("Failed to automatically ignore fetched specs", e);
            }
        }
    }

    private void autoGitIgnoreArtifacts() throws IOException {
        var nearestGitDir = new RepositoryBuilder()
                .findGitDir(project.getBasedir())
                .getGitDir();

        if (nearestGitDir != null) {
            var gitRepo = nearestGitDir.getParentFile().toPath();
            var gitIgnorePath = gitRepo.resolve(".gitignore");

            if (Files.exists(gitIgnorePath)) {
                var versionedGitignoreEntriesPrefix = "### AUTO-GENERATED BY yapily-api-implementation-maven-plugin " + getClass().getPackage().getImplementationVersion();
                var specParentIgnoreEntry = gitRepo
                        .relativize(Utils.getSpecParent(project))
                        .toString();

                try (var br = new BufferedReader(new FileReader(gitIgnorePath.toFile()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(versionedGitignoreEntriesPrefix)) return;
                    }
                }
                log.info("Appending generated yapily-api specifications folder to .gitignore of repository: {}", gitRepo);
                try (var os = new BufferedWriter(new FileWriter(gitIgnorePath.toFile(), true))) {
                    os.newLine();
                    os.write(versionedGitignoreEntriesPrefix);
                    os.newLine();
                    os.write(specParentIgnoreEntry);
                    os.newLine();
                    os.write("openapitools.json");
                    os.newLine();
                    os.write(GITIGNORE_ENTRIES_SUFFIX);
                }
            }
        }
    }

    private Configuration configuration(YapilyApi api) throws MojoExecutionException {
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

        var outputDirectory = Utils.getServerStubbing(project);
        openapiMavenPluginConfiguration.addChild(element("output", outputDirectory.toString()).toDom());

        if (openapiConfigurationOverrides != null) {
            log.info("Merging user-defined openapi-generator configuration");
            log.debug("\t config {}", openapiConfigurationOverrides);

            openapiMavenPluginConfiguration = Xpp3Dom.mergeXpp3Dom(
                    openapiMavenPluginConfiguration,
                    Utils.buildElement((Map<String, Object>) openapiConfigurationOverrides, "configuration").toDom()
            );
        }

        // add the inputSpec (-i) path (from the yapily-api local-repo)
        openapiMavenPluginConfiguration.addChild(element("inputSpec", Utils.getSpec(api, project).toString()).toDom());

        return new Configuration(openapiMavenPluginConfiguration, outputDirectory);
    }

    private void fetchApi(YapilyApi api) throws MojoExecutionException {
        log.info("Fetching {}", api);

        if (Utils.isGitRepository(Utils.getPath(api, project))) {
            log.info("\t{} already cached", api);
        } else {
            Utils.fetchApi(api, project);
        }
    }
}

@Value class Configuration {
    Xpp3Dom xml;
    Path outputDirectory;
}

