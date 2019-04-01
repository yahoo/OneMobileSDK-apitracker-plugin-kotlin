package com.verizonmedia.mobile.publicapi.publicapiplugin.utils

import java.io.File
import java.util.concurrent.TimeUnit

fun Process.wait(): Int {
    waitFor(60, TimeUnit.SECONDS)
    return exitValue()
}

fun String.execute(workingDir: File): Process = ProcessBuilder(*split("\\s".toRegex()).toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

val Process.text get() = inputStream.bufferedReader().readText().trim()