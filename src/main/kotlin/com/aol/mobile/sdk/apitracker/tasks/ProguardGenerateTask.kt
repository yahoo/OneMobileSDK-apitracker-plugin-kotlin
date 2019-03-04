/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.apitracker.tasks

import com.aol.mobile.sdk.apitracker.dto.asTypeDescriptorList
import com.aol.mobile.sdk.apitracker.utils.Proguard
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ProguardGenerateTask : DefaultTask() {
    @InputFile
    lateinit var newManifestFile: File
    @OutputFile
    lateinit var proguardFile: File

    @Suppress("unused")
    @TaskAction
    fun generateProguard() {
        val newManifest = newManifestFile.readText()
                .asTypeDescriptorList()

        proguardFile.writeText(Proguard.generateRules(newManifest))
    }
}
