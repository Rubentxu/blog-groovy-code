package dev.rubentxu.policies.model

import com.cloudbees.groovy.cps.NonCPS

class Metadata extends HashMap<String, Object> {

    @NonCPS
    @Override
    String toString() {
        return """           
                ${this.collect { k, v -> "$k: $v" }.join("\n")}            
        """
    }
}