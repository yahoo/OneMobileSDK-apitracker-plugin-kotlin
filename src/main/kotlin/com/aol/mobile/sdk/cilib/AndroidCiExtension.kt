/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.cilib

import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import kotlin.String


open class AndroidCiExtension(val project: Project) {
    /**
     * GitHub repo for publishing, must be in format `organization/repository`
     */
    val githubRepo: Property<String> = project.objects.property(String::class.java)

    /**
     * Branch for publishing, default is `maven`
     */
    val githubBranch: Property<String> = project.objects.property(String::class.java).apply { set("maven") }

    /**
     * Maven group id of library
     */
    val groupId: Property<String> = project.objects.property(String::class.java)

    /**
     * Maven artifact id of library, if build has several flavors artifact id will have
     * form `artifactid-flavourname`. Flavours with name 'full' or `default` will have this
     * artifact id without any additions
     */
    val artifactId: Property<String> = project.objects.property(String::class.java)

    /**
     * Namespaces that should be trimmed inside api change report
     */
    val apiTrimNamespaces: ListProperty<String> = project.objects.listProperty(String::class.java).apply { add("java.lang") }

    /**
     * Maven group id in form or uri path
     */
    val groupPath: String get() = groupId.get().replace('.', '/')

    /**
     * Gets artifact id for specified @see LibraryVariant
     */
    fun artifactId(variant: LibraryVariant): String = when (variant.flavorName) {
        null, "", "full", "default" -> artifactId.get()
        else -> "${artifactId.get()}-${variant.flavorName.toLowerCase()}"
    }
}


