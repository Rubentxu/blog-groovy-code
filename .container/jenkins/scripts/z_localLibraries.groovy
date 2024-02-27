import hudson.FilePath
import hudson.model.Run
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
import jenkins.plugins.git.GitSCMSource;

import java.nio.file.Path
import java.nio.file.Paths
import hudson.plugins.git.GitSCM

import groovy.transform.Field

@Field def utils = new GroovyShell().parse(new File('/var/jenkins_home/init.groovy.d/utils.groovy'))



public final class LocalLibraryRetriever extends LibraryRetriever {
    private final File localDirectory;

    public LocalLibraryRetriever() {
        this(Paths.get(System.getProperty("user.dir")));
    }

    public LocalLibraryRetriever(final Path path) {
        localDirectory = Objects.requireNonNull(path).toFile();
    }

    @Override
    public final void retrieve(final String name, final String version,
                               final boolean changelog, final FilePath target, final Run<?, ?> run,
                               final TaskListener listener) throws Exception {
        doRetrieve(target, listener)
    }

    @Override
    public final void retrieve(final String name, final String version,
                               final FilePath target, final Run<?, ?> run,
                               final TaskListener listener) throws Exception {
        doRetrieve(target, listener);
    }

    private void doRetrieve(final FilePath target, final TaskListener listener)
            throws IOException, InterruptedException {
        final FilePath localFilePath = new FilePath(localDirectory);
        listener.getLogger().format("Copying from local path %s to workspace path %s%s", localDirectory, target, System.lineSeparator());
        println("Copying from local path $localDirectory to workspace path  $target${System.lineSeparator()}")
        // Exclusion filter copied from SCMSourceRetriever
        localFilePath.copyRecursiveTo("src/**/*.groovy,vars/*.groovy,vars/*.txt,resources/", null, target);
    }
}

void configureGlobalLibraries(List<String> libraries) {
    List<LibraryConfiguration> localLibs = []
    libraries.each { libName ->
        String libId = "${libName}-lib"
        String ref = "master"

        final LibraryRetriever retriever = new SCMSourceRetriever(new GitSCMSource(libName, "/opt/repositories/remote/libraries/${libName}.git", "", "*/${ref}", "", true))
        final LibraryConfiguration localLibrary = new LibraryConfiguration(libId, retriever)
        localLibrary.implicit = false
        localLibrary.defaultVersion = ref
        localLibrary.allowVersionOverride = false
        localLibs.add(localLibrary)
        utils.log("Store Local library in /opt/libs/${libName} in /opt/repositories/remote/libraries/${libName}.git".toString())
    }
    GlobalLibraries.get().setLibraries(localLibs.asImmutable())
}

utils.log("Procedemos a instalar las librerias...........................................")
// Esta opción permite los repositorios git locales
hudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT = true


def excludedModules = System.getenv()['EXCLUDE_MODULES']?.split(',')?.collect { it.trim() }?:[0]
def libsDir = new File("/opt/libs/")

def configurableLibraries = []
libsDir.eachDir { module ->
    if (excludedModules.contains(module.name)) {
        utils.log("Ignore directory $module.name in modules libs")
        return
    }

    utils.log("Module name $module.name add or update to shared-library")
    utils.createLocalRepository(module.name,"/opt/libs/${module.name}",['src','vars','resources'],'libraries')
    utils.log("Configure Shared Library '$module.name' in Jenkins")
    configurableLibraries.add(module.name)
}
utils.createLocalRepository('jenkinsfiles','/usr/share/jenkins/ref',['jenkinsfiles'],'pipelines')

configureGlobalLibraries(configurableLibraries)
println "execute updated local libraries $configurableLibraries"
