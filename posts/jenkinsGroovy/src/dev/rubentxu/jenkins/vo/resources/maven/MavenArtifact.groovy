package dev.rubentxu.jenkins.vo.resources.maven

import dev.rubentxu.jenkins.vo.resources.Artifact


class MavenArtifact extends Artifact {
    String artifactId
    String groupId

    Boolean isSnapshot() {
        return this.version.endsWith("-SNAPSHOT")
    }
}
