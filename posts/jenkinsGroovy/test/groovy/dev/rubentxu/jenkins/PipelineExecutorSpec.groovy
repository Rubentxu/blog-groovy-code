package dev.rubentxu.jenkins


import dev.rubentxu.jenkins.interfaces.IService
import dev.rubentxu.jenkins.mocks.TestContext
import dev.rubentxu.jenkins.cdi.ServiceFactory

class PipelineExecutorSpec extends TestContext {


    def "test registerService and getService"() {
        setup:
        def pipeline = this.createPipeline()

        when:
        pipeline.registerService('testService', ServiceFactory.from { return Mock(IService) })
        def service = pipeline.getService('testService')

        then:
        service instanceof IService

    }

    def "test initializeServicesConfiguration"() {
        setup:
        def pipeline = this.createPipeline()
        def serviceMock = Mock(IService)
        pipeline.registerService('testService', ServiceFactory.from { return serviceMock })
        configClient.setConfig([testService: 'testValue'])

        when:
        pipeline.initializeServices(configClient)

        then:
        1 * serviceMock.configure(configClient)
    }

    def "test addSkipStage and getSkipStages"() {
        setup:
        def pipeline = this.createPipeline()

        when:
        pipeline.addSkipStage('testStage')

        then:
        pipeline.getSkipStages() == ['testStage']
    }

    def "test injectEnvironmentVariables"() {
        setup:
        def pipeline = this.createPipeline()
        steps.env = [:]

        when:
        pipeline.injectEnvironmentVariables([testVar: 'testValue'])

        then:
        steps.env.testVar == 'testValue'
    }
}
