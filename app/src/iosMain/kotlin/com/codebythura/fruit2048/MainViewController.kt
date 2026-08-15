package com.codebythura.fruit2048

import platform.UIKit.UIColor
import platform.UIKit.UIViewController

/**
 * TEMPORARY plain-UIKit test: NO Compose at all — just a green background view controller.
 * If the screen is green, the Kotlin framework loads and the Swift hosting works, so the
 * crash is inside Compose Multiplatform rendering. If it still crashes, the problem is the
 * framework load / Swift app setup, before any UI.
 */
fun MainViewController(): UIViewController {
    val controller = UIViewController()
    controller.view.backgroundColor = UIColor(red = 0.05, green = 0.6, blue = 0.25, alpha = 1.0)
    return controller
}
