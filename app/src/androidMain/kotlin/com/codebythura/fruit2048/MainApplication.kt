package com.codebythura.fruit2048

import android.app.Application
import com.codebythura.fruit2048.di.appModule
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        MobileAds.initialize(this)
    }
}
