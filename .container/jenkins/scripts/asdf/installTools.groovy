import groovy.transform.Field

@Field Script utils = new GroovyShell().parse(new File('/var/jenkins_home/init.groovy.d/utils.groovy'))

def installPlugins() {
    File file = new File('/var/jenkins_home/init.groovy.d/.tool-plugins')
    if (file.exists()) {
        file.eachLine {
            def data = it =~ /([\S]*)[\s]+([\S]*)/
            if (data[0].size() > 2) {
                def result = utils.shell("asdf plugin add ${data[0][1]} ${data[0][2]} || true")
                utils.log("Plugin add ${data[0][1]} ${data[0][2]}")
                utils.log(result)

            }
        }
    }
    utils.log('End install plugins')
}

def installTools() {
    installPlugins()
    File file = new File('/var/jenkins_home/init.groovy.d/.tool-install')
    if (file.exists()) {
        file.eachLine {
            def data = it =~ /([\S]*)[\s]+([\S]*)/
            if (data[0].size() > 2) {
                utils.shell("asdf install ${data[0][1]} ${data[0][2]} || true")
                utils.shell("asdf global ${data[0][1]} ${data[0][2]} || true")
                utils.log("Tool install ${data[0][1]} ${data[0][2]}")
            }
        }
    }
    utils.log('End install tools')
}

installTools()