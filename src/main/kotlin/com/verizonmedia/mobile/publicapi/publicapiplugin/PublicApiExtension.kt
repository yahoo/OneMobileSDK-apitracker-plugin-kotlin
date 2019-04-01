package com.verizonmedia.mobile.publicapi.publicapiplugin

import org.gradle.api.provider.Property
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import java.io.File

open class PublicApiExtension(private val project: Project) {

    val publicApiManifestDir: Property<File> = project.objects.property(File::class.java).apply { set(File("${project.buildDir}/publicApiPlugin")) }

    val generatedProguardRulesDir: Property<File> = project.objects.property(File::class.java).apply { set(File("${project.buildDir}/publicApiPlugin")) }

    val apiTrimNamespaces: ListProperty<String> = project.objects.listProperty(String::class.java).apply { add("java.lang") }

    val changesDir: Property<File> = project.objects.property(File::class.java).apply { set(File(" ")) }

    val githubRepo: Property<String> = project.objects.property(String::class.java)

    val githubBranch: Property<String> = project.objects.property(String::class.java).apply { set("master") }

    val groupId: Property<String> = project.objects.property(String::class.java)

    val artifactId: Property<String> = project.objects.property(String::class.java)
}