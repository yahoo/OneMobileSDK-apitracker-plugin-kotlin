package com.verizonmedia.mobile.publicapi.publicapiplugin

import com.android.build.gradle.internal.dsl.AnnotationProcessorOptions
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.BUILD_PATH_KEY
import com.aol.mobile.sdk.apicollector.PublicApiGrabber.Companion.PUBLIC_API_FILENAME
import com.verizonmedia.mobile.publicapi.publicapiplugin.tasks.*
import com.verizonmedia.mobile.publicapi.publicapiplugin.utils.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class PublicApiPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("publicApi", PublicApiExtension::class.java, project)

        project.config {
            plugins.apply {
                apply("org.ajoberstar.git-publish")
            }
            dependencies.apply {
                add("kapt", "com.aol.one.publishers.android:api-collector:1.4")
                add("compileOnly", "com.aol.one.publishers.android:annotations:1.4")
            }
        }

        generatePublicApiArtifacts(project)

        project.afterEvaluate { evaluatedProject ->
            configureGithubPublishing(evaluatedProject)
        }
    }

    private fun generatePublicApiArtifacts(project: Project) {
        project.config {
            android.libraryVariants.all { variant ->
                variant.apply {
                    (javaCompileOptions.annotationProcessorOptions as AnnotationProcessorOptions)
                            .argument(BUILD_PATH_KEY, mkdir("${publicApi.publicApiManifestDir.get()}/$dirName").absolutePath)

                    val manifest = file("${publicApi.publicApiManifestDir.get()}/$dirName/$PUBLIC_API_FILENAME")

                    task("genPublicApiProguard${name.capitalize()}", ProguardGenTask::class) {
                        dependsOn(javaCompileProvider)
                        manifestFile = manifest
                        proguardFile = file("${publicApi.generatedProguardRulesDir.get()}/proguard-classes-${variant.name.toLowerCase()}.pro")
                    }

                    task("checkPublicApiChanges${name.capitalize()}", ApiCheckTask::class) {
                        dependsOn(javaCompileProvider)
                        variantDirName = dirName
                        newManifestFile = manifest
                        implicitNamespaces = publicApi.apiTrimNamespaces
                        changeReportFile = file("${publicApi.changesDir.get()}/${publicApi.artifactId.get()}/$dirName/apiChanges.md")
                    }

                    task("manifestCopy${name.capitalize()}", CopyManifestTask::class) {
                        dependsOn(javaCompileProvider)
                        manifestFile = manifest
                        copiedManifestFile = file("${publicApi.changesDir.get()}/${publicApi.artifactId.get()}/$dirName/$PUBLIC_API_FILENAME")
                    }

                    tasks.apply {
                        (getByName("bundle${variant.name.capitalize()}Aar") as AbstractArchiveTask).from(manifest)
                        getByName("preBuild").dependsOn(getByName("gitPublishReset"))
                        getByName("merge${variant.name.capitalize()}ConsumerProguardFiles").dependsOn(getByName("genPublicApiProguard${name.capitalize()}"))
                        getByName("compile${name.capitalize()}Sources").dependsOn(getByName("checkPublicApiChanges${name.capitalize()}"))
                        getByName("assemble${name.capitalize()}").dependsOn(getByName("manifestCopy${name.capitalize()}"))
                        getByName("gitPublishCommit").dependsOn(getByName("assemble"))
                        getByName("lint").dependsOn(getByName("gitPublishPush"))
                    }
                }
            }
        }
    }

    private fun configureGithubPublishing(project: Project) {
        project.config {
            gitPublish.apply {
                repoUri = "https://github.com/${publicApi.githubRepo.get()}.git"
                branch = publicApi.githubBranch.get()
                repoDir = publicApi.changesDir.get()

                preserve { it.includes.add("**/*") }

                with(publicApi) {
                    commitMessage = "Artifact ${groupId.get()}:${artifactId.get()}"
                }
            }
        }
    }
}
