/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.apitracker.tasks

import com.aol.mobile.sdk.apitracker.dto.TypeDescriptor
import com.aol.mobile.sdk.apitracker.dto.asTypeDescriptorList
import com.aol.mobile.sdk.apitracker.utils.ChangeAggregator
import com.aol.mobile.sdk.apitracker.utils.Markdown
import com.aol.mobile.sdk.apitracker.utils.ensureParentExists
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL

open class ApiCompareTask : DefaultTask() {
    @Input
    lateinit var oldManifestUrl: String
    @InputFile
    lateinit var newManifestFile: File
    @Input
    var implicitNamespaces = listOf("java.lang")
    @OutputFile
    lateinit var changeReportFile: File

    @TaskAction
    fun comparePublicApi() {

        val oldManifest: List<TypeDescriptor>
        try {
            oldManifest = URL(oldManifestUrl).readText().asTypeDescriptorList()
        } catch (e: Exception) {
            project.delete(changeReportFile)
            return
        }

        val newManifest = newManifestFile.readText().asTypeDescriptorList()

        val changeReport = ChangeAggregator.process(oldManifest, newManifest)

        changeReportFile.ensureParentExists()
        changeReportFile.writeText(Markdown.render(implicitNamespaces, changeReport))
    }
}
