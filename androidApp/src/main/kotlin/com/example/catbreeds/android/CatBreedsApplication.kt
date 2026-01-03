package com.example.catbreeds.android

import android.app.Application
import com.example.catbreeds.di.sharedModules
import org.koin.core.context.startKoin

class CatBreedsApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(sharedModules())
        }
    }
}