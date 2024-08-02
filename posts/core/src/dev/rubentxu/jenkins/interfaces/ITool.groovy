package dev.rubentxu.jenkins.interfaces

interface ITool extends IService {

    String execute(String taskName, List<String> options)

}
