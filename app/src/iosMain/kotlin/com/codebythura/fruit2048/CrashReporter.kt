package com.codebythura.fruit2048

import kotlin.native.setUnhandledExceptionHook
import platform.Foundation.NSUserDefaults

private const val CRASH_KEY = "last_crash_report"

/**
 * Captures an otherwise-fatal (uncaught) exception to NSUserDefaults so the NEXT launch can
 * display it on screen instead of silently crashing. Diagnostic aid for iOS bring-up.
 */
fun installCrashReporter() {
    setUnhandledExceptionHook { throwable ->
        val text = throwable.toString() + "\n\n" + throwable.stackTraceToString()
        NSUserDefaults.standardUserDefaults.setObject(text, forKey = CRASH_KEY)
        NSUserDefaults.standardUserDefaults.synchronize()
    }
}

/** Reads and clears the last captured crash report, if any. */
fun consumeCrashReport(): String? {
    val defaults = NSUserDefaults.standardUserDefaults
    val report = defaults.stringForKey(CRASH_KEY) ?: return null
    defaults.removeObjectForKey(CRASH_KEY)
    defaults.synchronize()
    return report
}
