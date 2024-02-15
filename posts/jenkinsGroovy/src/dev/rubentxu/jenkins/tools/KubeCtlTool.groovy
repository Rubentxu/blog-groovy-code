package dev.rubentxu.jenkins.tools

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.StepsExecutor
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.IPipeline
import dev.rubentxu.jenkins.tools.interfaces.IKubeCtlTool


class KubeCtlTool extends StepsExecutor implements IKubeCtlTool {

    public static final String KUBECTL = "kubectl"
    private String kubeconfig
    private String defaultNamespace

    KubeCtlTool(IPipeline pipeline) {
        super(pipeline)
        initialize(pipeline.getConfigClient())
    }

    @Override
    void executeTask(String taskName, List<String> options) {
        executeTask(taskName, options, false)
    }

    @Override
    String executeTask(String taskName, List<String> options, Boolean returnStdout) {
        logger.debug("Running kubectl task '${taskName}' with options ${options}")
        steps.withCredentials([
                steps.file(credentialsId: kubeconfig, variable: 'KUBECONFIG')
        ]) {
            def result = steps.sh(returnStdout: returnStdout, script: "${KUBECTL} ${taskName} ${options.join(' ')}")
            logger.debug("Task '${taskName}' completed with output: ${result}")
            return result
        }
    }

    @Override
    List<String> getPods(String namespace = defaultNamespace, Map<String, String> options = ['-o', 'jsonpath={.items[*].metadata.name}']) {
        List<String> finalOptions = ['-n', namespace] + options
        String pods = executeTask('get pods', finalOptions, true)
        return pods ? pods.split() : []
    }


    @NonCPS
    @Override
    void initialize(IConfigClient configClient) {
        kubeconfig = configClient.get('kubectl.kubeconfig')
        defaultNamespace = configClient.get('kubectl.defaultNamespace')
    }


}
