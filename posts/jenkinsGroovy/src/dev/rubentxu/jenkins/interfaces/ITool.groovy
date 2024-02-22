package dev.rubentxu.jenkins.interfaces

interface ITool {

    void executeTask(String taskName, List<String> options)

    String executeTask(String taskName, List<String> options, Boolean returnStdout)
}
