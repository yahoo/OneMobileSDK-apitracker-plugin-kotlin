/*
 * Copyright 2018, Oath Inc.
 * Licensed under the terms of the MIT License. See LICENSE.md file in project root for terms.
 */

package com.aol.mobile.sdk.cilib.utils

import com.android.build.gradle.LibraryExtension
import com.aol.mobile.sdk.cilib.AndroidCiExtension
import org.ajoberstar.gradle.git.publish.GitPublishExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublishingExtension
import kotlin.reflect.KClass

fun <T : Task> Project.task(name: String, type: KClass<T>, action: T.() -> Unit): T = tasks.create(name, type.java) { it.action() }

fun Project.config(action: Project.() -> Unit) = action()

val Project.android: LibraryExtension get() = extensions.getByType(LibraryExtension::class.java)

val Project.androidCi: AndroidCiExtension get() = extensions.getByType(AndroidCiExtension::class.java)

val Project.publishing: PublishingExtension get() = extensions.getByType(PublishingExtension::class.java)

val Project.gitPublish: GitPublishExtension get() = extensions.getByType(GitPublishExtension::class.java)