package com.example.survivors.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.example.survivors.SurvivorsGame
import com.example.survivors.util.SimpleUI

class HowToPlayScreen(game: SurvivorsGame) : BaseScreen(game) {
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
        shape.color = Color(0.07f, 0.07f, 0.1f, 1f)
        shape.rect(0f, 0f, 1080f, 1920f)
        ui.begin()
        ui.button(40f, 1750f, 240f, 100f, "Back", shape, font, game.batch) { game.screen = MenuScreen(game) }
        shape.end()
        game.batch.begin()
        font.data.setScale(1.1f)
        font.draw(game.batch, "Desktop: WASD move, SPACE dash, ESC pause, F1 debug", 120f, 1480f)
        font.draw(game.batch, "Android: left drag joystick, tap right side dash", 120f, 1410f)
        font.draw(game.batch, "Auto-fire aims nearest enemy. Collect gems to level up.", 120f, 1340f)
        font.draw(game.batch, "Boss appears every 2 minutes. Survive and invest gold.", 120f, 1270f)
        game.batch.end()
    }
}
