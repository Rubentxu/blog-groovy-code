package dev.rubentxu.jenkins.interfaces

import dev.rubentxu.jenkins.vo.resources.Artifact
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.FileDefinition
import dev.rubentxu.jenkins.vo.resources.Resource


interface IBuildTool<A extends Artifact, F extends FileDefinition> extends ITool {

    A build(List<String> options)

    void publish(ArtifactRepository repository, A artifact)

    F readFileDefinition()

    F writeVersion(String overrideVersion)
}
