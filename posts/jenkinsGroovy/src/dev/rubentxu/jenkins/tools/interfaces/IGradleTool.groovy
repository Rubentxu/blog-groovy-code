package dev.rubentxu.jenkins.tools.interfaces

import dev.rubentxu.jenkins.interfaces.IBuildTool
import dev.rubentxu.jenkins.vo.resources.gradle.GradleArtifact
import dev.rubentxu.jenkins.vo.resources.gradle.GradleIProjectDefinitionFile


interface IGradleTool extends IBuildTool<GradleArtifact, GradleIProjectDefinitionFile> {

}
