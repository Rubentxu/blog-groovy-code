package dev.rubentxu.jenkins

import dev.rubentxu.jenkins.cdi.IServiceFactory
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IPipeline
import dev.rubentxu.jenkins.interfaces.IService
import java.util.concurrent.ConcurrentHashMap

class PipelineExecutor implements IPipeline {
    private final Map<String, IServiceFactory> services
    private List<String> skipStages = new ArrayList()
    private Script steps
    private ILogger logger
    private IConfigClient configClient

    PipelineExecutor(Script steps, ILogger logger, IConfigClient configClient) {
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
        return services.containsKey(name) ? services[name].call(pipeline) : null
    }

    @Override
    void initializeServicesConfiguration(Map configuration) {
        services.each { name, serviceClosure ->
            serviceClosure.create(this).initialize(configuration)
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
        logger.debug(envVars)
        def jenkinsEnvVars = steps.env
        envVars.each { k, v -> jenkinsEnvVars[k] = v }
    }

    @Override
    Map<String, Object> getPipelineConfig() {
        return null
    }
}
