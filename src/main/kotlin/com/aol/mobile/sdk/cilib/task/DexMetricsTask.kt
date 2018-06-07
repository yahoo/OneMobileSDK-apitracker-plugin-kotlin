/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.cilib.task

import com.jakewharton.dex.DexParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

open class DexMetricsTask : DefaultTask() {
    @InputFile
    lateinit var aarFile: File
    @OutputFile
    lateinit var metricsReportFile: File

    @TaskAction
    fun measureAar(inputs: IncrementalTaskInputs) {
        if (inputs.isIncremental)
            project.delete(metricsReportFile)

        inputs.outOfDate { change ->
            val aarFile = change.file
            val parser = DexParser.fromFile(aarFile)
            val methodsCount = parser.listMethods().size
            val fieldsCount = parser.listFields().size

            metricsReportFile.writeText("# Methods count: $methodsCount Fields count: $fieldsCount")
        }

        inputs.removed {
            if (metricsReportFile.exists()) project.delete(metricsReportFile)
        }
    }
}
