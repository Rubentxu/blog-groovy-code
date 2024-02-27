#!groovy


import groovy.transform.Field
import groovy.text.GStringTemplateEngine
import java.util.regex.Matcher
import jenkins.model.*
import com.cloudbees.hudson.plugins.folder.*

@Field def utils = new GroovyShell().parse(new File('/var/jenkins_home/init.groovy.d/utils.groovy'))


Map renderTemplate(String template, Map model) {
    List missingProperties = checkTemplate(template, model)

    if (missingProperties.size() > 0) {
        return [isError: true, body: missingProperties]
    } else {
        def engine = new GStringTemplateEngine()
        def writer = new StringWriter()
        engine.createTemplate(template).make(model).writeTo(writer)
        return [isError: false, body: writer.toString()]
    }
}

def checkTemplate(String template, Map model) {
    def engine = new GStringTemplateEngine()
    def missingProperties = []
    template.eachLine { line, count ->
        Matcher prop = line =~ /\$\{?([\w\.\s_]*)\}?/
        if (prop.count > 0) {
            try {
                String lineResult = (prop[0] as ArrayList)[0]
                engine.createTemplate(lineResult).make(model).toString()
            } catch (Exception ex) {
                missingProperties.add("Render template error. Clave '${(prop[0] as ArrayList)[1]}'\n $ex.message")
            }
        }
    }
    return missingProperties
}


def createJob(String name, String domain, String jenkinsFilePath, String jenkinsFileRepository='/opt/repositories/remote/pipelines/jenkinsfiles.git', folder) {

    String jekinsFileContent = new File('/usr/share/jenkins/ref/template/jobTemplate.xml').text
    if(name.contains('preview-deploy-pipeline')) {
        jekinsFileContent = new File('/usr/share/jenkins/ref/template/jobTemplate-preview-deploy-pipeline.xml').text
    }

    Map binding = [
            displayName: name,
            remoteRepository: "/opt/repositories/remote/projects/${domain}/${name.replace('-preview-deploy-pipeline', '')}.git",
            jenkinsFilePath: jenkinsFilePath,
            jenkinsFileRepository: jenkinsFileRepository,
            sourceClass: 'jenkins.branch.MultiBranchProject$BranchSourceList'
    ]
   println "Binding template $binding"
    utils.log("Binding template $binding")
    Map renderResult = renderTemplate(jekinsFileContent, binding)
    utils.log "Render $renderResult.body"
    if (renderResult.isError) {
        utils.logError renderResult.body.join('\n')
    } else {
        utils.log("Creamos Job con nombre $name")
        def xmlStream = new ByteArrayInputStream(renderResult.body.getBytes("UTF-8") )
        if(folder) {
            def job = folder.getItem(name)
            if (job != null) {
                folder.remove(job)
            }
            def project = folder.createProjectFromXML(name, xmlStream)
            utils.log("Se completo la creación del Job con nombre $name url ${project.getUrl()}")
        } else {
            def project = Jenkins.instance.createProjectFromXML(name, xmlStream)
            utils.log("Se completo la creación del Job con nombre $name url ${project.getUrl()}")
        }

    }
}

def cloneRemoteRepositoryAndCreateJob(String remoteRepository, String jenkinsFilePath, String previewDeploy='false') {
    println "Into  cloneRemoteRepositoryAndCreateJob function with args $remoteRepository ${jenkinsFilePath} ${previewDeploy}"

    def data = remoteRepository =~ /.*\\/(.*)\\/([\w-]+)\.git/
    if (data[0].size() > 2) {
        String domain= data[0][1]
        String name = data[0][2]
        utils.createLocalBareRepositoryFromExternal("${domain}/${name}", remoteRepository, true,'projects')
        println("Clonamos el repositorio $remoteRepository")
        folder =recursiveCreateFolder(domain.tokenize('/'),null)

        createJob(name, domain, jenkinsFilePath, folder)
        println("Job $name creado")
        println("Job $name Domain $domain JenkinsFilePath $jenkinsFilePath Folder $folder")
        if (previewDeploy=='true') {
            createJob("${name}-preview-deploy-pipeline", domain, "${jenkinsFilePath}-preview-deploy-pipeline", folder)
        }

    }

}



def recursiveCreateFolder(List domain, folder) {
    def folderName= domain[0]
    println "FolderName $folderName"
    if (folder == null) {
        if(Jenkins.instance.getItem(folderName)==null) {
            folder = Jenkins.instance.createProject(Folder.class, folderName)
        } else {
            folder = Jenkins.instance.getItem(folderName)
        }
    } else if(folder.getItem(folderName) == null){
        folder = folder.createProject(Folder.class, folderName)
    } else {
        folder = folder.getItem(folderName)
    }

    if(domain.size()==1) {
        return folder
    }
    return recursiveCreateFolder(domain.tail(), folder)

}

if(this?.args) {
    "${this?.args.head()}"( *this?.args.tail() )
}


