package com.example.survivors.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class UIButton(val rect: Rectangle, val label: String, val onClick: () -> Unit)

class SimpleUI {
    private val buttons = ArrayList<UIButton>()

    fun begin() = buttons.clear()

    fun button(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        label: String,
        shape: ShapeRenderer,
        font: BitmapFont,
        batch: SpriteBatch,
        active: Boolean = true,
        onClick: () -> Unit
    ) {
        val btn = UIButton(Rectangle(x, y, w, h), label, onClick)
        buttons.add(btn)
        shape.color = if (active) Color(0.18f, 0.18f, 0.22f, 0.95f) else Color(0.1f, 0.1f, 0.1f, 0.7f)
        shape.rect(x, y, w, h)
        batch.begin()
        font.color = Color.WHITE
        font.draw(batch, label, x + 30f, y + h * 0.62f)
        batch.end()
    }

    fun touch(x: Float, y: Float): Boolean {
        for (i in buttons.indices.reversed()) {
            if (buttons[i].rect.contains(x, y)) {
                buttons[i].onClick.invoke()
                return true
            }
        }
        return false
    }
}
