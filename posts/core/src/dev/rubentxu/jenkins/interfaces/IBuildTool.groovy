package dev.rubentxu.jenkins.interfaces

import dev.rubentxu.jenkins.vo.resources.Artifact
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.IProjectDefinitionFile

interface IBuildTool<A extends Artifact, F extends IProjectDefinitionFile> extends ITool {

    void resolveDependencies()

    boolean test()

    A build(List<String> options)

    void publish(ArtifactRepository repository, A artifact)

    F readFileDefinition()

    F writeVersion(String overrideVersion)

}
