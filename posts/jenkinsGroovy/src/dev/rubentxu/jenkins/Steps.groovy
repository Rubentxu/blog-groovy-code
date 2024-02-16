package dev.rubentxu.jenkins

import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.interfaces.ISteps

abstract class Steps implements ISteps {

    protected Script steps
    protected ILogger logger
    protected IPipelineContext pipeline

    Steps(IPipelineContext pipeline) {
        this.pipeline = pipeline
        this.steps = pipeline.getSteps()
        this.logger = pipeline.getLogger()
    }

}
