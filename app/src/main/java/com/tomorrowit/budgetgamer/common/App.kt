package com.tomorrowit.budgetgamer.common

import android.app.Application
import com.tomorrowit.budgetgamer.common.network.AppInitializer
import com.tomorrowit.budgetgamer.common.utils.MyActivityLifecycleCallbacks
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var appInitializer: AppInitializer

    @Inject
    lateinit var myActivityLifecycleCallbacks: MyActivityLifecycleCallbacks

    override fun onCreate() {
        super.onCreate()
        appInitializer.initialize()

        //This line takes care of registering Activity lifecycle logs
        registerActivityLifecycleCallbacks(myActivityLifecycleCallbacks)
    }
}