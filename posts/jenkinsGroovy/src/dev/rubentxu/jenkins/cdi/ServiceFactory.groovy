package dev.rubentxu.jenkins.cdi

import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.interfaces.IService

class ServiceFactory implements IServiceFactory {
    private final Closure<IService> closure

    private ServiceFactory(Closure<IService> closure) {
        this.closure = closure
    }

    @Override
    IService create(IPipelineContext pipeline) {
        return closure.call(pipeline)
    }

    static ServiceFactory from(Closure<IService> closure) {
        return new ServiceFactory(closure)
    }
}