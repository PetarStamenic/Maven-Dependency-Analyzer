package org.example;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PomParser {

    public static PomData parse(Path pomPath) throws Exception {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        try (FileReader fileReader = new FileReader(pomPath.toFile())) {
            Model model = reader.read(fileReader);

            String groupId = model.getGroupId();
            String artifactId = model.getArtifactId();
            String version = model.getVersion();

            if (groupId == null && model.getParent() != null) {
                groupId = model.getParent().getGroupId();
            }
            if (version == null && model.getParent() != null) {
                version = model.getParent().getVersion();
            }

            Parent parent = model.getParent();
            String parentGroupId = null;
            String parentArtifactId = null;
            String parentVersion = null;

            if (parent != null) {
                parentGroupId = parent.getGroupId();
                parentArtifactId = parent.getArtifactId();
                parentVersion = parent.getVersion();
            }

            List<Dependency> dependencies = new ArrayList<>();
            for (org.apache.maven.model.Dependency dep : model.getDependencies()) {
                dependencies.add(new Dependency(
                        dep.getGroupId(),
                        dep.getArtifactId(),
                        dep.getVersion(),
                        dep.getScope()
                ));
            }

            return new PomData(
                    groupId,
                    artifactId,
                    version,
                    parentGroupId,
                    parentArtifactId,
                    parentVersion,
                    pomPath,
                    dependencies
            );
        }
    }
}
