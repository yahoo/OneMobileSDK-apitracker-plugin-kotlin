/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.apitracker.utils

import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.PUBLIC_API_FILENAME
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.ExtensionContainer
import java.io.File

operator fun ConfigurationContainer.get(name: String): Configuration = getByName(name)

operator fun <T> ExtensionContainer.get(aClass: Class<T>): T = getByType(aClass)

val Project.manifestFile get() = File(buildDir, PUBLIC_API_FILENAME)

fun File.ensureParentExists() = with(parentFile) {
    mkdirs() || isDirectory || throw GradleException("Could not create $path")
}
