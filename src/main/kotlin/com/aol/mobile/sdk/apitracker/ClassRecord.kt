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