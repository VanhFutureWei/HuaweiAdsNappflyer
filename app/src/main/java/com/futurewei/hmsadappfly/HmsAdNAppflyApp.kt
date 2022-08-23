package com.futurewei.hmsadappfly

import android.app.Application
import com.futurewei.hmsadappfly.utils.AppContainer
import com.futurewei.hmsadappfly.utils.AppContainerImpl


class HmsAdNAppflyApp : Application() {

//    used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)

    }
}