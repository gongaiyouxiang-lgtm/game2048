package com.codebythura.fruit2048

import android.app.Application
import com.codebythura.fruit2048.di.initKoin
import com.codebythura.fruit2048.di.platformModule
import com.codebythura.fruit2048.util.ActivityHolder
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityHolder)
        initKoin(platformModule) {
            androidContext(this@MainApplication)
        }
        MobileAds.initialize(this)
    }
}
