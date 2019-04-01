package com.verizonmedia.mobile.publicapi.publicapiplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

open class CopyManifestTask : DefaultTask() {
    @InputFile
    lateinit var manifestFile: File
    @OutputFile
    lateinit var copiedManifestFile: File

    @TaskAction
    fun execute(inputs: IncrementalTaskInputs) {
        if (inputs.isIncremental) {
            project.delete(copiedManifestFile)
        }

        inputs.outOfDate {
            val from = manifestFile.toPath()
            val to = copiedManifestFile.toPath()
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
        }

        inputs.removed {
            project.delete(copiedManifestFile)
        }
    }
}