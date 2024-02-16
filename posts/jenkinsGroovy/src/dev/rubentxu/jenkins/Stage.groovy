package dev.rubentxu.jenkins

import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.interfaces.IStageExecutor


abstract class Stage extends Steps implements IStageExecutor {

    Stage(IPipelineContext pipeline) {
        super(pipeline)
    }

    @Override
    def stage(String name, Closure body) {
        body.delegate = steps

        steps.stage(name) {
            if (!evaluateIfSkipStage(name)) {
                def result = null
                try {
                    logger.info "Execute Stage with name: '$name'"
                    result = body()
                } catch (Exception ex) {
                    handleException(name, ex, logger)
                    steps.currentBuild.result = 'FAILURE'
                    steps.error ex.getMessage()
                }
                logger.info "Finalize Stage with name: '$name'"
                return result
            }
        }
    }

    protected boolean evaluateIfSkipStage(String name) {
        Boolean skipped = false
        if (pipeline.skipStages?.contains(name)) {
            logger.warn "Stage '$name' is marked to be skipped (pipeline.skipStages=${pipeline.skipStages.join(',')})"
            skipped = true
        }
        return skipped
    }

    static void handleException(String name, Exception ex, ILogger logger) {
        List lines = ["Error in Stage $name", ex.toString(), ex.getMessage()]
        lines.add('----------------------------------------')
        lines.addAll(filterStackTrace(ex).join("\n"))
        logger.logPrettyError(lines)
    }


}
