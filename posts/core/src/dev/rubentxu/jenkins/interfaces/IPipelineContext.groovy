package dev.rubentxu.jenkins.interfaces

import dev.rubentxu.jenkins.cdi.IServiceLocator

interface IPipelineContext extends IServiceLocator {

    List<String> getSkipStages()

    void addSkipStage(String stage)

    void injectEnvironmentVariables(Map<String, String> envVars)

    IConfigClient getConfigClient()

    ILogger getLogger()

    Script getSteps()


}
