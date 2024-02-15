package dev.rubentxu.jenkins.interfaces

import dev.rubentxu.jenkins.cdi.IServiceLocator

interface IPipeline extends IServiceLocator {

    List<String> getSkipStages()

    void addSkipStage(String stage)

    void injectEnvironmentVariables(Map<String, String> envVars)

    IConfigClient getConfigClient()

}
