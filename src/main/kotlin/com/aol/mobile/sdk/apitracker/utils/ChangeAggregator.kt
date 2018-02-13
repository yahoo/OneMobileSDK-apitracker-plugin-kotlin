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

object ChangeAggregator {
    fun process(old: List<TypeDescriptor>, new: List<TypeDescriptor>): List<ClassRecord> {
        val oldApi = (old - new)
        val newApi = (new - old).toMutableList()
        var result = emptyList<ClassRecord>()

        oldApi.forEach { oldTd ->
            val newIt = newApi.iterator()
            var hasNewEquivalent = false

            while (newIt.hasNext()) {
                val newTd = newIt.next()

                if (oldTd.isNear(newTd)) {
                    newIt.remove()
                    hasNewEquivalent = true
                    result += createModifiedClassRecord(oldTd, newTd)
                    break
                }
            }

            if (!hasNewEquivalent) result += oldTd.asRemovedClassRecord()
        }

        newApi.forEach { result += it.asNewClassRecord() }

        return result.sortedBy { it.ordinal }.toList()
    }

    private fun TypeDescriptor.asNewClassRecord() = ClassRecord.New(
            modifiers.map { modifier -> Modifier.New(modifier) },
            name,
            fields.map { prop -> PropertyRecord.New(prop.name, prop.type) },
            methods.map { method ->
                MethodRecord.New(method.returnType, method.name, method.params.map { param ->
                    PropertyRecord.New(param.name, param.type)
                })
            })


    private fun TypeDescriptor.asRemovedClassRecord() =
            ClassRecord.Removed(modifiers.map { modifier -> Modifier.Removed(modifier) }, name)


    private fun TypeDescriptor.isNear(another: TypeDescriptor): Boolean {
        val fieldAndMethodsCount = fields.size + methods.size

        val sameFieldsAndMethodsCount = fields.intersect(another.fields).size +
                methods.intersect(another.methods).size

        return name == another.name || sameFieldsAndMethodsCount.toFloat() / fieldAndMethodsCount > .3f
    }

    private fun createModifiedClassRecord(old: TypeDescriptor, new: TypeDescriptor): ClassRecord.Modified {
        val modifiers = (new.modifiers - old.modifiers).map { Modifier.New(it) } +
                (old.modifiers.intersect(new.modifiers)).map { Modifier.Untouched(it) } +
                (old.modifiers - new.modifiers).map { Modifier.Removed(it) }

        val name = if (old.name == new.name) Name.Untouched(new.name) else Name.Modified(old.name, new.name)

        val props = (new.fields - old.fields).map { field ->
            PropertyRecord.New(field.name, field.type)
        } + (old.fields - new.fields).map { field ->
            PropertyRecord.Removed(field.name, field.type)
        }

        val methods = (new.methods - old.methods).map { method ->
            MethodRecord.New(method.returnType, method.name, method.params.map { param ->
                PropertyRecord.New(param.name, param.type)
            })
        } + (old.methods - new.methods).map { method ->
            MethodRecord.Removed(method.returnType, method.name, method.params.map { param ->
                PropertyRecord.Removed(param.name, param.type)
            })
        }

        return ClassRecord.Modified(modifiers, name, props, methods)
    }
}