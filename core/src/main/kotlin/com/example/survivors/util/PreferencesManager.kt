package com.example.survivors.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

data class MetaData(
    var gold: Int = 0,
    var maxHp: Int = 0,
    var moveSpeed: Int = 0,
    var magnet: Int = 0,
    var damage: Int = 0,
    var xpGain: Int = 0,
    var bestTime: Float = 0f,
    var bestKills: Int = 0,
    var colorblind: Boolean = false,
    var vibration: Boolean = true,
    var deterministic: Boolean = false,
    var seed: Long = 1337L
)

class PreferencesManager {
    private val prefs: Preferences = Gdx.app.getPreferences("survivors_meta_v1")

    fun load(): MetaData = MetaData(
        gold = prefs.getInteger("gold", 0),
        maxHp = prefs.getInteger("u_max_hp", 0),
        moveSpeed = prefs.getInteger("u_move_speed", 0),
        magnet = prefs.getInteger("u_magnet", 0),
        damage = prefs.getInteger("u_damage", 0),
        xpGain = prefs.getInteger("u_xp_gain", 0),
        bestTime = prefs.getFloat("best_time", 0f),
        bestKills = prefs.getInteger("best_kills", 0),
        colorblind = prefs.getBoolean("colorblind", false),
        vibration = prefs.getBoolean("vibration", true),
        deterministic = prefs.getBoolean("deterministic", false),
        seed = prefs.getLong("seed", 1337L)
    )

    fun save(meta: MetaData) {
        prefs.putInteger("gold", meta.gold)
        prefs.putInteger("u_max_hp", meta.maxHp)
        prefs.putInteger("u_move_speed", meta.moveSpeed)
        prefs.putInteger("u_magnet", meta.magnet)
        prefs.putInteger("u_damage", meta.damage)
        prefs.putInteger("u_xp_gain", meta.xpGain)
        prefs.putFloat("best_time", meta.bestTime)
        prefs.putInteger("best_kills", meta.bestKills)
        prefs.putBoolean("colorblind", meta.colorblind)
        prefs.putBoolean("vibration", meta.vibration)
        prefs.putBoolean("deterministic", meta.deterministic)
        prefs.putLong("seed", meta.seed)
        prefs.flush()
    }
}
