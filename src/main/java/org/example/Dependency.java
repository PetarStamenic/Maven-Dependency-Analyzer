package org.example;

import java.util.Objects;

public class Dependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;

    public Dependency(String groupId, String artifactId, String version, String scope) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.scope = scope;
    }

    public String getKey() {
        return groupId + ":" + artifactId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dependency that)) return false;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(artifactId, that.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + (scope != null ? ":" + scope : "");
    }
}

