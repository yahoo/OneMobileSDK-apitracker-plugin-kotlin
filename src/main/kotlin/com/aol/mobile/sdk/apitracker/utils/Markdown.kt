/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.apitracker.utils

import com.aol.mobile.sdk.apitracker.dto.*


object Markdown {
    fun render(implicitNamespaces: List<String> = listOf("java.lang"), apiChanges: List<ClassRecord>): String {
        val markdown = "# Public API changes\n" +
                apiChanges.sortedBy {
                    when (it) {
                        is ClassRecord.Removed -> it.name
                        is ClassRecord.New -> it.name
                        is ClassRecord.Modified -> when (it.name) {
                            is Name.Modified -> it.name.oldName
                            is Name.Untouched -> it.name.value
                        }
                    }
                }.joinToString(separator = "\n\n-----\n\n") { it.renderToMd() }

        return implicitNamespaces.distinct().fold(markdown) { md, namespace ->
            md.replace("$namespace.", "")
        }
    }

    private val List<Modifier>.modsMd
        get() = filter { it !is Modifier.Untouched }
                .joinToString(separator = " ") { it.md }

    private val List<MethodRecord>.methodsMd
        get() = if (isEmpty()) String() else
            "#### Methods\n" + joinToString(separator = "\n\n> ", prefix = "\n\n> ", postfix = "\n\n") { it.md }

    private val List<PropertyRecord>.propsMd
        get() = if (isEmpty()) String() else
            "#### Fields\n" + joinToString(separator = "\n\n> ", prefix = "\n\n> ", postfix = "\n\n") { it.md }

    private val Modifier.md
        get() = when (this) {
            is Modifier.New -> "*$name*"
            is Modifier.Removed -> name
            is Modifier.Untouched -> name
        }

    private val Name.md
        get() = when (this) {
            is Name.Modified -> "~~$oldName~~ -> *$newName*"
            is Name.Untouched -> value
        }

    private val PropertyRecord.md
        get() = when (this) {
            is PropertyRecord.New -> "*$name*: **$type**"
            is PropertyRecord.Removed -> "~~$name: $type~~"
        }

    private val PropertyRecord.rawMd
        get() = when (this) {
            is PropertyRecord.New -> "$name: $type"
            is PropertyRecord.Removed -> "$name: $type"
        }

    private val MethodRecord.New.argsMd
        get() = args.joinToString { it.md }

    private val MethodRecord.Removed.argsMd
        get() = args.joinToString { it.rawMd }

    private val MethodRecord.md
        get() = when (this) {
            is MethodRecord.New -> "$name($argsMd): **$type**"
            is MethodRecord.Removed -> "~~$name($argsMd): $type~~"
        }

    private fun ClassRecord.renderToMd() = when (this) {
        is ClassRecord.Removed -> "### REMOVED: ~~$name~~\n"

        is ClassRecord.New -> """
                |### NEW: ${modifiers.modsMd} $name
                |${properties.propsMd}
                |${methods.methodsMd}""".trimMargin()

        is ClassRecord.Modified -> """
                |### CHANGED: ${modifiers.modsMd} ${name.md}
                |${properties.propsMd}
                |${methods.methodsMd}""".trimMargin()
    }
}