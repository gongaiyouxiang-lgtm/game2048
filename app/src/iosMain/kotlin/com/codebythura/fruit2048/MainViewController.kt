package com.codebythura.fruit2048

import androidx.compose.ui.window.ComposeUIViewController
import com.codebythura.fruit2048.di.initKoin
import com.codebythura.fruit2048.di.platformModule
import com.codebythura.fruit2048.ui.Fruit2048
import com.codebythura.fruit2048.ui.theme.Fruit2048Theme
import platform.UIKit.UIViewController

// Start Koin exactly once, the first time the Compose view controller is created.
private val koinStarted: Unit by lazy {
    initKoin(platformModule)
    Unit
}

/** Entry point the Swift `iosApp` embeds via a UIKit SceneDelegate root. */
fun MainViewController(): UIViewController {
    koinStarted
    return ComposeUIViewController {
        Fruit2048Theme {
            Fruit2048()
        }
    }
}
