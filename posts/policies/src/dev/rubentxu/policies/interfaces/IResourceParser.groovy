package dev.rubentxu.policies.interfaces


import java.nio.file.Path

interface IResourceParser<T extends StructuredResource> {

    T parse(Path resourcePath)

    String getType()
}