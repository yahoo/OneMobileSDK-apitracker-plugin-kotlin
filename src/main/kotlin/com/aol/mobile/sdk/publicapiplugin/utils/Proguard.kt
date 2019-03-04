package com.aol.mobile.sdk.publicapiplugin.utils

import com.aol.mobile.sdk.publicapiplugin.dto.TypeDescriptor

object Proguard {
    fun generateRules(typeDescriptors: List<TypeDescriptor>) = typeDescriptors
            .joinToString(separator = "\n\n", postfix = "\n") { type ->
                """|-keep public class ${type.name} {
                   |    public protected *;
                   |}""".trimMargin()
            }
}