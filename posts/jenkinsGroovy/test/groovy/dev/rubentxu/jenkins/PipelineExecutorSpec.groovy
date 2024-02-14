package dev.rubentxu.jenkins

import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IService
import dev.rubentxu.jenkins.mocks.StepsExecutorMock
import spock.lang.Specification

//@CompileStatic
class PipelineExecutorSpec extends Specification {

    protected StepsExecutorMock steps
    protected ILogger loggerSpy

    void setup() {
        steps = new StepsExecutorMock()
        loggerSpy = Spy(new Logger(steps))
    }

    def "test registerService and getService"() {
        setup:
        def pipelineExecutor = new PipelineExecutor(steps, loggerSpy)

        when:
        pipelineExecutor.registerService('testService', { -> return Mock(IService) })

        then:
        pipelineExecutor.getService('testService') instanceof IService

    }

    def "test initializeServicesConfiguration"() {
        setup:
        def pipelineExecutor = new PipelineExecutor(steps, loggerSpy)
        def serviceMock = Mock(IService)
        pipelineExecutor.registerService('testService', { -> return serviceMock })

        when:
        pipelineExecutor.initializeServicesConfiguration([testService: 'testValue'])

        then:
        1 * serviceMock.initialize('testValue')
    }

    def "test addSkipStage and getSkipStages"() {
        setup:
        def pipelineExecutor = new PipelineExecutor(steps, loggerSpy)

        when:
        pipelineExecutor.addSkipStage('testStage')

        then:
        pipelineExecutor.getSkipStages() == ['testStage']
    }

    def "test injectEnvironmentVariables"() {
        setup:
        def pipelineExecutor = new PipelineExecutor(steps, loggerSpy)
        steps.env = [:]

        when:
        pipelineExecutor.injectEnvironmentVariables([testVar: 'testValue'])

        then:
        steps.env.testVar == 'testValue'
    }
}
