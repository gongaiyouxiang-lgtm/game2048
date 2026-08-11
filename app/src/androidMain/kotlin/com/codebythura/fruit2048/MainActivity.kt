package com.codebythura.fruit2048

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.codebythura.fruit2048.ui.Fruit2048
import com.codebythura.fruit2048.ui.theme.Fruit2048Theme
import com.codebythura.fruit2048.util.LocaleManager

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Fruit2048Theme {
                Fruit2048()
            }
        }
    }
}
