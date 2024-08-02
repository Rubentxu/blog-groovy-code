package dev.rubentxu.policies.parsers

import dev.rubentxu.policies.interfaces.IResourceParser
import dev.rubentxu.policies.interfaces.StructuredResource
import dev.rubentxu.policies.model.ValidationPolicy

import java.nio.file.Path


class YamlRulesParser implements IResourceParser {

    private Script steps

    YamlRulesParser(Script steps) {
        this.steps = steps
    }


    @Override
    StructuredResource parse(Path resourcePath) {

        assert steps.fileExists(file: resourcePath.toString()): "Rules File Yaml not found in path: ${resourcePath.toString()}"
        Map data = steps.readYaml(file: resourcePath.toString(), encoding: 'UTF-8') as Map

        if (!data.kind || data.kind != 'ValidationPolicy') {
            throw new IllegalArgumentException("Kind not found in the Yaml file")
        }

        ValidationPolicy policy = ValidationPolicy.fromMap(data)
        return policy
    }

    @Override
    String getType() {
        return 'YamlResource'
    }
}