package com.aol.mobile.sdk.publicapiplugin.tasks

import com.aol.mobile.sdk.publicapiplugin.dto.TypeDescriptor
import com.aol.mobile.sdk.publicapiplugin.dto.asTypeDescriptorList
import com.aol.mobile.sdk.publicapiplugin.utils.ChangeAggregator
import com.aol.mobile.sdk.publicapiplugin.utils.Markdown
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
    @InputFile
    lateinit var oldManifestFile: File
    @InputFile
    lateinit var newManifestFile: File
    @Input
    lateinit var oldManifestUrl: String
    @Input
    lateinit var implicitNamespaces: ListProperty<String>
    @OutputFile
    lateinit var changeReportFile: File
    @OutputFile
    lateinit var changeReportFileFromUrl: File

    @TaskAction
    fun comparePublicApi(inputs: IncrementalTaskInputs) {
        if (inputs.isIncremental) {
            project.delete(changeReportFile, changeReportFileFromUrl)
        }

        inputs.outOfDate {
            val oldManifestFileString = oldManifestFile.readText()
            val oldManifest = try {
                if (oldManifestFileString.isNotEmpty()) {
                    oldManifestFileString.asTypeDescriptorList()
                } else {
                    emptyList()
                }
            } catch (e: IOException) {
                project.delete(changeReportFile)
                emptyList<TypeDescriptor>()
            }

            val oldManifestFromUrl = try {
                URL(oldManifestUrl).readText().asTypeDescriptorList()
            } catch (e: Exception) {
                project.delete(changeReportFileFromUrl)
                emptyList<TypeDescriptor>()
            }

            val newManifest = try {
                newManifestFile.readText().asTypeDescriptorList()
            } catch (e: IOException) {
                project.delete(changeReportFile, changeReportFileFromUrl)
                return@outOfDate
            }

            if (oldManifest.isNotEmpty()) {
                val changeReport = ChangeAggregator.process(oldManifest, newManifest)
                project.file(changeReportFile)
                changeReportFile.writeText(Markdown.render(implicitNamespaces.get(), changeReport))
            }

            if (oldManifestFromUrl.isNotEmpty()) {
                val changeReportFromUrl = ChangeAggregator.process(oldManifestFromUrl, newManifest)
                project.file(changeReportFileFromUrl)
                changeReportFileFromUrl.writeText(Markdown.render(implicitNamespaces.get(), changeReportFromUrl))
            }
        }

        inputs.removed {
            if (changeReportFile.exists() && changeReportFileFromUrl.exists()) {
                project.delete(changeReportFile, changeReportFileFromUrl)
            }
        }
    }
}