package dev.rubentxu.jenkins.vo.resources.http

import com.cloudbees.groovy.cps.NonCPS
import groovy.json.JsonSlurperClassic


class HttpResponse<T> {
    Integer statusCode
    Map headers
    T body

    @NonCPS
    @Override
    String toString() {
        return "status_code: ${statusCode}, response: ${body}"
    }

    @NonCPS
    Object parseJsonBody() {
        return new JsonSlurperClassic().parseText(body as String)
    }

    Object parseJsonBody(steps) {
        return steps.readJSON(text: body as String)
    }
}
