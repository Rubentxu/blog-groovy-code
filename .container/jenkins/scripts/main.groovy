import groovy.transform.Field
import jenkins.model.*

@Field Script utils = new GroovyShell().parse(new File('/var/jenkins_home/init.groovy.d/utils.groovy'))
@Field Script asdf = new GroovyShell().parse(new File('/var/jenkins_home/init.groovy.d/asdf/installTools.groovy'))

def installPlugins() {

//    Utils.shell("jenkins-plugin-cli -f /var/jenkins_home/init.groovy.d/plugins.txt -d /opt/jenkins/plugins")
    def installed = false
    def initialized = false
    File file = new File('/var/jenkins_home/init.groovy.d/plugins.txt')
    def plugins = file.readLines()
    utils.log("Plugin list: $plugins")
    def instance = jenkins.model.Jenkins.getInstance()
    def pm = instance.getPluginManager()
    def uc = instance.getUpdateCenter()

    plugins.each {
        if (!pm.getPlugin(it)) {
            utils.log("Looking UpdateCenter for $it")
            if (!initialized) {
                uc.updateAllSites()
                initialized = true
            }
            def plugin = uc.getPlugin(it)
            if (plugin) {
                utils.log("Installing $it")
                def installFuture = plugin.deploy()
                while (!installFuture.isDone()) {
                    utils.log("Waiting for plugin install: $it")
                    sleep(2000)
                }
                utils.log("Plugin installed: $it")
                installed = true
            } else {
                utils.log("Plugin not found in UpdateCenter: $it")
            }
        }
    }
    if (installed) {
        utils.log("Plugins installed, initializing a restart!")
        instance.save()
        instance.restart()
    }
}



assert utils != null: "utils script is null or empty"

//utils.log("Procedemos  a instalar los plugins 'plugins.installPlugins()'...........................................")
installPlugins()
utils.log("Completado la instalacion de los plugins...........................................")
utils.gitConfig()
utils.log("Procedemos a definir las variable de entorno ...........................................")
utils.defineGlobalEnvironmentVariables()
utils.log("Procedemos a descargar jenkins cli ...........................................")
utils.downloadJenkinsCli()

utils.log("Procedemos a instalar asdf-vm         ...........................................")
asdf.installAsdf()

//utils.log("Procedemos a instalar groovy  ...........................................")
//utils.installGroovy()

utils.log("Procedemos a instalar las tools con asdf  ...........................................")
asdf.installTools()

utils.log("Procedemos a clonar y crear el repositorio Bare de configuracion en local    ...........................................")
