package com.codebythura.fruit2048.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Tracks the currently-resumed Activity so context-less controllers (interstitial ads,
 * locale switch) can reach it. Registered in [com.codebythura.fruit2048.MainApplication].
 */
object ActivityHolder : Application.ActivityLifecycleCallbacks {
    var current: Activity? = null
        private set

    override fun onActivityResumed(activity: Activity) {
        current = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (current === activity) current = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
