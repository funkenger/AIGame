package com.example.survivors.entities

import com.badlogic.gdx.graphics.Color

class Player {
    var x = 0f
    var y = 0f
    var vx = 0f
    var vy = 0f
    var hp = 100f
    var maxHp = 100f
    var speed = 260f
    var invuln = 0f
    var dashCooldown = 1.5f
    var dashTimer = 0f
    var level = 1
    var xp = 0f
    var xpToLevel = 10f
    var kills = 0
}

enum class EnemyType(val baseHp: Float, val speed: Float, val damage: Float, val color: Color) {
    BASIC(20f, 90f, 10f, Color(0.9f, 0.3f, 0.3f, 1f)),
    FAST(14f, 140f, 8f, Color(1f, 0.65f, 0.2f, 1f)),
    TANK(55f, 65f, 18f, Color(0.5f, 0.8f, 0.4f, 1f)),
    RANGED(28f, 75f, 12f, Color(0.6f, 0.45f, 1f, 1f)),
    BOSS(450f, 72f, 25f, Color(1f, 0.2f, 0.7f, 1f))
}

class Enemy {
    var active = false
    var x = 0f
    var y = 0f
    var hp = 1f
    var radius = 22f
    var type = EnemyType.BASIC
    var shootTimer = 0f
}

class Projectile {
    var active = false
    var x = 0f
    var y = 0f
    var vx = 0f
    var vy = 0f
    var life = 2f
    var damage = 8f
    var radius = 7f
    var pierce = 0
}

class Gem {
    var active = false
    var x = 0f
    var y = 0f
    var value = 1f
}

class Particle {
    var active = false
    var x = 0f
    var y = 0f
    var vx = 0f
    var vy = 0f
    var life = 0f
    var maxLife = 0f
}
