package dev.rubentxu.jenkins.tools.interfaces

import dev.rubentxu.jenkins.interfaces.ITool


interface IKubeCtlTool extends ITool {

    List<String> getPods()

    List<String> getPods(String namespace)

    List<String> getPods(String namespace, Map<String, String> options)


}
