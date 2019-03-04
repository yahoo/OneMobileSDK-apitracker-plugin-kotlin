/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.verizonmedia.mobile.publicapi.cilib.utils

import groovy.lang.Closure
import org.gradle.internal.Cast
import java.io.File
import java.util.concurrent.TimeUnit


fun String.execute(workingDir: File): Process = ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

val Process.text get() = inputStream.bufferedReader().readText().trim()

fun Process.wait(): Int {
    waitFor(60, TimeUnit.SECONDS)
    return exitValue()
}

fun <T : Any> Any.delegateClosureOf(action: T.() -> Unit) = object : Closure<T>(this, this) {
    @Suppress("unused") // to be called dynamically by Groovy
    fun doCall() = (Cast.uncheckedCast<T>(delegate))?.action()
}

fun <T : Any, V : Any> Any.closureOf(action: T.() -> V?) = object : Closure<V?>(this, this) {
    @Suppress("unused") // to be called dynamically by Groovy
    fun doCall(it: T): V? = it.action()
}