/*
 * Copyright (c) 2018. Oath.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.aol.mobile.sdk.apitracker

import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.BUILD_PATH_KEY
import com.aol.mobile.sdk.apitracker.tasks.ApiCompareTask
import com.aol.mobile.sdk.apitracker.tasks.ProguardGenerateTask
import com.aol.mobile.sdk.apitracker.utils.get
import com.aol.mobile.sdk.apitracker.utils.manifestFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import java.io.File

class TrackerPlugin : Plugin<Project> {
    companion object {
        const val API_COLLECTOR_VERSION = "1.3-SNAPSHOT"
        const val API_ANNOTATIONS_VERSION = "1.3"

        const val PUBLIC_API_CLASSIFIER = "pubapi"
        const val API_TRACKER_EXT = "apiTracker"
        const val KAPT_PLUGIN = "kotlin-kapt"

        const val API_COLLECTOR_REF = "com.aol.one.publishers.android:api-collector:$API_COLLECTOR_VERSION"
        const val ANNOTATIONS_REF = "com.aol.one.publishers.android:annotations:$API_ANNOTATIONS_VERSION"
    }

    open class Extension {
        var compareVersion: String = "1.0"
        var reportFile = File("API CHANGELOG.md")
        var implicitNamespaces = listOf("java.lang")
    }

    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(KAPT_PLUGIN)

            with(extensions) {
                getByType(KaptExtension::class.java).arguments {
                    arg(BUILD_PATH_KEY, buildDir.absolutePath)
                }

                create(API_TRACKER_EXT, Extension::class.java)
            }

            with(dependencies) {
                add("kapt", API_COLLECTOR_REF)
                add("compileOnly", ANNOTATIONS_REF)
            }

            afterEvaluate {
                val ext = extensions[Extension::class.java]
                val artifactId = properties["archivesBaseName"]
                val version = ext.compareVersion
                val groupPath = group.toString().replace(oldChar = '.', newChar = '/')
                val publicManifestUrl = "https://raw.githubusercontent.com/aol-public/OneMobileSDK-releases-android/maven/" +
                                "$groupPath/" +
                                "$artifactId/" +
                                "$version/" +
                                "$artifactId-$version-pubapi.json"

                with(artifacts) {
                    add("archives", manifestFile) { artifact ->
                        artifact.classifier = PUBLIC_API_CLASSIFIER
                    }
                }

                with(tasks) {
                    create("checkApiChanges", ApiCompareTask::class.java) { task ->
                        with(task) {
                            oldManifestUrl = publicManifestUrl
                            newManifestFile = manifestFile
                            changeReportFile = ext.reportFile
                            implicitNamespaces += ext.implicitNamespaces
                        }
                    }.dependsOn(getByName("kaptReleaseKotlin"))

                    create("generateProguard", ProguardGenerateTask::class.java) { task ->
                        with(task) {
                            newManifestFile = manifestFile
                            proguardFile = File(projectDir, "proguard-classes.pro")
                        }
                    }.dependsOn(getByName("kaptDebugKotlin"))

                    getByName("compileDebugKotlin")
                            .dependsOn(getByName("generateProguard"))
                }
            }
        }
    }
}
