package dev.rubentxu.jenkins.tools


import dev.rubentxu.jenkins.mocks.TestContext
import dev.rubentxu.jenkins.vo.resources.ArtifactRepository
import dev.rubentxu.jenkins.vo.resources.gradle.GradleFileDefinition

class GradleToolSpec extends TestContext {

    def "test build"() {
        given:
        def pipeline = this.createPipeline()
        def gradleTool = new GradleTool(pipeline)

        when:
        gradleTool.build()

        then:
        steps.validate().sh('clean build', ['-x', 'test'], false)[1]
    }

    def "test publish"() {
        given:
        def pipeline = this.createPipeline()
        def gradleTool = new GradleTool(pipeline)
        def repository = new ArtifactRepository() // Define this with appropriate values

        when:
        gradleTool.publish(repository)

        then:
        steps.validate().executeTaskWithCredentials('publish', _, repository.credentialsId, false)[1]
    }

    def "test readFileDefinition"() {
        given:
        def pipeline = this.createPipeline()
        def gradleTool = new GradleTool(pipeline)

        when:
        def definition = gradleTool.readFileDefinition()

        then:
        steps.validate().readProperties(file: _)[1]
        definition instanceof GradleFileDefinition
    }

    def "test writeVersion"() {
        given:
        def pipeline = this.createPipeline()
        def gradleTool = new GradleTool(pipeline)
        def overrideVersion = '1.0.0'

        when:
        gradleTool.writeVersion(overrideVersion)

        then:
        steps.validate().executeTask('updateVersion', ["-PnewVersion='${overrideVersion}'"])[1]
    }

    def "test executeTask"() {
        given:
        def pipeline = this.createPipeline()
        def gradleTool = new GradleTool(pipeline)
        def taskName = 'testTask'
        def options = ['option1', 'option2']

        when:
        gradleTool.executeTask(taskName, options)

        then:
        steps.validate().sh(taskName, options, false)[1]
    }
}