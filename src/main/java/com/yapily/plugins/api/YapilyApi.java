package com.yapily.plugins.api;

import lombok.Value;

@Value class YapilyApi {
    String name;
    String version;

    @Override public String toString() {
        return String.format("%s API (%s)", name, version);
    }

    public String getLocalGitRepositoryFolderName() {
        return String.format("%s-%s", name, version);
    }

}
