package dev.rubentxu.jenkins.interfaces

interface ITool extends PipelineComponent{

    String execute(String taskName, List<String> options)

}
