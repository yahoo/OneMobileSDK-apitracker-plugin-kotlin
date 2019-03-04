package com.aol.mobile.sdk.publicapiplugin.tasks

import com.aol.mobile.sdk.publicapiplugin.dto.asTypeDescriptorList
import com.aol.mobile.sdk.publicapiplugin.utils.Proguard
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import java.io.File

open class ProguardGenTask : DefaultTask() {
    @InputFile
    lateinit var manifestFile: File
    @OutputFile
    lateinit var proguardFile: File

    @TaskAction
    fun execute(inputs: IncrementalTaskInputs) {
        if (inputs.isIncremental)
            project.delete(proguardFile)

        inputs.outOfDate { change ->
            val manifest = change.file.readText().asTypeDescriptorList()
            proguardFile.writeText(Proguard.generateRules(manifest))
        }

        inputs.removed {
            if (proguardFile.exists()) project.delete(proguardFile)
        }
    }
}