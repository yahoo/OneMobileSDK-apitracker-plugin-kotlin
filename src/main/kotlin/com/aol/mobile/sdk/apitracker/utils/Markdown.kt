/*
 * Copyright (c) 2018. Oath.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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