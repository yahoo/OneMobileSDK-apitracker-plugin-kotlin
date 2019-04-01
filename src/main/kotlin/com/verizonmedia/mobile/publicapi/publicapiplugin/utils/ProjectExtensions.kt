package com.verizonmedia.mobile.publicapi.publicapiplugin.utils

import com.android.build.gradle.LibraryExtension
import com.verizonmedia.mobile.publicapi.publicapiplugin.PublicApiExtension
import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.gradle.api.Project
import org.gradle.api.Task
import kotlin.reflect.KClass

fun Project.config(action: Project.() -> Unit) = action()

val Project.android: LibraryExtension get() = extensions.getByType(LibraryExtension::class.java)

val Project.publicApi: PublicApiExtension get() = extensions.getByType(PublicApiExtension::class.java)

val Project.gitPublish: GitPublishExtension get() = extensions.getByType(GitPublishExtension::class.java)

fun <T : Task> Project.task(name: String, type: KClass<T>, action: T.() -> Unit): T = tasks.create(name, type.java) { it.action() }