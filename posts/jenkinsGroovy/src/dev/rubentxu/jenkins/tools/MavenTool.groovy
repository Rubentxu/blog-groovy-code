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

    public static final String TOOL_NAME = 'mvn'
    private String mavenDefaultArgs
    private String mavenSettingsPath
    private String pomXmlPath
    private String settingsFileId
    private Boolean debugMode

    MavenTool(IPipelineContext pipeline) {
        super(pipeline)
    }


    @Override
    String execute(String taskName, List<String> options) {
        def args = mavenDefaultArgs + options.join(' ')
        def workDir = new File(pomXmlPath).getParent()?: '.'
        steps.dir(workDir) {
            String tool = debugMode ? "${TOOL_NAME} -X" : TOOL_NAME
            steps.sh(script: "${tool} -s '${mavenSettingsPath}' ${taskName} ${args}".trim(), returnStdout: true)
        }
    }

    @Override
    MavenArtifact build(List<String> options) {
        execute('package', ['-DskipTests'] + options)
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
        execute("verify org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy", [
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
        execute('versions:set', ["-DnewVersion=${overrideVersion}"])
        return readFileDefinition()
    }

    @NonCPS
    @Override
    void initialize(IConfigClient configClient) {
        this.mavenSettingsPath = configClient.optional('maven.settingsPath', String.class)
        this.pomXmlPath = configClient.optional('maven.pomXmlPath', String.class)
        this.settingsFileId = configClient.required('maven.settingsFileId', String.class)
        this.debugMode = configClient.optional('maven.debug', Boolean.class)
        this.mavenDefaultArgs = configClient.optional('maven.args', String.class)
        copyMavenSettingsFile()
    }


}
