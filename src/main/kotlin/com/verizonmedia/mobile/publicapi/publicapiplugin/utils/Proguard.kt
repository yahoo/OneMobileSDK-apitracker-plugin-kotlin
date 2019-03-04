package com.verizonmedia.mobile.publicapi.publicapiplugin.utils

import com.verizonmedia.mobile.publicapi.publicapiplugin.dto.TypeDescriptor

object Proguard {
    fun generateRules(typeDescriptors: List<TypeDescriptor>) = typeDescriptors
            .joinToString(separator = "\n\n", postfix = "\n") { type ->
                """|-keep public class ${type.name} {
                   |    public protected *;
                   |}""".trimMargin()
            }
}