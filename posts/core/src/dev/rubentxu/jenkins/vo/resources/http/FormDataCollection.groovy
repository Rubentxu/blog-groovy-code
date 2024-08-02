package dev.rubentxu.jenkins.vo.resources.http

import com.cloudbees.groovy.cps.NonCPS

import java.nio.file.Path

class FormDataCollection {
    Map<String, Path> files
    Map<String, Object> data

    @NonCPS
    @Override
    String toString() {
        List<String> postData = []
        data.each { key, value ->
            postData.add("${key}=${value}")
        }

        files.each { key, path ->
            postData.add("${key}@${path}")
        }
        return postData.join(' ')

    }
}
