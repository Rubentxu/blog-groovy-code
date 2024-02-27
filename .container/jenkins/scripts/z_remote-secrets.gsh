import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import hudson.util.Secret
import jenkins.model.Jenkins
import groovy.transform.Field
import org.apache.commons.fileupload.FileItem;
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*

@Field def utils = new GroovyShell().parse(new File('/var/jenkins_home/init.groovy.d/utils.groovy'))


def changeUserAndPassword(id, Map data) {
    def localCredentials = getLocalCredentials(com.cloudbees.plugins.credentials.common.StandardUsernameCredentials.class)
    def currentCredential = localCredentials.findResult { it.id == id ? it : null }
    def newCredential = new UsernamePasswordCredentialsImpl(
            CredentialsScope.GLOBAL,
            currentCredential.id,
            "${data.description} remote updated".toString(),
            data.username,
            new String(data.password.decodeBase64())
    )
    updateSecret(id, currentCredential, newCredential)

}

def changeSecret(String id, Map data) {
    List localCredentials = getLocalCredentials(StringCredentials.class)
    def currentCredential = localCredentials.findResult { it.id == id ? it : null }
    def newCredential = new StringCredentialsImpl(
            CredentialsScope.GLOBAL,
            currentCredential.id,
            "${data.description} remote updated".toString(),
            Secret.fromString(new String(data.secret.decodeBase64()))
    )
    updateSecret(id, currentCredential, newCredential)
}

def changeFileSecret(String id, Map data) {
    def localCredentials = getLocalCredentials()
    def currentCredential = localCredentials.findResult { it.id == id ? it : null }
    def newCredential =  new FileCredentialsImpl(
            CredentialsScope.GLOBAL,
            currentCredential.id,
            "${data.description} remote updated".toString(),
            data.fileName,
            SecretBytes.fromString(new String(data.secretBytes.decodeBase64()))
    )
    updateSecret(id, currentCredential, newCredential)

}

def changeSSHUserPrivateKey(String id, Map data) {
    def localCredentials = getLocalCredentials()
    def currentCredential = localCredentials.findResult { it.id == id ? it : null }
    def newCredential =  new BasicSSHUserPrivateKey(
            CredentialsScope.GLOBAL,
            currentCredential.id,
            data.username,
            new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(new String(data.privateKey.decodeBase64())),
            new String(data.passphrase.decodeBase64()),
            "${data.description} remote updated".toString()
    )
    updateSecret(id, currentCredential, newCredential)

}



def updateSecret(String id, currentCredential, newCredential) {
    if (currentCredential) {
        utils.log "found credential ${currentCredential.id} "
        def credentials_store = jenkins.model.Jenkins.instance.getExtensionList(
                'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
        )[0].getStore()

        def result = credentials_store.updateCredentials(
                com.cloudbees.plugins.credentials.domains.Domain.global(),
                currentCredential,
                newCredential
        )

        if (result) {
            utils.log "--> Secret changed for credential ${currentCredential.id}"
        } else {
            throw new RuntimeException("Failed to change secret for ${currentCredential.id}")
        }
    } else {
        throw new RuntimeException("No existing credential with ID ${id} :: ${currentCredential?.id}")
    }
}

def getLocalCredentials(Class clazzCredentials=com.cloudbees.plugins.credentials.Credentials.class) {
    return com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
            clazzCredentials
    )
}

def getSecretsFromRemoteJenkins() {
    def xmlFile = new File('/var/jenkins_home/credentials.xml').text

    utils.log xmlFile

    def matcherIds = xmlFile =~ /<id>(.*)<\/id>/
    def matcherTypes = xmlFile =~ /<[\.\w]+\.(.*Impl)/
    List credentialsMap = (0..<matcherTypes.count).collect { [id: matcherIds[it][1], type: matcherTypes[it][1]] }
    def idsString = credentialsMap.collect { it.id }.join(',')

    String jenkinsUrl = utils.getEnv('JENKINS_REMOTE_URL') ?: 'https://cloudbees.gcp.rubentxu.dev'
    String userRemoteJenkins = utils.getEnv('JENKINS_REMOTE_ID')
    String passwordRemoteJenkins = utils.getEnv('JENKINS_REMOTE_API_TOKEN')

    utils.log("Se recoge el Id para las credenciales de credentials.xml $idsString")
    utils.log("Usuario remoto $userRemoteJenkins")
    utils.log("Se recoge el passwordRemoteJenkins desde environment  ${passwordRemoteJenkins?.take(5)}....")

    def resultJenkins = utils.executeFindCredentialRemoteScript(jenkinsUrl, idsString, userRemoteJenkins, passwordRemoteJenkins)

    List resultCredentials = new groovy.json.JsonSlurper().parseText(resultJenkins)
    if(resultCredentials[0].Error) return
    def resultMap = resultCredentials.inject([:]) { map, c -> map << [(c.id): c] }

    utils.log "Resultado llamada remota exitosa"

    for (entry in credentialsMap) {
        switch (entry.type) {
            case 'UsernamePasswordCredentialsImpl':
                utils.log "id: $entry.id = type: $entry.type"
                def updateData = resultMap[entry.id]
                if (updateData) {
                    changeUserAndPassword(entry.id, updateData)
                }
                break
            case 'StringCredentialsImpl':
                utils.log "id: $entry.id = type: $entry.type"
                def updateData = resultMap[entry.id]
                if (updateData) {
                    changeSecret(entry.id, updateData)
                }
                break
            case 'FileCredentialsImpl':
                utils.log "id: $entry.id = type: $entry.type"
                def updateData = resultMap[entry.id]
                if (updateData) {
                    changeFileSecret(entry.id, updateData)
                }
                break
            case 'BasicSSHUserPrivateKey':
                utils.log "id: $entry.id = type: $entry.type"
                def updateData = resultMap[entry.id]
                if (updateData) {
                    changeSSHUserPrivateKey(entry.id, updateData)
                }
                break
            case 'CertificateCredentialsImpl':
                utils.log "id: $entry.id = type: $entry.type"
                def updateData = resultMap[entry.id]
                if (updateData) {
                     utils.logError("Updated CertificateCredentialsImpl Sin implementar...... ")
//                    changeCertificateCredentials(entry.id, updateData)
                }
                break
        }

    }
}

if (this?.args) {
    "${this?.args.head()}"(*this?.args.tail())
}
