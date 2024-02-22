package dev.rubentxu.jenkins.mocks

import dev.rubentxu.jenkins.PipelineContext
import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.logger.Logger
import spock.lang.Specification

class TestContext  extends Specification {

    protected StepsMock steps
    protected ILogger loggerSpy
    protected ConfigClientMock configClient

    protected IPipelineContext createPipeline(Map<String, String> config = [:]) {
        steps = new StepsMock()
        loggerSpy = Spy(new Logger(steps))
        configClient = new ConfigClientMock(config)
        return new PipelineContext(steps, loggerSpy, configClient)

    }
}