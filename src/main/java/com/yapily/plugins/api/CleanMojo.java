package com.yapily.plugins.api;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

@Mojo(name = "clean")
public class CleanMojo extends AbstractMojo {
    @Component
    private MavenProject project;

    @Override public void execute() throws MojoExecutionException {
        try {
            Utils.cleanSpecParent(project);
            Utils.cleanServerStubbing(project);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to clean", e);
        }
    }
}

