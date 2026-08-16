package com.codebythura.fruit2048

import platform.UIKit.UIColor
import platform.UIKit.UIViewController

/**
 * TEMPORARY: plain UIKit (no Compose) through the CURRENT scene-based lifecycle + dynamic
 * framework. Green => the scene setup and dynamic framework are fine and Compose is the sole
 * problem. Crash => my recent scene/framework changes broke launch (not Compose).
 */
fun MainViewController(): UIViewController {
    val controller = UIViewController()
    controller.view.backgroundColor = UIColor(red = 0.05, green = 0.6, blue = 0.25, alpha = 1.0)
    return controller
}
