package com.example.catbreeds.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.catbreeds.data.platform.initPlatform
import com.example.catbreeds.ui.AppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPlatform(this)

        setContent {
            AppRoot()
        }
    }
}
