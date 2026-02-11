package com.example.survivors.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.example.survivors.SurvivorsGame
import com.example.survivors.util.SimpleUI

class MetaScreen(game: SurvivorsGame) : BaseScreen(game) {
    private val ui = SimpleUI()
    private val labels = listOf("Max HP", "Move Speed", "Magnet", "Damage", "XP Gain")

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
        shape.color = Color(0.07f, 0.07f, 0.1f, 1f)
        shape.rect(0f, 0f, 1080f, 1920f)
        ui.begin()
        ui.button(40f, 1750f, 240f, 100f, "Back", shape, font, game.batch) { game.screen = MenuScreen(game) }
        for (i in labels.indices) {
            val y = 1440f - i * 210f
            shape.color = Color(0.15f, 0.15f, 0.2f, 1f)
            shape.rect(120f, y, 840f, 170f)
            val level = when (i) {0 -> meta.maxHp;1 -> meta.moveSpeed;2 -> meta.magnet;3 -> meta.damage; else -> meta.xpGain}
            ui.button(730f, y + 35f, 200f, 100f, "Buy", shape, font, game.batch, meta.gold >= 20 + level * 10) {
                val cost = 20 + level * 10
                if (meta.gold >= cost) {
                    meta.gold -= cost
                    when (i) {0 -> meta.maxHp++;1 -> meta.moveSpeed++;2 -> meta.magnet++;3 -> meta.damage++; else -> meta.xpGain++}
                    game.prefs.save(meta)
                }
            }
            game.batch.begin()
            font.data.setScale(1.2f)
            font.draw(game.batch, "${labels[i]} Lv.$level", 150f, y + 112f)
            font.draw(game.batch, "Cost ${20 + level * 10}", 440f, y + 112f)
            game.batch.end()
        }
        shape.end()
        game.batch.begin()
        font.data.setScale(1.5f)
        font.draw(game.batch, "Gold: ${meta.gold}", 120f, 1680f)
        game.batch.end()
    }
}
