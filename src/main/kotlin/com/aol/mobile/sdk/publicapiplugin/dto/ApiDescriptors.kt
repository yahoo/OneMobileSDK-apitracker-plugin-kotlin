package com.aol.mobile.sdk.publicapiplugin.dto

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson

data class TypeDescriptor(val modifiers: Collection<String>, val name: String,
                          val fields: Set<VariableDescriptor>, val methods: Set<MethodDescriptor>)

data class VariableDescriptor(val modifiers: Collection<String>, val name: String, val type: String)

data class MethodDescriptor(val modifiers: Collection<String>, val name: String,
                            val returnType: String, val params: List<VariableDescriptor>)

internal fun String.asTypeDescriptorList(): List<TypeDescriptor> = Gson().fromJson(this)