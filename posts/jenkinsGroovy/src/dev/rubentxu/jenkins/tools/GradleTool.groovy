package dev.rubentxu.jenkins.tools

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.Steps
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.tools.interfaces.IGradleTool
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.gradle.GradleArtifact
import dev.rubentxu.jenkins.vo.resources.gradle.GradleFileDefinition

import java.nio.file.Paths

class GradleTool extends Steps implements IGradleTool {

    private String credentialsId
    private String buildGradlePath
    private String gradlePropertiesFile
    private Boolean debugMode
    private Boolean useWrapper

    GradleTool(IPipelineContext pipeline) {
        super(pipeline)
    }

    @Override
    GradleArtifact build(List<String> options) {
        execute('clean build', ['-x', 'test'] + options)
        GradleFileDefinition definition = readFileDefinition()
        String artifactRef = "${definition.group.replaceAll('\\.', '/')}/${definition.name}/${definition.version}"

        new GradleArtifact(
                id: "${definition.group}:${definition.name}:${definition.version}",
                name: definition.name,
                domain: definition.group,
                version: definition.version,
                url: "${repoUrl}/${artifactRef}"
        )

    }


    @Override
    void publish(ArtifactRepository repository, GradleArtifact artifact) {
        def repoUrl = "${repository.baseUrl}/${repository.name}"

        def options = [
                "-Pversion=${artifact.version}",
                "-Prepository_repo_url=${repoUrl}"
        ]
        executeTaskWithCredentials('publish', options, repository.credentialsId, false)


    }

    @Override
    GradleFileDefinition readFileDefinition() {
        def gradleProperties = steps.readProperties(file: this.gradlePropertiesFile)
        return new GradleFileDefinition(
                id: "${gradleProperties.group}:${gradleProperties.name}",
                name: gradleProperties.name,
                group: gradleProperties.group,
                version: gradleProperties?.version ?: '0.0.0'
        )

    }

    @Override
    GradleFileDefinition writeVersion(String overrideVersion) {
        if (steps.sh(returnStatus: true, script: "gradle tasks --all | grep -i updateVersion") == 0) {
            execute('updateVersion', ["-PnewVersion='${overrideVersion}'"])
        } else {
            steps.sh """sed -i -r '/^version( ?)=/s/[0-9]+.*/${overrideVersion}/g' ${gradlePropertiesFile}"""
        }
        return readFileDefinition()
    }


    @Override
    String execute(String taskName, List<String> options) {
        executeTaskWithCredentials(taskName, options, credentialsId)
    }

    private String executeTaskWithCredentials(String taskName, List<String> options, String credentials) {
        steps.dir(Paths.get(this.buildGradlePath).parent.toString()) {
            steps.withCredentials([
                    steps.usernamePassword(credentialsId: credentials, usernameVariable: 'USR', passwordVariable: 'PSW')
            ]) {
                def tool = "${useWrapper ? './gradlew' : 'gradle'} ${debugMode ? '-d' : ''}".toString().trim()
                options.addAll([
                        "-Prepository_user=${steps.env.USR}",
                        "-Prepository_apiKey=${steps.env.PSW}"
                ])
                return steps.sh(script: "${tool} ${taskName} ${options.join(' ')}", returnStdout: true)
            }
        }
    }


    @NonCPS
    @Override
    void initialize(IConfigClient configClient) {
        this.credentialsId = configClient.required('gradle.repository.credentialsId', String.class)
        this.buildGradlePath = configClient.optional('gradle.buildGradlePath', 'build.gradle')
        this.gradlePropertiesFile = configClient.optional('gradle.gradlePropertiesPath', 'gradle.properties')
        this.debugMode = configClient.optional('gradle.debug', false)
        this.useWrapper = configClient.optional('gradle.useWrapper', false)

    }
}
