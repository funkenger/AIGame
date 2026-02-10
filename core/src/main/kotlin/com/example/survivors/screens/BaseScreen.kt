package com.example.survivors.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.example.survivors.SurvivorsGame

abstract class BaseScreen(protected val game: SurvivorsGame) : ScreenAdapter() {
    protected val camera = OrthographicCamera()
    protected val viewport = FitViewport(1080f, 1920f, camera)
    protected val font = BitmapFont()
    protected val shape = ShapeRenderer()

    protected fun beginFrame() {
        Gdx.gl.glClearColor(0.04f, 0.04f, 0.06f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        camera.update()
        shape.projectionMatrix = camera.combined
        game.batch.projectionMatrix = camera.combined
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        font.dispose()
        shape.dispose()
    }
}
