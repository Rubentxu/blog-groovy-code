package dev.rubentxu.jenkins

import dev.rubentxu.jenkins.cdi.IServiceFactory
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.interfaces.IService

import java.util.concurrent.ConcurrentHashMap

class PipelineContext implements IPipelineContext {
    private final Map<String, IServiceFactory> services
    private List<String> skipStages = new ArrayList()
    private Script steps
    private ILogger logger
    private IConfigClient configClient

    PipelineContext(Script steps, ILogger logger, IConfigClient configClient) {
        this.steps = steps
        this.logger = logger
        this.services = new ConcurrentHashMap<>()
        this.skipStages = new ArrayList()
        this.configClient = configClient
    }

    @Override
    void registerService(String name, IServiceFactory factory) {
        services[name] = factory
    }


    @Override
    IService getService(String name) {
        return services.containsKey(name) ? services[name].create(this) : null
    }

    @Override
    void initializeServices(IConfigClient configClient) {
        services.each { name, factory ->
            factory.create(this).initialize(configClient)
        }
    }

    @Override
    List<String> getSkipStages() {
        return skipStages
    }

    @Override
    void addSkipStage(String stage) {
        skipStages.add(stage)
    }

    @Override
    void injectEnvironmentVariables(Map<String, String> envVars) {
        logger.debug('Custom environment variables to be inserted in pipeline')
        logger.debug(envVars.toString())
        def jenkinsEnvVars = steps.env
        envVars.each { k, v -> jenkinsEnvVars[k] = v }
    }

    @Override
    IConfigClient getConfigClient() {
        return configClient
    }

    @Override
    ILogger getLogger() {
        return logger
    }

    @Override
    Script getSteps() {
        return steps
    }

}
