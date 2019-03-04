package com.verizonmedia.mobile.publicapi.publicapiplugin.dto

sealed class ClassRecord(val ordinal: Int) {
    class Removed(val modifiers: List<Modifier.Removed>, val name: String) : ClassRecord(2)

    class Modified(val modifiers: List<Modifier>, val name: Name,
                   val properties: List<PropertyRecord>,
                   val methods: List<MethodRecord>) : ClassRecord(1)

    class New(val modifiers: List<Modifier.New>, val name: String,
              val properties: List<PropertyRecord.New>,
              val methods: List<MethodRecord.New>) : ClassRecord(0)
}

sealed class MethodRecord {
    class New(val type: String, val name: String, val args: List<PropertyRecord.New>) : MethodRecord()

    class Removed(val type: String, val name: String, val args: List<PropertyRecord.Removed>) : MethodRecord()
}

sealed class PropertyRecord {
    class New(val name: String, val type: String) : PropertyRecord()

    class Removed(val name: String, val type: String) : PropertyRecord()
}

sealed class Name {
    class Modified(val oldName: String, val newName: String) : Name()

    class Untouched(val value: String) : Name()
}

sealed class Modifier {
    class New(val name: String) : Modifier()

    class Removed(val name: String) : Modifier()

    class Untouched(val name: String) : Modifier()
}