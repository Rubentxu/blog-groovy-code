package dev.rubentxu.jenkins.cdi

import dev.rubentxu.jenkins.PipelineContext
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.logger.Logger

class ContextSinglenton {
    private static PipelineContext instance

    private ContextSinglenton() {
        // constructor privado
    }

    static synchronized PipelineContext getContext() {
        if (instance == null) {
          throw new IllegalStateException('Pipeline context not created')
        }
        return instance
    }

    static synchronized PipelineContext createInstance(Script steps, ILogger logger, IConfigClient configClient) {
        if (instance == null) {
            instance = new PipelineContext(steps, logger, configClient)
            logger.info('Pipeline context created')
        }
        return instance
    }
}
