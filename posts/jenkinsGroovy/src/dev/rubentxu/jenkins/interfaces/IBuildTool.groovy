package dev.rubentxu.jenkins.interfaces

import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.Resource

interface IBuildTool extends ITool {

    void build()

    void publish(ArtifactRepository repository)

    def <T extends Resource> T readFileDefinition()

    void writeVersion(String overrideVersion)
}
