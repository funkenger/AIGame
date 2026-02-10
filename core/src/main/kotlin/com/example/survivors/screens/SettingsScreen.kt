package com.example.survivors.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.example.survivors.SurvivorsGame
import com.example.survivors.util.SimpleUI

class SettingsScreen(game: SurvivorsGame) : BaseScreen(game) {
    private val ui = SimpleUI()

    init {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val v = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                return ui.touch(v.x, v.y)
            }
        }
    }

    override fun render(delta: Float) {
        val meta = game.prefs.load()
        beginFrame()
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0.08f, 0.08f, 0.11f, 1f)
        shape.rect(0f, 0f, 1080f, 1920f)
        ui.begin()
        ui.button(40f, 1750f, 240f, 100f, "Back", shape, font, game.batch) { game.screen = MenuScreen(game) }
        ui.button(120f, 1260f, 840f, 120f, "Colorblind Mode: ${if (meta.colorblind) "ON" else "OFF"}", shape, font, game.batch) {
            meta.colorblind = !meta.colorblind; game.prefs.save(meta)
        }
        ui.button(120f, 1080f, 840f, 120f, "Vibration: ${if (meta.vibration) "ON" else "OFF"}", shape, font, game.batch) {
            meta.vibration = !meta.vibration; game.prefs.save(meta)
        }
        ui.button(120f, 900f, 840f, 120f, "Deterministic: ${if (meta.deterministic) "ON" else "OFF"}", shape, font, game.batch) {
            meta.deterministic = !meta.deterministic; game.prefs.save(meta)
        }
        ui.button(120f, 720f, 840f, 120f, "Seed: ${meta.seed} (tap to +1)", shape, font, game.batch) {
            meta.seed += 1; game.prefs.save(meta)
        }
        shape.end()
    }
}
