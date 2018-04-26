/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.cilib

import com.android.build.gradle.internal.dsl.AnnotationProcessorOptions
import com.android.build.gradle.tasks.factory.AndroidJavaCompile
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.BUILD_PATH_KEY
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.PUBLIC_API_FILENAME
import com.aol.mobile.sdk.cilib.task.ApiCheckTask
import com.aol.mobile.sdk.cilib.task.ProguardGenTask
import com.aol.mobile.sdk.cilib.utils.*
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocOfflineLink
import org.gradle.external.javadoc.StandardJavadocDocletOptions

class AndroidCiLibrary : Plugin<Project> {
    companion object {
        private const val apiCollectorVersion = "1.4"
        private const val minAndroidApi = 16
        private const val compileAndroidApi = 27
        private const val targetAndroidApi = compileAndroidApi
    }

    private lateinit var libraryVersion: String
    private lateinit var previousLibraryVersion: String

    private fun calculateVersions(project: Project) {
        with(project) {
            val isCommitTagged =
                    "git describe --exact-match --tags HEAD".execute(projectDir).wait() == 0
            val gitTag =
                    "git describe --tags --abbrev=0".execute(projectDir).text.let { if (it == "") "0.1" else it }

            val versionParts = gitTag.split(".")
            val nextVersion = when (versionParts.size) {
                0, 1 -> "1.0"
                else -> versionParts.mapIndexed { index: Int, part: String ->
                    if (index == versionParts.lastIndex)
                        "${part.toInt() + 1}"
                    else
                        part
                }.joinToString(separator = ".", postfix = "", prefix = "")

            }

            previousLibraryVersion = if (!isCommitTagged) gitTag else
                "git describe --tags --abbrev=0 $gitTag^".execute(projectDir).text
            libraryVersion = if (isCommitTagged) gitTag else
                "$nextVersion-SNAPSHOT"
        }
    }

    private fun configurePluginsAndDependencies(project: Project) {
        project.config {
            plugins.apply {
                apply("com.android.library")
                apply("kotlin-android")
                apply("kotlin-kapt")
                apply("digital.wup.android-maven-publish")
                apply("org.ajoberstar.git-publish")
            }

            configurations.apply {
                create("javadoc")
            }

            dependencies.apply {
                add("javadoc", "ch.raffael.pegdown-doclet:pegdown-doclet:1.3")
                add("kapt", "com.aol.one.publishers.android:api-collector:$apiCollectorVersion")
                add("compileOnly", "com.aol.one.publishers.android:annotations:$apiCollectorVersion")
            }
        }
    }

    private fun configureAndroid(project: Project) {
        project.config {
            android.apply {
                compileSdkVersion(compileAndroidApi)

                compileOptions {
                    it.apply {
                        setSourceCompatibility(JavaVersion.VERSION_1_8)
                        setTargetCompatibility(JavaVersion.VERSION_1_8)
                    }
                }

                defaultConfig {
                    it.apply {
                        minSdkVersion(minAndroidApi)
                        targetSdkVersion(targetAndroidApi)
                        version = libraryVersion
                    }
                }

                aaptOptions {
                    it.failOnMissingConfigEntry = true
                }

                lintOptions {
                    it.apply {
                        isAbortOnError = true
                        isShowAll = true
                        isWarningsAsErrors = true
                        isExplainIssues = true

                        textReport = true
                        htmlReport = true
                        xmlReport = true

                        textOutput("stdout")
                    }
                }

                testOptions {
                    it.apply {
                        unitTests.apply {
                            isIncludeAndroidResources = true
                            isReturnDefaultValues = true
                        }

                        unitTests.all(delegateClosureOf {
                            testLogging {
                                it.apply {
                                    events("passed", "skipped", "failed", "standardOut", "standardError")
                                    showStandardStreams = true
                                }
                            }
                        })
                    }
                }

                buildTypes {
                    it.apply {
                        getByName("release") {
                            it.apply {
                                isMinifyEnabled = true
                                isUseProguard = true
                            }
                        }
                    }
                }

                productFlavors.all {
                    it.apply {
                        val defaultProguardFile = getDefaultProguardFile("proguard-android-optimize.txt")
                        proguardFiles(defaultProguardFile, "proguard-rules.pro", "proguard-classes-${name.toLowerCase()}.pro")
                        testProguardFiles(defaultProguardFile, "proguard-rules.pro", "proguard-classes-${name.toLowerCase()}.pro")
                        consumerProguardFiles("proguard-rules.pro", "proguard-classes-${name.toLowerCase()}.pro")
                    }
                }

                variantFilter { it.setIgnore(!it.buildType.name.contains("release")) }
            }
        }
    }

    private fun configureApiTracking(project: Project) {
        project.config {
            android.libraryVariants.all { variant ->
                variant.apply {
                    val artifactDir = mkdir("$buildDir/artifacts/$dirName")

                    (javaCompileOptions.annotationProcessorOptions as AnnotationProcessorOptions)
                            .argument(BUILD_PATH_KEY, artifactDir.absolutePath)

                    val genProguardTask = task("genApiProguard${name.capitalize()}", ProguardGenTask::class) {
                        dependsOn(javaCompiler)
                        manifestFile = file("$artifactDir/$PUBLIC_API_FILENAME")
                        proguardFile = file("$projectDir/proguard-classes-${flavorName.toLowerCase()}.pro")
                    }

                    val checkApiTask = task("checkApiChanges${name.capitalize()}", ApiCheckTask::class) {
                        dependsOn(javaCompiler)

                        oldManifestUrl = "https://raw.githubusercontent.com/${androidCi.githubRepo.get()}/${androidCi.githubBranch.get()}/" +
                                "${androidCi.groupPath}/" +
                                "${androidCi.artifactId(variant)}/" +
                                "$previousLibraryVersion/" +
                                "${androidCi.artifactId(variant)}-$previousLibraryVersion-pubapi.json"

                        newManifestFile = genProguardTask.manifestFile
                        implicitNamespaces = androidCi.apiTrimNamespaces
                        changeReportFile = file("$buildDir/maven/API CHANGES/${androidCi.artifactId(variant)}/$previousLibraryVersion to $libraryVersion.md")
                    }

                    tasks.getByName("publish").dependsOn(checkApiTask)

                    javaCompiler.finalizedBy(genProguardTask)
                }
            }
        }
    }

    private fun configureLibraryArtifacts(project: Project) {
        project.config {
            android.libraryVariants.all { variant ->
                variant.apply {
                    val genJavadocTask = task("generate${name.capitalize()}Javadoc", Javadoc::class) {
                        val javaCompile = javaCompiler as AndroidJavaCompile
                        dependsOn(javaCompile)

                        title = "Javadoc for $flavorName flavor"
                        classpath = files(javaCompile.classpath, project.android.bootClasspath)
                        source = javaCompile.source
                        isFailOnError = false

                        (options as StandardJavadocDocletOptions).apply {
                            doclet = "ch.raffael.doclets.pegdown.PegdownDoclet"
                            docletpath = configurations.getByName("javadoc").files.toList()
                            encoding = "UTF-8"
                            locale = "en"
                            links = listOf("http://docs.oracle.com/javase/7/docs/api/")
                            linksOffline = listOf(JavadocOfflineLink("https://developer.android.com/reference", "${android.sdkDirectory}/docs/reference"))

                            addStringOption("overview", "$rootDir/README.md")
                            addStringOption("javadocversion", "v8")
                            addStringOption("extensions", "all")
                            addStringOption("parse-timeout", "100")
                            addStringOption("enable-auto-highlight")
                            addStringOption("quiet")
                        }

                        exclude("**/R.java")
                    }

                    task("jar${name.capitalize()}Javadoc", Jar::class) {
                        classifier = "javadoc"
                        destinationDir = file("$buildDir/artifacts/$dirName")
                        from(genJavadocTask.outputs.files)
                    }

                    task("jar${name.capitalize()}Sources", Jar::class) {
                        classifier = "sources"
                        destinationDir = file("$buildDir/artifacts/$dirName")
                        from(sourceSets.flatMap { it.javaDirectories })
                    }
                }
            }
        }
    }

    private fun configureLocalMavenPublishing(project: Project) {
        project.config {
            publishing.apply {
                publications.apply {
                    android.libraryVariants.all { variant ->
                        val genProguardTask = tasks.getByName("genApiProguard${variant.name.capitalize()}") as ProguardGenTask

                        create("maven${variant.name.capitalize()}Aar", MavenPublication::class.java) { publication ->
                            publication.apply {
                                groupId = androidCi.groupId.get()
                                version = libraryVersion
                                artifactId = androidCi.artifactId(variant)

                                from(components.findByName("android${variant.name.capitalize()}"))

                                artifact(tasks.getByName("jar${variant.name.capitalize()}Sources"))
                                artifact(tasks.getByName("jar${variant.name.capitalize()}Javadoc"))
                                artifact(genProguardTask.manifestFile) {
                                    with(it) {
                                        classifier = "pubapi"
                                        extension = "json"
                                    }
                                }
                            }
                        }
                    }
                }

                repositories.apply {
                    maven { it.setUrl("$buildDir/maven/") }
                }
            }

            tasks.getByName("publish").dependsOn.addAll(listOf("assembleRelease", "gitPublishReset"))
        }
    }

    private fun configureGithubPublishing(project: Project) {
        project.config {
            gitPublish.apply {
                repoUri = "https://github.com/${androidCi.githubRepo.get()}.git"
                branch = androidCi.githubBranch.get()
                repoDir = file("$buildDir/maven")

                preserve { it.includes.add("**/*") }

                with(androidCi) {
                    commitMessage = "Artifact ${groupId.get()}:${artifactId.get()}:$libraryVersion"
                }
            }

            tasks.getByName("gitPublishCommit").dependsOn.add("publish")
        }
    }

    override fun apply(project: Project) {
        calculateVersions(project)

        project.extensions.create("androidCi", AndroidCiExtension::class.java, project)

        configurePluginsAndDependencies(project)
        configureAndroid(project)
        configureApiTracking(project)

        project.afterEvaluate { evaluatedProject ->
            configureLibraryArtifacts(evaluatedProject)
            configureLocalMavenPublishing(evaluatedProject)
            configureGithubPublishing(evaluatedProject)
        }
    }
}