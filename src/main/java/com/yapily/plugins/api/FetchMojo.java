package com.yapily.plugins.api;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.errors.GitAPIException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mojo(name = "fetch")
public class FetchMojo extends AbstractMojo {
    @Parameter(required = true)
    String apiVersion;
    @Parameter(required = true)
    String apiType;
    @Component
    private MavenProject project;

    @Override public void execute() throws MojoExecutionException {
        var api = new YapilyApi(apiType, apiVersion);
        var apiName = api.toString();

        try {
            log.info("Fetching {}", apiName);
            Utils.fetchApi(api, project);
        } catch (IOException | GitAPIException e) {
            throw new MojoExecutionException("Failed to fetch " + apiName, e);
        }
    }
}

