package dev.rubentxu.jenkins.cdi

import dev.rubentxu.jenkins.interfaces.IPipeline
import dev.rubentxu.jenkins.interfaces.IService

@FunctionalInterface
interface IServiceFactory {
    IService create(IPipeline pipeline);
}