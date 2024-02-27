package dev.rubentxu.jenkins.interfaces

import com.cloudbees.groovy.cps.NonCPS

interface PipelineComponent {
    @NonCPS
    void configure(IConfigClient configClient)

}