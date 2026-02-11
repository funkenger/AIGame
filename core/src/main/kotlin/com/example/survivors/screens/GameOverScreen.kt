package com.example.survivors.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.example.survivors.SurvivorsGame
import com.example.survivors.util.SimpleUI

class GameOverScreen(
    game: SurvivorsGame,
    private val survived: Float,
    private val kills: Int,
    private val level: Int
) : BaseScreen(game) {
    private val ui = SimpleUI()

    init {
        val meta = game.prefs.load()
        val earned = (survived * 1.2f + kills * 0.8f).toInt()
        meta.gold += earned
        meta.bestTime = maxOf(meta.bestTime, survived)
        meta.bestKills = maxOf(meta.bestKills, kills)
        game.prefs.save(meta)
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
        shape.color = Color(0.09f, 0.05f, 0.05f, 1f)
        shape.rect(0f, 0f, 1080f, 1920f)
        ui.begin()
        ui.button(280f, 700f, 520f, 120f, "Main Menu", shape, font, game.batch) { game.screen = MenuScreen(game) }
        ui.button(280f, 860f, 520f, 120f, "Run Again", shape, font, game.batch) { game.screen = GameScreen(game) }
        shape.end()
        game.batch.begin()
        font.data.setScale(2f)
        font.draw(game.batch, "GAME OVER", 350f, 1450f)
        font.data.setScale(1.2f)
        font.draw(game.batch, "Time: ${"%.1f".format(survived)}s", 350f, 1260f)
        font.draw(game.batch, "Kills: $kills", 350f, 1190f)
        font.draw(game.batch, "Level: $level", 350f, 1120f)
        font.draw(game.batch, "Best Time: ${"%.1f".format(meta.bestTime)}", 350f, 1050f)
        font.draw(game.batch, "Best Kills: ${meta.bestKills}", 350f, 980f)
        game.batch.end()
    }
}
