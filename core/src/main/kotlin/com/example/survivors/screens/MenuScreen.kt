package com.example.survivors.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.example.survivors.SurvivorsGame
import com.example.survivors.util.SimpleUI

class MenuScreen(game: SurvivorsGame) : BaseScreen(game) {
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
        beginFrame()
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0.06f, 0.06f, 0.1f, 1f)
        shape.rect(0f, 0f, 1080f, 1920f)
        ui.begin()
        ui.button(280f, 1180f, 520f, 120f, "Start Run", shape, font, game.batch) { game.screen = GameScreen(game) }
        ui.button(280f, 1010f, 520f, 120f, "Meta Upgrades", shape, font, game.batch) { game.screen = MetaScreen(game) }
        ui.button(280f, 840f, 520f, 120f, "Settings", shape, font, game.batch) { game.screen = SettingsScreen(game) }
        ui.button(280f, 670f, 520f, 120f, "How To Play", shape, font, game.batch) { game.screen = HowToPlayScreen(game) }
        ui.button(280f, 500f, 520f, 120f, "Exit", shape, font, game.batch) { Gdx.app.exit() }
        shape.end()

        game.batch.begin()
        font.data.setScale(2.2f)
        font.draw(game.batch, "SURVIVORS ARENA", 265f, 1520f)
        font.data.setScale(1.2f)
        font.draw(game.batch, "Best: ${"%.1f".format(game.prefs.load().bestTime)}s   Kills: ${game.prefs.load().bestKills}", 270f, 430f)
        game.batch.end()
    }
}
