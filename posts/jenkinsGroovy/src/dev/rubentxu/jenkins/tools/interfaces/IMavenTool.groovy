package dev.rubentxu.jenkins.tools.interfaces

import dev.rubentxu.jenkins.interfaces.IBuildTool
import dev.rubentxu.jenkins.vo.resources.maven.MavenArtifact
import dev.rubentxu.jenkins.vo.resources.maven.MavenFileDefinition


interface IMavenTool extends IBuildTool<MavenArtifact, MavenFileDefinition> {
    def readPom()
}
