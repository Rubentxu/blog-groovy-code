package dev.rubentxu.jenkins.tools

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.StepsExecutor
import dev.rubentxu.jenkins.interfaces.IPipeline
import dev.rubentxu.jenkins.tools.interfaces.IGradleTool
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.gradle.GradleArtifact
import dev.rubentxu.jenkins.vo.resources.gradle.GradleFileDefinition

import java.nio.file.Paths

class GradleTool extends StepsExecutor implements IGradleTool {

    private String credentialsId
    private String buildGradlePath
    private String gradlePropertiesFile
    private Boolean debugMode
    private Boolean useWrapper

    GradleTool(IPipeline pipeline) {
        super(pipeline)
        initialize(pipeline.getPipelineConfig())
    }

    @Override
    void build() {
        executeTask('clean build', ['-x', 'test'], false)
    }

    @Override
    void publish(ArtifactRepository repository) {
        def repoUrl = "${repository.baseUrl}/${repository.name}"
        GradleFileDefinition definition = readFileDefinition()
        def options = [
            "-Pversion=${definition.version}",
            "-Prepository_repo_url=${repoUrl}"
        ]
        executeTaskWithCredentials('publish', options, repository.credentialsId, false)

        def artifactRef = "${definition.group.replaceAll('\\.', '/')}/${definition.name}/${definition.version}"
        pipeline.addOrUpdateResource(
            new GradleArtifact(
                id: "${definition.group}:${definition.name}:${definition.version}",
                name: definition.name,
                domain: definition.group,
                version: definition.version,
                url: "${repoUrl}/${artifactRef}"
            )
        )
    }

    @Override
    GradleFileDefinition readFileDefinition() {
        def gradleProperties = steps.readProperties(file: gradlePropertiesFile)
        def name = gradleProperties.name
        def group = gradleProperties.group
        def fileDefinition = new GradleFileDefinition(
                id: "${group}:${name}",
                name: name,
                group: group,
                version: gradleProperties?.version?.replaceAll('-SNAPSHOT', '') ?: '0.0.0'
        )
        pipeline.addOrUpdateResource(fileDefinition)
        return fileDefinition
    }

    @Override
    void writeVersion(String overrideVersion) {
        if (steps.sh(returnStatus: true, script: "gradle tasks --all | grep -i updateVersion") == 0) {
            executeTask('updateVersion', ["-PnewVersion='${overrideVersion}'"])
        } else {
            steps.sh """sed -i -r '/^version( ?)=/s/[0-9]+.*/${overrideVersion}/g' ${gradlePropertiesFile}"""
        }
        pipeline.releaseVersion.version = overrideVersion
        readFileDefinition()
    }

    @Override
    void executeTask(String taskName, List<String> options) {
        executeTask(taskName, options, false)
    }

    @Override
    String executeTask(String taskName, List<String> options, Boolean returnStdout) {
        executeTaskWithCredentials(taskName, options, credentialsId, returnStdout)
    }

    private String executeTaskWithCredentials(String taskName, List<String> options, String credentials, Boolean returnStdout) {
        steps.dir(Paths.get(this.buildGradlePath).parent.toString()) {
            steps.withCredentials([
                    steps.usernamePassword(credentialsId: credentials, usernameVariable: 'USR', passwordVariable: 'PSW')
            ]) {
                def tool = "${useWrapper ? './gradlew' : 'gradle'} ${debugMode ? '-d' : ''}".toString().trim()
                options.addAll([
                        "-Prepository_user=${steps.env.USR}",
                        "-Prepository_apiKey=${steps.env.PSW}"
                ])
                return steps.sh(script: "${tool} ${taskName} ${options.join(' ')}", returnStdout: returnStdout)
            }
        }
    }


    @NonCPS
    @Override
    void initialize(Map configuration) {
        def get = configuration.&get
        this.credentialsId = get('gradle.repository.credentialsId')
        this.buildGradlePath = get('gradle.buildGradlePath')
        this.gradlePropertiesFile = get('gradle.gradlePropertiesPath')
        this.debugMode = get('gradle.debug')
        this.useWrapper = get('gradle.useWrapper')
    }


}
