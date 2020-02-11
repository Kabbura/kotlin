/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util

import org.jetbrains.kotlin.utils.KotlinExceptionWithAttachments
import java.lang.IllegalStateException
import kotlin.reflect.KClass

@Deprecated("call should be removed once resolved")
fun <T> wrapExceptionWithAttachments(
    ref: String,
    block: () -> T,
    exceptionClass: KClass<out Throwable> = Throwable::class,
    vararg vars: Any?
): T {
    try {
        return block.invoke()
    } catch (t: Throwable) {
        if (exceptionClass.java.isAssignableFrom(t.javaClass)) {
            val exception = KotlinExceptionWithAttachments(ref, t)
            var i = 0
            for (arg in vars) {
                exception.withAttachment("arg" + i++, arg.toString())
            }
            throw exception
        }
        throw t;
    }
}

class ExceptionWithAttachmentWrapper(val ref: String, val exceptionClass: KClass<out Throwable> = Throwable::class) {
    var vars: Array<out Any?> = emptyArray()

    fun <T> invoke(block: () -> T) =
        wrapExceptionWithAttachments(ref, block, exceptionClass, vars)

    fun vars(vararg vars: Any?): ExceptionWithAttachmentWrapper {
        this.vars = vars
        return this;
    }
}

val ea141456 = { ExceptionWithAttachmentWrapper("EA-141456", IllegalStateException::class) }