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
                apiChanges.joinToString(separator = "\n\n-----\n\n") { it.renderToMd() }

        return implicitNamespaces.distinct().fold(markdown) { md, namespace ->
            md.replace("$namespace.", "")
        }
    }

    @JvmName("renderModifiersToMd")
    private fun List<Modifier>.renderToMd() = filter { it !is Modifier.Untouched }
            .joinToString(separator = " ") { it.renderToMd() }

    @JvmName("renderMethodsToMd")
    private fun List<MethodRecord>.renderToMd() = if (isEmpty()) String() else
        "#### Methods\n" + joinToString(separator = "\n> ", prefix = "\n> ", postfix = "\n\n") { it.renderToMd() }

    @JvmName("renderFieldsToMd")
    private fun List<PropertyRecord>.renderToMd() = if (isEmpty()) String() else
        "#### Fields\n" + joinToString(separator = "\n> ", prefix = "\n> ", postfix = "\n\n") { it.renderToMd() }

    private fun Modifier.renderToMd() = when (this) {
        is Modifier.New -> "*$name*"
        is Modifier.Removed -> "~~$name~~"
        is Modifier.Untouched -> name
    }

    private fun Name.renderToMd() = when (this) {
        is Name.Modified -> "~~$oldName~~`$newName`"
        is Name.Untouched -> value
    }

    private fun PropertyRecord.renderToMd() = when (this) {
        is PropertyRecord.New -> "*$name*:`$type`"
        is PropertyRecord.Removed -> "~~$name:$type~~"
    }

    private fun MethodRecord.renderToMd() = when (this) {
        is MethodRecord.New -> {
            val params = args.joinToString { it.renderToMd() }
            "$name($params):`$type`"
        }

        is MethodRecord.Removed -> {
            val params = args.joinToString { it.renderToMd() }
            "~~$name($params):$type~~"
        }
    }

    private fun ClassRecord.renderToMd() = when (this) {
        is ClassRecord.Removed -> "### ~~${modifiers.renderToMd()} $name~~\n"
        is ClassRecord.New -> {
            val modifiers = modifiers.renderToMd()
            val fields = properties.renderToMd()
            val methods = methods.renderToMd()

            """|### NEW: $modifiers $name
               |$fields
               |$methods""".trimMargin()
        }
        is ClassRecord.Modified -> {
            val modifiers = modifiers.renderToMd()
            val name = name.renderToMd()
            val fields = properties.renderToMd()
            val methods = methods.renderToMd()

            """|### $modifiers $name
               |$fields
               |$methods""".trimMargin()
        }
    }
}