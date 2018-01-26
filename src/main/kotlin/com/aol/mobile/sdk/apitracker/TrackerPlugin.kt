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

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

const val CLASSIFIER = "pubapi"
const val CONFIGURATION = "publicApiManifest"
const val API_COLLECTOR_VERSION = "1.2"
const val API_ANNOTATIONS_VERSION = "1.1"

class TrackerPlugin : Plugin<Project> {
    open class Extension {
        var compareVersion: String = "1.0"
    }

    override fun apply(project: Project) {
        with(project) {
            val ext = extensions.create("apiTracker", Extension::class.java)

            afterEvaluate {
                it.artifacts.add("archives", File(project.rootDir, "public_api.json")) {
                    it.classifier = CLASSIFIER
                }

                with(dependencies) {
                    add(CONFIGURATION, "$group:${properties["archivesBaseName"]}:${ext.compareVersion}:$CLASSIFIER@json")
                }
            }

            with(configurations) {
                create(CONFIGURATION).apply {
                    isTransitive = false
                    isVisible = false
                    description = "Public api tracker configuration"
                }
            }

            with(dependencies) {
                add("kapt", "com.aol.one.publishers.android:api-collector:$API_COLLECTOR_VERSION")
                add("compileOnly", "com.aol.one.publishers.android:annotations:$API_ANNOTATIONS_VERSION")
            }

            tasks.add(ApiCompareTask())
        }
    }
}
