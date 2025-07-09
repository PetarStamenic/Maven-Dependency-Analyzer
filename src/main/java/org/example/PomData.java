package org.example;

import java.nio.file.Path;
import java.util.List;

public class PomData {
    private String groupId;
    private String artifactId;
    private String version;

    private String parentGroupId;
    private String parentArtifactId;
    private String parentVersion;

    private Path pomPath;

    private List<Dependency> dependencies;

    public PomData(String groupId,
                   String artifactId,
                   String version,
                   String parentGroupId,
                   String parentArtifactId,
                   String parentVersion,
                   Path pomPath,
                   List<Dependency> dependencies) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.parentGroupId = parentGroupId;
        this.parentArtifactId = parentArtifactId;
        this.parentVersion = parentVersion;
        this.pomPath = pomPath;
        this.dependencies = dependencies;
    }

    public String getCoordinates() {
        return groupId + ":" + artifactId;
    }

    public String getParentCoordinates() {
        if (parentGroupId != null && parentArtifactId != null) {
            return parentGroupId + ":" + parentArtifactId;
        }
        return null;
    }

    public Path getPomPath() {
        return pomPath;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return String.format(
                "%s:%s:%s [Parent: %s:%s:%s]",
                groupId,
                artifactId,
                version,
                parentGroupId,
                parentArtifactId,
                parentVersion
        );
    }
}