package com.example.survivors

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    val cfg = Lwjgl3ApplicationConfiguration().apply {
        setTitle("Survivors Arena")
        setWindowedMode(1080, 1920)
        useVsync(true)
        setForegroundFPS(60)
    }
    Lwjgl3Application(SurvivorsGame(object : PlatformServices {
        override fun vibrate(ms: Long) = Unit
        override fun canVibrate(): Boolean = false
    }), cfg)
}
