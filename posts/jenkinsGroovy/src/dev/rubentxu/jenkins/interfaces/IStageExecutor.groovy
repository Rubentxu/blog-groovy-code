package dev.rubentxu.jenkins.interfaces

interface IStageExecutor {

    def stage(String name, Closure body)

}
