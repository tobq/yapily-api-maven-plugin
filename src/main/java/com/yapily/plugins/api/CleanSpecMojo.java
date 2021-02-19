package com.yapily.plugins.api;

import java.io.IOException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import lombok.extern.slf4j.Slf4j;

@Mojo(name = "clean-specs", defaultPhase = LifecyclePhase.CLEAN)
@Slf4j
public class CleanSpecMojo extends AbstractMojo {
    @Parameter(property = "openapi-generator.version")
    String openapiGeneratorVersion;
    @Component
    private MavenProject project;
    @Component
    private MavenSession mavenSession;
    @Component
    private BuildPluginManager pluginManager;

    @Override public void execute() throws MojoExecutionException {
        try {
            Utils.cleanSpecParent(project);
        } catch (IOException e) {
            log.error("Failed to clean", e);
            throw new MojoExecutionException("Failed to clean", e);
        }
    }
}

