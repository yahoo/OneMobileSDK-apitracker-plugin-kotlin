/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.verizonmedia.mobile.publicapi.cilib

import com.android.build.gradle.internal.dsl.AnnotationProcessorOptions
import com.android.build.gradle.tasks.AndroidJavaCompile
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.BUILD_PATH_KEY
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.PUBLIC_API_FILENAME
import com.verizonmedia.mobile.publicapi.cilib.task.ApiCheckTask
import com.verizonmedia.mobile.publicapi.cilib.task.DexMetricsTask
import com.verizonmedia.mobile.publicapi.cilib.task.ProguardGenTask
import com.verizonmedia.mobile.publicapi.cilib.utils.*
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocOfflineLink
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import java.io.File

class AndroidCiLibrary : Plugin<Project> {
    companion object {
        private const val apiCollectorVersion = "1.4"
        private const val minAndroidApi = 16
        private const val compileAndroidApi = 28
        private const val targetAndroidApi = compileAndroidApi
    }

    private lateinit var libraryVersion: String
    private lateinit var previousLibraryVersion: String
    private lateinit var artifactDir: File
    private val isOnCi = System.getenv("CI") != null

    private fun calculateVersions(project: Project) {
        with(project) {
            val isCommitTagged =
                    "git describe --exact-match --tags HEAD".execute(projectDir).wait() == 0
            val gitTag =
                    "git describe --tags --abbrev=0".execute(projectDir).text.let { if (it == "") "0.1" else it }

            val versionParts = gitTag.split(".")
            val nextVersion = when (versionParts.size) {
                0, 1 -> "1.0"
                else -> versionParts.asSequence().mapIndexed { index: Int, part: String ->
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
                if (androidCi.isNonAndroidLib.get() == java.lang.Boolean(true)) {
                    apply("kotlin")
                } else {
                    apply("com.android.library")
                    apply("kotlin-android")
                }

                apply("kotlin-kapt")
                apply("digital.wup.android-maven-publish")
                apply("org.ajoberstar.git-publish")
                apply("com.selesse.git.changelog")
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
                        sourceCompatibility = JavaVersion.VERSION_1_8
                        targetCompatibility = JavaVersion.VERSION_1_8
                    }
                }

                defaultConfig {
                    it.apply {
                        minSdkVersion(minAndroidApi)
                        targetSdkVersion(targetAndroidApi)

//                        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
//                        versionCode = 1
//                        version = libraryVersion
//                        versionName = libraryVersion
                    }
                }

                aaptOptions {
                    it.failOnMissingConfigEntry = true
                }

                lintOptions {
                    it.apply {
                        isAbortOnError = false
                        isShowAll = true
                        isWarningsAsErrors = true
                        isExplainIssues = true

                        textReport = true
                        htmlReport = true
                        xmlReport = true

                        textOutput("stdout")
                    }
                }

                testOptions.unitTests.apply {
                    isIncludeAndroidResources = true
                    isReturnDefaultValues = true

                    all(delegateClosureOf {
                        testLogging {
                            it.apply {
                                events("passed", "skipped", "failed", "standardOut", "standardError")
                                showStandardStreams = true
                            }
                        }
                    })
                }

                project.afterEvaluate {
                    buildTypes.all { buildType ->
                        val buildTypeName = buildType.name.toLowerCase()

                        if (productFlavors.isEmpty()) {
                            val apiProguardFile = file("$artifactDir/proguard-classes-$buildTypeName.pro")

                            buildType.apply {
                                consumerProguardFiles(apiProguardFile)
                            }
                        } else {
                            productFlavors.all { flavor ->
                                flavor.apply {
                                    val flavorName = name.toLowerCase()
                                    val flavoredProguardFile = file("$artifactDir/proguard-classes-$flavorName$buildTypeName.pro")

                                    consumerProguardFile(flavoredProguardFile)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun configureApiTracking(project: Project) {
        project.config {
            android.libraryVariants.all { variant ->
                variant.apply {
                    (javaCompileOptions.annotationProcessorOptions as AnnotationProcessorOptions)
                            .argument(BUILD_PATH_KEY, mkdir("$artifactDir/$dirName/").absolutePath)

                    val genProguardTask = task("genApiProguard${name.capitalize()}", ProguardGenTask::class) {
                        dependsOn(javaCompiler)
                        manifestFile = file("$artifactDir/$dirName/$PUBLIC_API_FILENAME")
                        proguardFile = file("$artifactDir/proguard-classes-${variant.name.toLowerCase()}.pro")
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
                        changeReportFile = file("${rootProject.buildDir}/maven/API CHANGES/${androidCi.artifactId(variant)}/$previousLibraryVersion to $libraryVersion.md")
                    }

                    tasks.getByName("publish").dependsOn(checkApiTask)
                    tasks.getByName("merge${variant.name.capitalize()}ConsumerProguardFiles").dependsOn(genProguardTask)
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
                    }

                    task("jar${name.capitalize()}Javadoc", Jar::class) {
                        classifier = "javadoc"
                        destinationDir = file("$artifactDir/$dirName")
                        from(genJavadocTask.outputs.files)
                        exclude("**/R.html", "**/R.*.html")
                    }

                    task("jar${name.capitalize()}Sources", Jar::class) {
                        classifier = "sources"
                        destinationDir = file("$artifactDir/$dirName")
                        from(sourceSets.flatMap { it.javaDirectories })
                    }
                }
            }
        }
    }

    private fun configureLocalMavenPublishing(project: Project) {
        project.config {
            android.libraryVariants.all { variant ->
                val genProguardTask = tasks.getByName("genApiProguard${variant.name.capitalize()}") as ProguardGenTask

                publishing.publications.create("maven${variant.name.capitalize()}Aar", MavenPublication::class.java) {
                    it.apply {
                        groupId = androidCi.groupId.get()
                        version = libraryVersion
                        artifactId = androidCi.artifactId(variant)

                        from(components.findByName("android${variant.name.capitalize()}"))

                        artifact(tasks.getByName("jar${variant.name.capitalize()}Sources"))
                        artifact(tasks.getByName("jar${variant.name.capitalize()}Javadoc"))
                        artifact(genProguardTask.manifestFile) { artifact ->
                            with(artifact) {
                                classifier = "pubapi"
                                extension = "json"
                            }
                        }
                    }
                }
            }

            publishing.apply {
                repositories.apply {
                    maven { it.setUrl("${rootProject.buildDir}/maven/") }
                }
            }

            if (isOnCi) {
                tasks.getByName("publish").dependsOn.addAll(listOf("assembleRelease", "gitPublishReset"))
            }
        }
    }

    private fun configureGithubPublishing(project: Project) {
        project.config {
            gitPublish.apply {
                repoUri = "https://github.com/${androidCi.githubRepo.get()}.git"
                branch = androidCi.githubBranch.get()
                repoDir = file("${rootProject.buildDir}/maven")

                preserve { it.includes.add("**/*") }

                with(androidCi) {
                    commitMessage = "Artifact ${groupId.get()}:${artifactId.get()}:$libraryVersion"
                }
            }

            if (isOnCi) {
                tasks.getByName("gitPublishCommit").dependsOn("publish")
            }
        }
    }

    private fun configureChangelogGeneration(project: Project) {
        project.config {
            changelog.apply {
                val artifactId = androidCi.artifactId.get()

                title = "# O2 ${artifactId.capitalize()} release notes"
                outputDirectory = file("${rootProject.buildDir}/maven")
                fileName = "${artifactId.toUpperCase()} CHANGELOG.md"

                commitFormat = "%s"

                markdownConvention.apply {
                    commitFormat = "- %s"
                }

                includeLines = closureOf<String, Boolean> {
                    !contains("Merge pull request #")
                }
            }

            if (isOnCi) {
                tasks.getByName("generateChangelog").dependsOn("gitPublishReset")
                tasks.getByName("gitPublishCommit").dependsOn("generateChangelog")
            }
        }
    }

    private fun gatherMethodAndFieldMetrics(project: Project) {
        project.config {
            android.libraryVariants.all { variant ->
                val taskName = "measureDexOf${variant.name.capitalize()}"

                val dexMetricsCount = task(taskName, DexMetricsTask::class) {
                    aarFile = variant.outputs.map { it.outputFile }.first { it.extension == "aar" }
                    metricsReportFile = file("${rootProject.buildDir}/maven/DEX COUNT/${androidCi.artifactId(variant)}/${variant.name.toUpperCase()}/STATS for $libraryVersion.md")
                }

                dexMetricsCount.dependsOn(variant.assemble)

                if (isOnCi) {
                    dexMetricsCount.dependsOn("gitPublishReset")
                    tasks.getByName("gitPublishCommit").dependsOn(dexMetricsCount)
                }
            }
        }
    }

    override fun apply(project: Project) {
        artifactDir = project.mkdir("${project.buildDir}/androidCiPlugin/")

        calculateVersions(project)

        project.extensions.create("androidCi", AndroidCiExtension::class.java, project)

        configurePluginsAndDependencies(project)
        configureAndroid(project)
        configureApiTracking(project)
        gatherMethodAndFieldMetrics(project)

        project.afterEvaluate { evaluatedProject ->
            configureLibraryArtifacts(evaluatedProject)
            if (isOnCi) {
                configureLocalMavenPublishing(evaluatedProject)
                configureGithubPublishing(evaluatedProject)
            }
            configureChangelogGeneration(evaluatedProject)
        }
    }
}