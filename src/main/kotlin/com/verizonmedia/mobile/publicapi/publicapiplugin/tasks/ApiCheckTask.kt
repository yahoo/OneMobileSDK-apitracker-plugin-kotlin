package com.verizonmedia.mobile.publicapi.publicapiplugin.tasks

import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.PUBLIC_API_FILENAME
import com.verizonmedia.mobile.publicapi.publicapiplugin.dto.asTypeDescriptorList
import com.verizonmedia.mobile.publicapi.publicapiplugin.utils.ChangeAggregator
import com.verizonmedia.mobile.publicapi.publicapiplugin.utils.Markdown
import com.verizonmedia.mobile.publicapi.publicapiplugin.utils.publicApi
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File
import java.io.IOException

open class ApiCheckTask : DefaultTask() {
    @Input
    lateinit var variantDirName: String
    @InputFile
    lateinit var newManifestFile: File
    @Input
    lateinit var implicitNamespaces: ListProperty<String>
    @OutputFile
    lateinit var changeReportFile: File

    @TaskAction
    fun comparePublicApi(inputs: IncrementalTaskInputs) {
        if (inputs.isIncremental) {
            project.delete(changeReportFile)
        }

        inputs.outOfDate {
            val publicApi = project.publicApi
            project.mkdir("${publicApi.changesDir.get()}/${publicApi.artifactId.get()}/$variantDirName")

            val oldManifestFile = project.file("${publicApi.changesDir.get()}/${publicApi.artifactId.get()}/$variantDirName/$PUBLIC_API_FILENAME").apply { createNewFile() }
            val oldManifestString = oldManifestFile.readText()
            val oldManifest = try {
                if (oldManifestString.isNotEmpty()) {
                    oldManifestString.asTypeDescriptorList()
                } else {
                    return@outOfDate
                }
            } catch (e: IOException) {
                project.delete(changeReportFile)
                return@outOfDate
            }

            val newManifest = try {
                newManifestFile.readText().asTypeDescriptorList()
            } catch (e: IOException) {
                project.delete(changeReportFile)
                return@outOfDate
            }

            val noChangesDirString = "${project.publicApi.changesDir.get()}/${project.publicApi.artifactId.get()}/noChanges/"

            if (oldManifest.isEmpty()) {
                project.mkdir(noChangesDirString)
            } else {
                val changeReport = ChangeAggregator.process(oldManifest, newManifest)
                project.file(changeReportFile)
                changeReportFile.writeText(Markdown.render(implicitNamespaces.get(), changeReport))
                project.delete(noChangesDirString)
            }
        }

        inputs.removed {
            if (changeReportFile.exists()) {
                project.delete(changeReportFile)
            }
        }
    }
}