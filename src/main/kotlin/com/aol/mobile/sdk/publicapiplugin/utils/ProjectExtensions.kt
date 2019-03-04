package com.aol.mobile.sdk.publicapiplugin.utils

import com.android.build.gradle.LibraryExtension
import com.aol.mobile.sdk.publicapiplugin.PublicApiExtension
import org.gradle.api.Project
import org.gradle.api.Task
import kotlin.reflect.KClass

fun Project.config(action: Project.() -> Unit) = action()

val Project.android: LibraryExtension get() = extensions.getByType(LibraryExtension::class.java)

val Project.publicApi: PublicApiExtension get() = extensions.getByType(PublicApiExtension::class.java)

fun <T : Task> Project.task(name: String, type: KClass<T>, action: T.() -> Unit): T = tasks.create(name, type.java) { it.action() }