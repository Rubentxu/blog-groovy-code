package dev.rubentxu.jenkins.interfaces

import com.cloudbees.groovy.cps.NonCPS

interface PipelineComponent {
    @NonCPS
    void initialize(IConfigClient configClient)
}