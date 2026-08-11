package com.codebythura.fruit2048.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Shared Koin startup. Each platform passes its own [platformModule] plus an optional
 * [appDeclaration] (Android uses it to register `androidContext`).
 */
fun initKoin(
    platformModule: Module,
    appDeclaration: KoinAppDeclaration = {},
): KoinApplication =
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule)
    }
