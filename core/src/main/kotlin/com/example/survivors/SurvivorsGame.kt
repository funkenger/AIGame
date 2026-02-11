package com.example.survivors

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.example.survivors.screens.SplashScreen
import com.example.survivors.util.PreferencesManager

interface PlatformServices {
    fun vibrate(ms: Long)
    fun canVibrate(): Boolean
}

class SurvivorsGame(val platformServices: PlatformServices) : Game() {
    lateinit var batch: SpriteBatch
    lateinit var prefs: PreferencesManager

    override fun create() {
        batch = SpriteBatch()
        prefs = PreferencesManager()
        setScreen(SplashScreen(this))
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }
}
