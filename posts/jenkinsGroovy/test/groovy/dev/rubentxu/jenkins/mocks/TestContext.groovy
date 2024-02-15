package dev.rubentxu.jenkins.mocks

import dev.rubentxu.jenkins.PipelineExecutor
import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IPipeline
import dev.rubentxu.jenkins.logger.Logger
import spock.lang.Specification

class TestContext  extends Specification {

    protected StepsExecutorMock steps
    protected ILogger loggerSpy
    protected ConfigClientMock configClient

    protected IPipeline createPipeline() {
        steps = new StepsExecutorMock()
        loggerSpy = Spy(new Logger(steps))
        configClient = new ConfigClientMock()
        return new PipelineExecutor(steps, loggerSpy, configClient)
    }
}