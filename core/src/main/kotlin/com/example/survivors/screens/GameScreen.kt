package com.example.survivors.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.example.survivors.SurvivorsGame
import com.example.survivors.engine.World
import com.example.survivors.util.GameRng
import com.example.survivors.util.SimpleUI
import com.example.survivors.util.clamp
import com.example.survivors.util.lerp
import kotlin.math.sin

class GameScreen(game: SurvivorsGame) : BaseScreen(game) {
    private val meta = game.prefs.load()
    private val rng = GameRng(if (meta.deterministic) meta.seed else System.currentTimeMillis())
    private val world = World(meta, rng)
    private val ui = SimpleUI()
    private var accumulator = 0f
    private var paused = false
    private var debug = false
    private var inputX = 0f
    private var inputY = 0f
    private var dashQueued = false
    private var camX = 0f
    private var camY = 0f

    init {
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val v = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
                if (world.pausedForLevel) return ui.touch(v.x, v.y)
                if (v.x > 540f) dashQueued = true
                return ui.touch(v.x, v.y)
            }
        }
        Gdx.input.setCatchKey(Input.Keys.BACK, true)
    }

    override fun render(delta: Float) {
        handleInput()
        if (!paused && !world.gameOver && !world.pausedForLevel) {
            accumulator += clamp(delta, 0f, 0.25f)
            var steps = 0
            while (accumulator >= 1f / 60f && steps < 5) {
                world.update(1f / 60f, inputX, inputY, dashQueued)
                dashQueued = false
                accumulator -= 1f / 60f
                steps++
            }
        }
        if (world.gameOver) {
            game.screen = GameOverScreen(game, world.time, world.player.kills, world.player.level)
            return
        }

        camX = lerp(camX, world.player.x, 0.1f)
        camY = lerp(camY, world.player.y, 0.1f)
        camera.position.set(camX, camY, 0f)
        beginFrame()

        drawWorld()
        drawUi()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug = !debug
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) paused = !paused
        inputX = 0f
        inputY = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.A)) inputX -= 1f
        if (Gdx.input.isKeyPressed(Input.Keys.D)) inputX += 1f
        if (Gdx.input.isKeyPressed(Input.Keys.W)) inputY += 1f
        if (Gdx.input.isKeyPressed(Input.Keys.S)) inputY -= 1f
        if (Gdx.input.isTouched) {
            val x = viewport.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)).x
            val y = viewport.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)).y
            if (x < camX) {
                inputX = clamp((x - (camX - 540f)) / 220f - 1f, -1f, 1f)
                inputY = clamp((y - (camY - 960f)) / 220f - 1f, -1f, 1f)
            } else {
                dashQueued = true
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) dashQueued = true
    }

    private fun drawWorld() {
        val cb = meta.colorblind
        shape.begin(ShapeRenderer.ShapeType.Filled)
        val bg = if (cb) Color(0.08f, 0.1f, 0.12f, 1f) else Color(0.06f, 0.06f, 0.08f, 1f)
        shape.color = bg
        shape.rect(camX - 600f, camY - 1000f, 1200f, 2000f)
        shape.color = Color(1f, 1f, 1f, 0.08f)
        for (i in -15..15) shape.rectLine(camX + i * 80f, camY - 1000f, camX + i * 80f, camY + 1000f, 1f)
        for (i in -24..24) shape.rectLine(camX - 600f, camY + i * 80f, camX + 600f, camY + i * 80f, 1f)

        for (g in world.gems) if (g.active) {
            shape.color = if (cb) Color.YELLOW else Color.CYAN
            shape.rect(g.x - 8f, g.y - 8f, 16f, 16f, 8f, 8f, 1f, 1f, world.time * 90f)
        }
        for (p in world.projectiles) if (p.active) {
            shape.color = if (p.pierce >= 0) Color(1f, 0.92f, 0.4f, 1f) else Color(1f, 0.4f, 0.95f, 1f)
            shape.circle(p.x, p.y, p.radius)
        }
        for (e in world.enemies) if (e.active) {
            shape.color = if (cb) Color(0.95f, 0.9f, 0.2f, 1f) else e.type.color
            if (e.type.name == "BOSS") {
                shape.circle(e.x, e.y, e.radius)
                shape.color = Color.BLACK
                shape.circle(e.x, e.y, e.radius - 8f)
                shape.color = if (cb) Color(1f, 0.7f, 0.2f, 1f) else e.type.color
                shape.circle(e.x, e.y, e.radius - 12f)
            } else {
                shape.circle(e.x, e.y, e.radius)
            }
        }

        val breathe = 1f + sin(world.time * 4f) * 0.06f
        shape.color = if (cb) Color.WHITE else Color(0.3f, 0.8f, 1f, 1f)
        shape.circle(world.player.x, world.player.y, 16f * breathe)
        shape.end()
    }

    private fun drawUi() {
        ui.begin()
        shape.begin(ShapeRenderer.ShapeType.Filled)
        shape.color = Color(0f, 0f, 0f, 0.45f)
        shape.rect(camX - 520f, camY + 860f, 1040f, 100f)
        val hpRatio = world.player.hp / world.player.maxHp
        shape.color = Color(0.8f, 0.2f, 0.2f, 1f)
        shape.rect(camX - 500f, camY + 892f, 400f * hpRatio, 22f)
        val xpRatio = world.player.xp / world.player.xpToLevel
        shape.color = Color(0.2f, 0.6f, 1f, 1f)
        shape.rect(camX - 500f, camY + 860f, 400f * xpRatio, 20f)
        if (paused) ui.button(camX - 210f, camY - 60f, 420f, 120f, "Resume", shape, font, game.batch) { paused = false }
        if (world.pausedForLevel) {
            shape.color = Color(0f, 0f, 0f, 0.7f)
            shape.rect(camX - 540f, camY - 960f, 1080f, 1920f)
            val picks = world.randomUpgradeSet()
            for (i in picks.indices) {
                val u = picks[i]
                ui.button(camX - 460f + i * 320f, camY - 40f, 280f, 190f, u.title, shape, font, game.batch) {
                    world.applyUpgrade(u.id)
                    if (meta.vibration && game.platformServices.canVibrate()) game.platformServices.vibrate(80)
                }
            }
        }
        shape.end()
        game.batch.begin()
        font.data.setScale(1.0f)
        font.draw(game.batch, "LV ${world.player.level}   Time ${"%.1f".format(world.time)}s   Seed ${rng.seed}", camX - 500f, camY + 935f)
        if (debug) {
            font.draw(game.batch, "FPS ${Gdx.graphics.framesPerSecond} E:${world.enemies.count { it.active }} P:${world.projectiles.count { it.active }} G:${world.gems.count { it.active }}", camX - 500f, camY + 820f)
            font.draw(game.batch, "Pos ${"%.1f".format(world.player.x)}, ${"%.1f".format(world.player.y)} Tier ${world.tier}", camX - 500f, camY + 790f)
        }
        game.batch.end()
    }

    override fun pause() {
        paused = true
    }
}
