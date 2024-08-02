package dev.rubentxu.policies.interfaces


interface DataModel extends Cloneable, Serializable {

    Map<String, Object> toMap()

}