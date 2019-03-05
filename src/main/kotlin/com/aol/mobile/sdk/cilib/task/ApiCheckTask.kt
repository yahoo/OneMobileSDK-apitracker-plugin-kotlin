/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.cilib.task

import com.aol.mobile.sdk.apitracker.dto.asTypeDescriptorList
import com.aol.mobile.sdk.apitracker.utils.ChangeAggregator
import com.aol.mobile.sdk.apitracker.utils.Markdown
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File
import java.io.IOException
import java.net.URL

open class ApiCheckTask : DefaultTask() {
    @Input
    lateinit var oldManifestUrl: String
    @InputFile
    lateinit var newManifestFile: File
    @Input
    lateinit var implicitNamespaces: ListProperty<String>
    @OutputFile
    lateinit var changeReportFile: File

    @TaskAction
    fun comparePublicApi(inputs: IncrementalTaskInputs) {
        if (inputs.isIncremental)
            project.delete(changeReportFile)

        inputs.outOfDate { change ->
            val oldManifest = try {
                URL(oldManifestUrl).readText().asTypeDescriptorList()
            } catch (e: Exception) {
                project.delete(changeReportFile)
                return@outOfDate
            }

            val newManifest = try {
                change.file.readText().asTypeDescriptorList()
            } catch (e: IOException) {
                project.delete(changeReportFile)
                return@outOfDate
            }

            val changeReport = ChangeAggregator.process(oldManifest, newManifest)
            if (changeReport.isEmpty()) {
                project.delete(changeReportFile)
            } else {
                project.file(changeReportFile)
                changeReportFile.writeText(Markdown.render(implicitNamespaces.get(), changeReport))
            }
        }

        inputs.removed {
            if (changeReportFile.exists()) project.delete(changeReportFile)
        }
    }
}
