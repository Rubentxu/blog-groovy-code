package dev.rubentxu.jenkins.tools

import dev.rubentxu.jenkins.mocks.TestContext
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.maven.MavenArtifact

class MavenToolSpec extends TestContext {

    Map<String, String> mavenProperties = [
            'maven.settingsPath'  : 'settings.xml',
            'maven.pomXmlPath'    : 'pom.xml',
            'maven.settingsFileId': 'settingsFileId',
            'maven.debug'         : false,
            'maven.publishGoal'   : 'deploy',
            'maven.args'          : ''
    ]

    def "test build"() {
        given:
        mavenProperties['maven.debug'] = true
        def pipeline = this.createPipeline(mavenProperties)
        steps.readMavenPom = {
            [
                    groupId: 'testGroupId',
                    artifactId: 'testArtifactId',
                    version: '1.0.0-test'
            ]
        }

        def mavenTool = new MavenTool(pipeline)

        when:
        mavenTool.build([])


        then:
        steps.validate().sh(script: "mvn -s 'settings.xml' package -DskipTests", returnStdout: true )[2]
    }

    def "test build with DebugMode"() {
        given:
        mavenProperties['maven.debug'] = true
        def pipeline = this.createPipeline(mavenProperties)
        steps.readMavenPom = {
            [
                    groupId: 'testGroupId',
                    artifactId: 'testArtifactId',
                    version: '1.0.0-test'
            ]
        }

        def mavenTool = new MavenTool(pipeline)

        when:
        mavenTool.build([])

        then:
        steps.validate().sh(script: "mvn -X -s 'settings.xml' package -DskipTests", returnStdout: true )[2]
    }

    def "test publish"() {
        given:
        def pipeline = this.createPipeline(mavenProperties)
        def mavenTool = new MavenTool(pipeline)
        def repository = new ArtifactRepository(
                id: 'testRepository',
                name: 'testRepository',
                baseUrl: 'http://localhost:8081/repository',
                credentialsId: 'testCredentialsId'
        )

        when:
        mavenTool.publish(repository, new MavenArtifact())

        then:
        loggerSpy.info("Publishing artifact to testRepository")
        steps.validate().sh(script: "mvn -s 'settings.xml' verify org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy -DaltDeploymentRepository=testRepository::http://localhost:8081/repository/testRepository", _)[2]
    }

    def "test executeTask"() {
        given:
        def pipeline = this.createPipeline(mavenProperties)
        def mavenTool = new MavenTool(pipeline)
        def taskName = 'testTask'
        def options = ['option1', 'option2']

        when:
        mavenTool.execute(taskName, options)

        then:
        steps.validate().sh(script :"mvn -s 'settings.xml' testTask option1 option2", _)[2]
    }
}