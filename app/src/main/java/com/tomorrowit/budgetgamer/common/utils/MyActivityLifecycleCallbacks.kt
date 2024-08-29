package com.tomorrowit.budgetgamer.common.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.tomorrowit.budgetgamer.common.config.extensions.tag
import com.tomorrowit.budgetgamer.domain.repo.LoggerRepo

class MyActivityLifecycleCallbacks(
    private val loggerRepo: LoggerRepo
) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        loggerRepo.info(tag(), "onActivityCreated: ${activity::class.java.simpleName}")
    }

    override fun onActivityStarted(activity: Activity) {
        loggerRepo.info(tag(), "onActivityStarted: ${activity::class.java.simpleName}")
    }

    override fun onActivityResumed(activity: Activity) {
        loggerRepo.info(tag(), "onActivityResumed: ${activity::class.java.simpleName}")
    }

    override fun onActivityPaused(activity: Activity) {
        loggerRepo.info(tag(), "onActivityPaused: ${activity::class.java.simpleName}")
    }

    override fun onActivityStopped(activity: Activity) {
        loggerRepo.info(tag(), "onActivityStopped: ${activity::class.java.simpleName}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        loggerRepo.info(tag(), "onActivitySaveInstanceState: ${activity::class.java.simpleName}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        loggerRepo.info(tag(), "onActivityDestroyed: ${activity::class.java.simpleName}")
    }
}