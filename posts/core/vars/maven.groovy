import dev.rubentxu.jenkins.tools.MavenTool

import static dev.rubentxu.jenkins.cdi.ContextSinglenton.getContext

void resolveDependencies() {
    MavenTool maven = getContext().getService("maven")
    maven.resolveDependencies()
}

void test() {
    MavenTool maven = getContext().getService("maven")
    maven.test()
}

void build(List<String> options = []) {
    MavenTool maven = getContext().getService("maven")
    maven.build(options)
}

void publish() {
    MavenTool maven = getContext().getService("maven")
    maven.publish()
}

void calculateNextRelease() {
    MavenTool maven = getContext().getService("maven")
    maven.build(list)
}

void writeVersion(String overrideVersion) {
    MavenTool maven = getContext().getService("maven")
    maven.build(list)
}


