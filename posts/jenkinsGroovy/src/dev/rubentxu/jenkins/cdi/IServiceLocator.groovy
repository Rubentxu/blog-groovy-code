package dev.rubentxu.jenkins.cdi

import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.IService

interface IServiceLocator extends Serializable {

    def <T extends IService> T getService(String name)

    void registerService(String name, IServiceFactory factory)

    void initializeServices(IConfigClient configClient)
}
