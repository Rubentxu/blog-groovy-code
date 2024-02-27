import hudson.*
import hudson.EnvVars
import hudson.model.*
import hudson.slaves.EnvironmentVariablesNodeProperty
import hudson.slaves.NodeProperty
import hudson.slaves.NodePropertyDescriptor
import hudson.util.DescribableList
import jenkins.*
import jenkins.model.*
import jenkins.model.Jenkins
import okhttp3.*

import javax.net.ssl.*
import java.security.cert.CertificateException
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import groovy.transform.Field

@Field def logsFile = "/var/jenkins_home/logs.txt"
@Field Logger logger = Logger.getLogger("testing")

def shell(String cmd) {
    def sout = new StringBuilder('')
    def serr = new StringBuilder('')
    logger.info("+ $cmd")
    ProcessBuilder pb = new ProcessBuilder(['sh', '-c', cmd])

    Process proc = pb.start()
    proc.consumeProcessOutput(sout, serr)
    proc.waitFor()
    logger.info("Procces shell execute with user ${System.getProperty("user.name")}")
    logger.info(sout.toString())
    println sout.toString()
    logger.severe(serr.toString())
}


def initialize() {
    FileHandler handler = new FileHandler(logsFile, 1024, 5, true)
    handler.setFormatter(new SimpleFormatter())
    logger.addHandler(handler)

}

def logWarn(String msg) {
    logger.warning(formatMessage(msg))
}

def logError(String msg) {
    logger.severe(formatMessage(msg))
}

def log(String msg) {
    logger.info(formatMessage(msg))
}

String formatMessage(String msg) {
    return "${new Date().getHours()}:${new Date().getMinutes()}:${new Date().getSeconds()} -> $msg".toString()
}

def gitConfig() {
    shell("""
        git config --global user.email "cicd@rubentxu.dev"
        git config --global user.name "Rubentxu"
    """)
}

def createLocalBareRepository(String name, String subDir) {
    if (!new File("/opt/repositories/remote/${subDir}/${name}.git/").exists()) {
        log("Create Local Bare Repository in /opt/repositories/remote/${subDir}/${name}.git\n")
        shell("ls -la /opt/repositories/remote/${subDir}/${name}.git || echo 'No existe el directorio /opt/repositories/remote/${subDir}/${name}.git'")
        shell("""
            mkdir -p /opt/repositories/remote/${subDir}/${name}.git && \
            git init --bare /opt/repositories/remote/${subDir}/${name}.git
		""")
    }
}

//    @CompileStatic
def createLocalRepository(String name, String pathSources, List<String> includes, String subDir) {
    createLocalBareRepository(name, subDir)
    String originsFiles = includes.collect { "-r ${pathSources}/${it}" }.join(' ')
    if (!new File("/opt/repositories/local/${subDir}/${name}").exists()) {
        log("Create Local Repository in /opt/repositories/local/${subDir}/${name}\n")

        shell("""
            git clone /opt/repositories/remote/${subDir}/${name}.git /opt/repositories/local/${subDir}/${name}
            cp ${originsFiles} /opt/repositories/local/${subDir}/${name}
            cd /opt/repositories/local/${subDir}/${name}
            git config  user.email "cicd@rubentxu.dev"
            git config  user.name "Rubentxu"
            git add .
            git commit -am "primer commit"
            git tag -a '1.0.0' -m 'tag inicial automatico'
            git push origin master
            cd ~
		""")
    } else {
        log("Update Local Repository /opt/repositories/local/${subDir}/${name}\n")
        shell("""
            cp -r ${originsFiles} /opt/repositories/local/${subDir}/${name}
            cd /opt/repositories/local/${subDir}/${name}
            git config user.email "cicd@rubentxu.dev"
            git config user.name "Rubentxu"
            [ \$(git status --porcelain | wc -l) -eq "0" ] || git add . && git commit -am "Commit date \$(date +%m-%d-%Y__%H:%M:%S)";
            git push origin master
            cd ~
		""")
    }
}


def getEnv(String key) {
    return System?.getenv()[key] ?: Jenkins?.instance?.getGlobalNodeProperties()[0]?.getEnvVars()[key]?: null

}


def createLocalBareRepositoryFromExternal(String name, String remoteSources, Boolean cloneCopy, String subDir) {

    if (!new File("/opt/repositories/remote/${subDir}/${name}.git/").exists()) {
//        String gitlabCredentialsId = getEnv('GITLAB_CREDENTIAL_ID')
//        String gitlabToken = getEnv('CLOUDBEES_CI_BOT_PERSONAL_TOKEN_GITLAB_TOKEN')
//        String jenkinsUrl = getEnv('JENKINS_REMOTE_URL') ?: 'https://localhost:8080'
//        String userRemoteJenkins = getEnv('JENKINS_REMOTE_ID')
//        String passwordRemoteJenkins = getEnv('JENKINS_REMOTE_API_TOKEN')
//        log("Se recoge el Id para las credenciales de Gitlab desde environment con nombre $gitlabCredentialsId")
//        log("Se recoge el userRemoteJenkins desde environment  $userRemoteJenkins")
//        log("Se recoge el jenkinsUrl desde environment  $jenkinsUrl")
//        log("Se recoge el passwordRemoteJenkins desde environment  ${passwordRemoteJenkins?.take(5)}....")
//        log("Se recoge el gitlabToken desde environment  ${gitlabToken?.take(5)}....")

        shell("""
               git clone --bare -c http.sslVerify=false https://${remoteSources} /opt/repositories/remote/${subDir}/${name}.git
            """)
//        if (gitlabToken) {
////            shell("""
////               git clone --bare -c http.sslVerify=false https://oauth2:${gitlabToken}@${remoteSources} /opt/repositories/remote/${subDir}/${name}.git
////            """)
//
//        }
//        if (!gitlabToken && jenkinsUrl && userRemoteJenkins && passwordRemoteJenkins) {
//            def resultJenkins = executeFindCredentialRemoteScript(jenkinsUrl, gitlabCredentialsId, userRemoteJenkins, passwordRemoteJenkins)
//
//            String authToken = new groovy.json.JsonSlurper().parseText(resultJenkins)[0].token.plainText
//
//            shell("""
//               git clone --bare -c http.sslVerify=false https://oauth2:${authToken}@${remoteSources} /opt/repositories/remote/${subDir}/${name}.git
//            """)
//        } else {
//            logError("No se encontro definidas las variables de entorno requeridas para consultar a Jenkins remoto.'JENKINS_REMOTE_URL,JENKINS_REMOTE_ID,JENKINS_REMOTE_API_TOKEN'")
//        }


    } else if (cloneCopy) {
        log("Update Local Repository /opt/repositories/local/${subDir}/${name}\n")
        shell("""
                    cd /opt/repositories/local/${subDir}/${name}
                    [ \$(git status --porcelain | wc -l) -eq "0" ] || git add . && git commit -am "Commit date \$(date +%m-%d-%Y__%H:%M:%S)"
                    git push origin master
                """)
    }
    if (cloneCopy && !new File("/opt/repositories/local/${subDir}/${name}").exists()) {
        log("Create Local Repository in /opt/repositories/local/${subDir}/${name}\n")

        shell("""
            git clone /opt/repositories/remote/${subDir}/${name}.git /opt/repositories/local/${subDir}/${name}
            git config --global user.email "cicd@rubentxu.dev"
            git config --global user.name "Rubentxu"
		""")
    }
}


String executeFindCredentialRemoteScript(String jenkinsUrl, String credentialsIds, String user, String password) {
    try {
        String url = "${jenkinsUrl}/scriptText"
        log("executeFindCredentialRemoteScript credentialsIds $credentialsIds")

        return doCallPost(url, findCredentialRemoteScript(credentialsIds), user, password)
    } catch (Exception ex) {
        println("Remote Script Jenkins, Got an error trying to get the top users!, $ex.message")
    }

}

private String doCallPost(String url, String script, String user, String password) {
    String auth = okhttp3.Credentials.basic(user, password)
    log("Do call post $url with user $user")
    OkHttpClient client = getUnsafeOkHttpClient()
    RequestBody formBody = new FormBody.Builder()
            .add("script", script)
            .build()
    Request request = new Request.Builder()
            .header("Authorization", auth)
            .url(url)
            .post(formBody)
            .build()

    Response response = client.newCall(request).execute()
    if (!response.isSuccessful()) {
        logError("Unexpected code " + response)
        String resutl = response.body().string()
        logError(resutl)
        return """[{"Error": "Peticion a Server Jenkins fallida", "code": "$response.code"}]"""

    }

    return response.body().string()

}

private OkHttpClient getUnsafeOkHttpClient() {
    try {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = [
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return [];
                    }
                }
        ];
        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        })
        OkHttpClient okHttpClient = builder.build();
        return okHttpClient;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

@Grab(group = 'com.squareup.okhttp3', module = 'okhttp', version = '4.9.3')
String findCredentialRemoteScript(String ids) {
    return """
        import groovy.json.JsonBuilder
        import java.nio.charset.StandardCharsets;
        def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
              com.cloudbees.plugins.credentials.Credentials.class
        )
        def result = []

        for (c in creds) {
          if('$ids'.contains(c.id)) {
            def credentialMap = [ id : c.id, type: c.class.simpleName, description: c.description, scope: c.scope]

            if (c.properties.username) {
              credentialMap.put('username', c.username)

            }
            if (c.properties.fileName) {
              credentialMap.put('fileName', c.fileName)

            }
            if (c.properties.password) {
              credentialMap.put('password', c.password.plainText.bytes.encodeBase64().toString())

            }
            if (c.properties.passphrase) {
               credentialMap.put('passphrase', c.passphrase.plainText.bytes.encodeBase64().toString())

            }
            if (c.properties.secret) {
               credentialMap.put('secret', c.secret.plainText.bytes.encodeBase64().toString())

            }
            if (c.properties.secretBytes) {
              credentialMap.put('secretBytes', new String(c.secretBytes.getPlainData(), StandardCharsets.UTF_8).bytes.encodeBase64().toString())

            }
            if (c.properties.privateKeySource) {
               credentialMap.put('privateKey', c.getPrivateKey().bytes.encodeBase64().toString())

            }
            if (c.properties.apiToken) {
              credentialMap.put('apiToken', c.apiToken.bytes.encodeBase64().toString())

            }
            if (c.properties.token) {
              credentialMap.put('token', c.token)

            }
            if (c.properties.keyStoreSource) {
              credentialMap.put('keyStoreSource', c.keyStoreSource.getKeyStoreBytes().encodeBase64().toString())

            }

             result.add(credentialMap)
          }

        }
        print new JsonBuilder(result).toPrettyString()

    """.toString()
}


def installAsdf() {
    log('Install asdf tool ')
    File file = new File('/var/jenkins_home/.asdf')
    if (!file.exists()) {
        shell('git clone https://github.com/asdf-vm/asdf.git /var/jenkins_home/.asdf --branch v0.10.2')
        shell('echo  "/var/jenkins_home/.asdf/asdf.sh" >> /var/jenkins_home/.bashrc')
        shell('mkdir -p /var/jenkins_home/.asdf-data')
        shell('chmod +x /var/jenkins_home/.asdf/asdf.sh')
    }
    log('End install asdf tool')
}


def installGroovy() {
    log('Install groovy tool ')
    if (!new File('/var/jenkins_home/.tool-versions').exists()) {
        shell("ln -s /home/rubentxu/.tool-versions /var/jenkins_home/.tool-versions")
    }
    shell("""
                    asdf plugin add groovy || true
                    asdf install groovy 2.5.14 || true
                    asdf global groovy 2.5.14 || true
                """)
    log('End install groovy tool')
}




def downloadJenkinsCli() {
    if (!new File('/var/jenkins_home/jenkins-cli.jar').exists()) {
        String jenkinsUrl = System.getenv()['JENKINS_REMOTE_URL'] ?: 'http://localhost:8080'
        shell("curl -k ${jenkinsUrl}/jnlpJars/jenkins-cli.jar --output /var/jenkins_home/jenkins-cli.jar")
    } else {
        logWarn("Jenkins cli ya se descargo anteriormente, saltamos este paso")
    }
}


def createGlobalEnvironmentVariables(String key, String value) {

    Jenkins instance = Jenkins.getInstance();

    DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalNodeProperties = instance.getGlobalNodeProperties();
    List<EnvironmentVariablesNodeProperty> envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class);

    EnvironmentVariablesNodeProperty newEnvVarsNodeProperty = null;
    EnvVars envVars = null;

    if (envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0) {
        newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
        globalNodeProperties.add(newEnvVarsNodeProperty);
        envVars = newEnvVarsNodeProperty.getEnvVars();
    } else {
        envVars = envVarsNodePropertyList.get(0).getEnvVars();
    }
    envVars.put(key, value)

}

def defineGlobalEnvironmentVariables() {
    File file = new File('/var/jenkins_home/.env')
    if (file.exists()) {
        file.eachLine {
            def data = it =~ /(\w+)=(.*)/
            if (data && data[0].size() > 2) {
                createGlobalEnvironmentVariables(data[0][1], data[0][2])
            }
        }
    }
    Jenkins.getInstance().save()
}


