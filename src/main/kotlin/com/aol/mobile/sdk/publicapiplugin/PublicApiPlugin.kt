package com.aol.mobile.sdk.publicapiplugin

import com.android.build.gradle.internal.dsl.AnnotationProcessorOptions
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.BUILD_PATH_KEY
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.PUBLIC_API_FILENAME
import com.aol.mobile.sdk.publicapiplugin.tasks.ApiCheckTask
import com.aol.mobile.sdk.publicapiplugin.tasks.ProguardGenTask
import com.aol.mobile.sdk.publicapiplugin.utils.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import java.io.File

class PublicApiPlugin : Plugin<Project> {
    private lateinit var artifactDir: File

    private fun generatePublicApiArtifacts(project: Project) {
        project.config {
            android.libraryVariants.all { variant ->
                variant.apply {
                    (javaCompileOptions.annotationProcessorOptions as AnnotationProcessorOptions)
                            .argument(BUILD_PATH_KEY, mkdir("${publicApi.publicApiManifestDir.get()}/$dirName").absolutePath)

                    val manifest = file("${publicApi.publicApiManifestDir.get()}/$dirName/$PUBLIC_API_FILENAME")

                    val genProguardTask = task("genApiProguard${name.capitalize()}", ProguardGenTask::class) {
                        dependsOn(javaCompileProvider)
                        manifestFile = manifest
                        proguardFile = file("${publicApi.generatedProguardRulesDir.get()}/proguard-classes-${variant.name.toLowerCase()}.pro")
                    }

                    val previousManifest = file("${publicApi.previousPublicApiManifest.get()}")
                    val changesDir = "${rootProject.buildDir}/apiChanges"

                    val checkApiTask = task("checkApiChanges${name.capitalize()}", ApiCheckTask::class) {
                        dependsOn(javaCompileProvider)
                        oldManifestFile = if (previousManifest.exists()) previousManifest else File.createTempFile("    ", "")
                        oldManifestUrl = publicApi.previousPublicApiUrl.get()
                        newManifestFile = manifest
                        implicitNamespaces = publicApi.apiTrimNamespaces
                        changeReportFile = file("$changesDir/changes.md")
                        changeReportFileFromUrl = file("$changesDir/changesFromUrl.md")
                    }

                    tasks.apply {
                        (getByName("bundle${variant.name.capitalize()}Aar") as AbstractArchiveTask).from(manifest)
                        getByName("merge${variant.name.capitalize()}ConsumerProguardFiles").dependsOn(genProguardTask)
                        getByName("assemble").dependsOn(checkApiTask)
                    }
                }
            }
        }
    }

    override fun apply(project: Project) {
        artifactDir = project.mkdir("${project.buildDir}/publicApiPlugin/")

        project.extensions.create("publicApi", PublicApiExtension::class.java, project)

        project.config {
            dependencies.apply {
                add("kapt", "com.aol.one.publishers.android:api-collector:1.4")
                add("compileOnly", "com.aol.one.publishers.android:annotations:1.4")
            }
        }

        generatePublicApiArtifacts(project)
    }
}
