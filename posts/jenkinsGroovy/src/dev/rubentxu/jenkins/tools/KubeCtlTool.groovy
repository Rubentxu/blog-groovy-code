package dev.rubentxu.jenkins.tools

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.Steps
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.tools.interfaces.IKubeCtlTool


class KubeCtlTool extends Steps implements IKubeCtlTool {

    public static final String KUBECTL = "kubectl"
    private String kubeconfigPath
    private String defaultNamespace

    KubeCtlTool(IPipelineContext pipeline) {
        super(pipeline)
    }


    String execute(String taskName, List<String> options) {
        logger.debug("Running kubectl task '${taskName}' with options ${options}")
        steps.withCredentials([
                steps.file(credentialsId: kubeconfigPath, variable: 'KUBECONFIG')
        ]) {
            def result = steps.sh(script: "${KUBECTL} ${taskName} ${options.join(' ')}", returnStdout: true)
            logger.debug("Task '${taskName}' completed with output: ${result}")
            return result
        }
    }

    @Override
    List<String> getPods(String namespace = defaultNamespace, Map<String, String> options = ['-o', 'jsonpath={.items[*].metadata.name}']) {
        List<String> finalOptions = ['-n', namespace] + options
        String pods = execute('get pods', finalOptions, true)
        return pods ? pods.split() : []
    }


    @NonCPS
    @Override
    void configure(IConfigClient configClient) {
        kubeconfigPath = configClient.required('kubectl.kubeconfigPath', String.class)
        defaultNamespace = configClient.optional('kubectl.defaultNamespace', 'default')
    }

    @Override
    void initialize() {

    }
}
