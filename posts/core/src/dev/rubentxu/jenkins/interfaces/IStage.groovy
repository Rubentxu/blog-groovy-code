package dev.rubentxu.jenkins.interfaces

interface IStage extends PipelineComponent {
    def stage(String name, Closure body)
}
