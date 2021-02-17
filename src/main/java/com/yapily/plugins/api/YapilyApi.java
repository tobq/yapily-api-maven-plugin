package com.yapily.plugins.api;

import lombok.Value;

@Value class YapilyApi {
    String type;
    String version;

    @Override public String toString() {
        return String.format("%s-%s", type, version);
    }

    /**
     * @return bitbucket ssh url
     */
    public String getGitUrl() {
        return String.format("git@bitbucket.org:yapily/%s-api.git", type);
    }

    public String getVersionTag() {
        return String.format("v%s", version);
    }
}
