package dev.rubentxu.jenkins.tools.interfaces

import dev.rubentxu.jenkins.interfaces.IBuildTool
import dev.rubentxu.jenkins.vo.resources.gradle.GradleArtifact
import dev.rubentxu.jenkins.vo.resources.gradle.GradleFileDefinition


interface IGradleTool extends IBuildTool<GradleArtifact, GradleFileDefinition> {
}
