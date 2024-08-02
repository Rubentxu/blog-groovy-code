import dev.rubentxu.jenkins.ConfigClientMock
import dev.rubentxu.jenkins.cdi.ServiceFactory
import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.logger.Logger
import dev.rubentxu.jenkins.tools.MavenTool
import dev.rubentxu.jenkins.tools.interfaces.IMavenTool

import static dev.rubentxu.jenkins.cdi.ContextSinglenton.createInstance
import static dev.rubentxu.jenkins.cdi.ContextSinglenton.getContext

void initialize(Script steps, Map<String, Object> config) {
    ConfigClientMock configClient = new ConfigClientMock(config)
    ILogger logger = new Logger(steps)
    def pipeline = createInstance(steps, logger, configClient)
    getContext().registerService('maven', ServiceFactory.from {
        IMavenTool mavenTool = new MavenTool(pipeline)
        mavenTool.initialize()
        mavenTool
    })
}

