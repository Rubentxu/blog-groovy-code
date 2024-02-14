package dev.rubentxu.jenkins

import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IPipeline
import dev.rubentxu.jenkins.interfaces.IStepsExecutor

abstract class StepsExecutor implements IStepsExecutor {

    protected Script steps
    protected ILogger logger
    protected IPipeline pipeline

    StepsExecutor(IPipeline pipeline) {
        this.pipeline = pipeline
        this.steps = pipeline.getService("StepsScript")
        this.logger = pipeline.getService("ILogger")
    }

}
