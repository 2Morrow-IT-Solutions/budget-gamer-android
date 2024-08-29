package com.tomorrowit.budgetgamer.domain.repo

interface LoggerRepo {

    fun setUid(userId: String)

    fun verbose(tag: String, message: String)

    fun debug(tag: String, message: String)

    fun info(tag: String, message: String)

    fun warning(tag: String, message: String)

    fun warning(tag: String, message: String, exception: Exception)

    fun error(tag: String, message: String)

    fun error(tag: String, message: String, throwable: Throwable)
}