package com.yapily.plugins.api;

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
import org.eclipse.jgit.annotations.Nullable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mojo(name = "generate-server", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateServerMojo extends AbstractMojo {

    @Parameter(required = true)
    String apiVersion;
    @Parameter(required = true)
    String apiName;
    @Parameter(defaultValue = "openapi.yml")
    String repositorySpecPath;
    @Nullable
    @Parameter
    String localSpecPath;
    @Parameter(required = true)
    String gitUrlTemplate;
    @Parameter(defaultValue = Utils.DEFAULT_GIT_BRANCH_TEMPLATE)
    String gitBranchTemplate;
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
        GenerateService.of(apiName, apiVersion,
                           repositorySpecPath,
                           localSpecPath,
                           gitUrlTemplate,
                           gitBranchTemplate,
                           autoGitignore,
                           openapiGeneratorVersion,
                           openapiConfigurationOverrides,
                           project,
                           mavenSession,
                           pluginManager,
                           "/openapi-generator.configuration.server.xml")
                       .generate();
    }
}
