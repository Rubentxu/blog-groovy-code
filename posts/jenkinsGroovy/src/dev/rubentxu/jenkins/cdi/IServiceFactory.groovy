package dev.rubentxu.jenkins.cdi

import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.interfaces.IService

@FunctionalInterface
interface IServiceFactory {
    IService create(IPipelineContext pipeline);
}