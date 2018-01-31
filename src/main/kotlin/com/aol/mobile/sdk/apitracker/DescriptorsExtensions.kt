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

package com.aol.mobile.sdk.apitracker

import com.aol.mobile.sdk.apitracker.UD.Kind.*
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson

internal fun String.asTypeDescriptorList(): List<TypeDescriptor> = Gson().fromJson(this)

private fun TypeDescriptor.asUdList() =
        UD(modifiers.toList(), null, name, TYPE, emptyList())

private fun VariableDescriptor.asUdList(typeName: String) =
        UD(modifiers.toList(), type, typeName + "." + this.name, FIELD, emptyList())

private fun MethodDescriptor.asUdList(typeName: String) =
        UD(modifiers.toList(), returnType, typeName + "." + this.name, METHOD, params.map { it.type })

internal fun TypeDescriptor.toUdList() =
        listOf(asUdList()) + fields.map { it.asUdList(name) } + methods.map { it.asUdList(name) }

internal fun List<TypeDescriptor>.asUdList() = map { it.toUdList() }.flatten()
