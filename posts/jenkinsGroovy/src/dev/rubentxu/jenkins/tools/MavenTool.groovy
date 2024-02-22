package dev.rubentxu.jenkins.tools

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.Steps
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.tools.interfaces.IMavenTool
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.maven.MavenArtifact
import dev.rubentxu.jenkins.vo.resources.maven.MavenFileDefinition

class MavenTool extends Steps implements IMavenTool {

    private String mavenDefaultArgs
    private String mavenSettingsPath
    private String pomXmlPath
    private String settingsFileId
    private Boolean debugMode
    private String publishGoal

    MavenTool(IPipelineContext pipeline) {
        super(pipeline)
    }

    @Override
    void executeTask(String taskName, List<String> options) {
        executeTask(taskName, options, false)
    }

    @Override
    String executeTask(String taskName, List<String> options, Boolean returnStdout) {
        def args = options.join(' ')
        def workDir = new File(pomXmlPath).getParent()?: '.'
        steps.dir(workDir) {
            def tool = debugMode ? 'mvn -X' : 'mvn'
            steps.sh(returnStdout: returnStdout,
                    script: "${tool} -s '${mavenSettingsPath}' ${mavenDefaultArgs} ${taskName} ${args}")

        }
    }

    @Override
    MavenArtifact build(List<String> options) {
        executeTask('package', ['-DskipTests'] + options)
        MavenFileDefinition definition = readFileDefinition()
        return new MavenArtifact(
                id: "${definition.groupId}:${definition.artifactId}:${definition.version}",
                name: definition.artifactId,
                domain: definition.groupId,
                version: definition.version
        )
    }

    private void copyMavenSettingsFile() {
        if (!steps.fileExists(file:  mavenSettingsPath)) {
            steps.configFileProvider([steps.configFile(fileId: settingsFileId, variable: 'FILE')]) {
                steps.sh("cp -p '${steps.env.FILE}' '${mavenSettingsPath}'")
            }
        }
    }

    @Override
    void publish(ArtifactRepository repository, MavenArtifact artifact) {
        executeTask("${publishGoal} org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy", [
                "-DaltDeploymentRepository=${repository.id}::${repository.baseUrl}/${repository.name}"
        ])
    }

    @Override
    MavenFileDefinition readFileDefinition() {
        def pom = steps.readMavenPom(file: pomXmlPath)
        def groupId = pom.groupId ?: pom.parent.groupId

        def definition = new MavenFileDefinition(
                id: "${groupId}:${pom.artifactId}",
                name: "${groupId}:${pom.artifactId}",
                artifactId: pom.artifactId,
                groupId: groupId,
                version: pom.version,
                dependencies: pom.dependencies
        )
        return definition
    }

    @Override
    def readPom() {
        return readFileDefinition()
    }

    @Override
    MavenFileDefinition writeVersion(String overrideVersion) {
        executeTask('versions:set', ["-DnewVersion=${overrideVersion}"])
        return readFileDefinition()
    }

    @NonCPS
    @Override
    void initialize(IConfigClient configClient) {
        this.mavenSettingsPath = configClient.get('maven.settingsPath')
        this.pomXmlPath = configClient.get('maven.pomXmlPath')
        this.settingsFileId = configClient.get('maven.settingsFileId')
        this.debugMode = configClient.get('maven.debug')
        this.publishGoal = configClient.get('maven.publishGoal')
        this.mavenDefaultArgs = configClient.get('maven.args')
        copyMavenSettingsFile()
    }


}
