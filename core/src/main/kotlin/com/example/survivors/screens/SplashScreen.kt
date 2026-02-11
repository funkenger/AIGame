package com.example.survivors.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.example.survivors.SurvivorsGame

class SplashScreen(game: SurvivorsGame) : BaseScreen(game) {
    private var t = 0f

    override fun render(delta: Float) {
        t += delta
        beginFrame()
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0.1f, 0.1f, 0.14f, 1f)
        shape.rect(0f, 0f, 1080f, 1920f)
        shape.color = Color(0.4f, 0.8f, 1f, 1f)
        shape.circle(540f, 1020f, 120f)
        shape.end()
        game.batch.begin()
        font.data.setScale(2.1f)
        font.draw(game.batch, "SURVIVORS ARENA", 280f, 840f)
        game.batch.end()
        if (t > 1f) game.screen = MenuScreen(game)
    }
}
