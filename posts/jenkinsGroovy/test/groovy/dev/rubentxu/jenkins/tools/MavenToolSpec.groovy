package dev.rubentxu.jenkins.tools

import dev.rubentxu.jenkins.mocks.TestContext
import dev.rubentxu.jenkins.mocks.credentials.UsernamePasswordCredentials
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.maven.MavenArtifact

class MavenToolSpec extends TestContext {

    Map<String, String> mavenProperties = [
            'maven.settingsPath'  : 'settings.xml',
            'maven.pomXmlPath'    : 'pom.xml',
            'maven.settingsFileId': 'settingsFileId',
            'maven.debug'         : 'false',
            'maven.publishGoal'   : 'deploy',
            'maven.args'          : '-DskipTests'
    ]

    def "test build"() {
        given:
        def pipeline = this.createPipeline(mavenProperties)
//        steps.setCredentials([
//                credentialsId: new UsernamePasswordCredentials('credentialsId', 'user', 'password')
//        ])
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
        steps.validate().sh(script: 'mvn -s settings.xml -DskipTests package', returnStdout: _)[2]
    }

    def "test publish"() {
        given:
        def pipeline = this.createPipeline(mavenProperties)
        def mavenTool = new MavenTool(pipeline)
        def repository = new ArtifactRepository() // Define this with appropriate values

        when:
        mavenTool.publish(repository, new MavenArtifact())

        then:
        steps.validate().sh('mvn -s settings.xml -DskipTests deploy org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1:deploy', _)[1]
    }

    def "test executeTask"() {
        given:
        def pipeline = this.createPipeline(mavenProperties)
        def mavenTool = new MavenTool(pipeline)
        def taskName = 'testTask'
        def options = ['option1', 'option2']

        when:
        mavenTool.executeTask(taskName, options)

        then:
        steps.validate().sh('mvn -s settings.xml -DskipTests testTask option1 option2', _)[1]
    }
}