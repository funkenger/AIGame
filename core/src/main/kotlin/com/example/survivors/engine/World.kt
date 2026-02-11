package com.example.survivors.engine

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.example.survivors.entities.*
import com.example.survivors.util.GameRng
import com.example.survivors.util.MetaData
import com.example.survivors.util.clamp
import kotlin.math.cos
import kotlin.math.sin

data class Upgrade(val id: String, val title: String, val cap: Int)

class World(meta: MetaData, private val rng: GameRng) {
    val player = Player()
    val enemies = ArrayList<Enemy>(400)
    val projectiles = ArrayList<Projectile>(900)
    val gems = ArrayList<Gem>(900)
    val particles = ArrayList<Particle>(1200)
    val grid = SpatialGrid(100f, 160, 160, 8000f)

    var time = 0f
    var spawnTimer = 0f
    var attackTimer = 0f
    var bossTimer = 120f
    var pausedForLevel = false
    var gameOver = false
    var tier = 0

    var damage = 8f + meta.damage
    var attackCooldown = 0.8f
    var projectileSpeed = 540f
    var multishot = 1
    var pierce = 0
    var area = 1f
    var magnet = 130f + meta.magnet * 20f
    var regen = 0f
    var crit = 0.05f
    var xpGain = 1f + meta.xpGain * 0.1f

    val upgradeLevels = HashMap<String, Int>()
    val allUpgrades = listOf(
        Upgrade("fire_rate", "Fire Rate", 8), Upgrade("damage", "Damage", 10), Upgrade("proj_speed", "Projectile Speed", 8),
        Upgrade("multishot", "Multi Shot", 5), Upgrade("pierce", "Pierce", 6), Upgrade("area", "Area", 8),
        Upgrade("magnet", "Magnet", 8), Upgrade("max_hp", "Max HP", 8), Upgrade("regen", "Regen", 6),
        Upgrade("dash_cd", "Dash Cooldown", 7), Upgrade("crit", "Crit Chance", 8), Upgrade("xp", "XP Gain", 10)
    )

    init {
        player.maxHp += meta.maxHp * 10f
        player.hp = player.maxHp
        player.speed += meta.moveSpeed * 18f
    }

    fun randomUpgradeSet(): List<Upgrade> {
        val picks = ArrayList<Upgrade>(3)
        var guard = 0
        while (picks.size < 3 && guard < 50) {
            val u = allUpgrades[rng.nextInt(allUpgrades.size)]
            if ((upgradeLevels[u.id] ?: 0) < u.cap && !picks.contains(u)) picks.add(u)
            guard++
        }
        return if (picks.isEmpty()) allUpgrades.take(3) else picks
    }

    fun applyUpgrade(id: String) {
        val next = (upgradeLevels[id] ?: 0) + 1
        upgradeLevels[id] = next
        when (id) {
            "fire_rate" -> attackCooldown = clamp(attackCooldown - 0.05f, 0.2f, 2f)
            "damage" -> damage += 2f
            "proj_speed" -> projectileSpeed += 35f
            "multishot" -> multishot++
            "pierce" -> pierce++
            "area" -> area += 0.1f
            "magnet" -> magnet += 22f
            "max_hp" -> { player.maxHp += 8f; player.hp += 8f }
            "regen" -> regen += 0.25f
            "dash_cd" -> player.dashCooldown = clamp(player.dashCooldown - 0.1f, 0.5f, 3f)
            "crit" -> crit = clamp(crit + 0.05f, 0f, 0.65f)
            "xp" -> xpGain += 0.08f
        }
        pausedForLevel = false
    }

    fun update(step: Float, inputX: Float, inputY: Float, dash: Boolean) {
        if (gameOver || pausedForLevel) return
        time += step
        bossTimer -= step
        tier = when {
            time >= 270f -> 4
            time >= 180f -> 3
            time >= 90f -> 2
            else -> 1
        }
        player.invuln -= step
        player.dashTimer -= step
        if (regen > 0f) player.hp = clamp(player.hp + regen * step, 0f, player.maxHp)

        val len2 = inputX * inputX + inputY * inputY
        val invLen = if (len2 > 0.0001f) 1f / kotlin.math.sqrt(len2) else 0f
        val dirX = inputX * invLen
        val dirY = inputY * invLen
        var speedMul = 1f
        if (dash && player.dashTimer <= 0f && len2 > 0f) {
            speedMul = 3.8f
            player.dashTimer = player.dashCooldown
        }
        player.vx = dirX * player.speed * speedMul
        player.vy = dirY * player.speed * speedMul
        player.x += player.vx * step
        player.y += player.vy * step

        spawnTimer -= step
        if (spawnTimer <= 0f) {
            spawnWave()
            spawnTimer = clamp(1.1f - time * 0.0025f, 0.22f, 1.1f)
        }
        if (bossTimer <= 0f) {
            spawnEnemy(EnemyType.BOSS)
            bossTimer = 120f
        }

        attackTimer -= step
        if (attackTimer <= 0f) {
            shootAtNearest()
            attackTimer = attackCooldown
        }

        grid.clear()
        for (i in enemies.indices) if (enemies[i].active) grid.insert(i, enemies[i].x, enemies[i].y)

        updateEnemies(step)
        updateProjectiles(step)
        updateGems(step)
        updateParticles(step)

        if (player.hp <= 0f) gameOver = true
    }

    private fun spawnWave() {
        val count = 1 + (time / 22f).toInt().coerceAtMost(8)
        repeat(count) {
            val type = when {
                time > 270f && rng.nextFloat() < 0.22f -> EnemyType.RANGED
                time > 180f && rng.nextFloat() < 0.2f -> EnemyType.TANK
                time > 90f && rng.nextFloat() < 0.33f -> EnemyType.FAST
                else -> EnemyType.BASIC
            }
            spawnEnemy(type)
        }
    }

    private fun spawnEnemy(type: EnemyType) {
        val angle = rng.range(0f, MathUtils.PI2)
        val dist = rng.range(950f, 1200f)
        val x = player.x + cos(angle) * dist
        val y = player.y + sin(angle) * dist
        if ((x - player.x) * (x - player.x) + (y - player.y) * (y - player.y) < 350f * 350f) return
        val e = enemies.firstOrNull { !it.active } ?: Enemy().also { enemies.add(it) }
        e.active = true
        e.type = type
        e.x = x
        e.y = y
        e.hp = type.baseHp + time * 0.22f
        e.radius = if (type == EnemyType.BOSS) 52f else 22f + type.ordinal * 2f
    }

    private fun shootAtNearest() {
        var nearest: Enemy? = null
        var best = Float.MAX_VALUE
        for (e in enemies) if (e.active) {
            val dx = e.x - player.x
            val dy = e.y - player.y
            val d2 = dx * dx + dy * dy
            if (d2 < best) {
                best = d2
                nearest = e
            }
        }
        nearest ?: return
        val baseAngle = MathUtils.atan2(nearest!!.y - player.y, nearest!!.x - player.x)
        for (i in 0 until multishot) {
            val spread = (i - (multishot - 1) * 0.5f) * 0.14f
            spawnProjectile(baseAngle + spread)
        }
    }

    private fun spawnProjectile(angle: Float) {
        val p = projectiles.firstOrNull { !it.active } ?: Projectile().also { projectiles.add(it) }
        p.active = true
        p.x = player.x
        p.y = player.y
        p.vx = cos(angle) * projectileSpeed
        p.vy = sin(angle) * projectileSpeed
        p.life = 2f
        p.damage = if (rng.nextFloat() < crit) damage * 2f else damage
        p.radius = 6f * area
        p.pierce = pierce
    }

    private fun updateEnemies(step: Float) {
        for (e in enemies) if (e.active) {
            val dx = player.x - e.x
            val dy = player.y - e.y
            val inv = 1f / kotlin.math.sqrt(dx * dx + dy * dy + 0.0001f)
            e.x += dx * inv * e.type.speed * step
            e.y += dy * inv * e.type.speed * step
            if (e.type == EnemyType.RANGED) {
                e.shootTimer -= step
                if (e.shootTimer <= 0f) {
                    val angle = MathUtils.atan2(player.y - e.y, player.x - e.x)
                    val p = projectiles.firstOrNull { !it.active } ?: Projectile().also { projectiles.add(it) }
                    p.active = true; p.x = e.x; p.y = e.y
                    p.vx = cos(angle) * 260f; p.vy = sin(angle) * 260f
                    p.damage = e.type.damage; p.life = 4f; p.radius = 5f; p.pierce = -1
                    e.shootTimer = 1.6f
                }
            }
            val rr = (e.radius + 16f)
            if (dx * dx + dy * dy < rr * rr && player.invuln <= 0f) {
                player.hp = clamp(player.hp - e.type.damage, 0f, player.maxHp)
                player.invuln = 0.5f
            }
        }
    }

    private fun updateProjectiles(step: Float) {
        for (p in projectiles) if (p.active) {
            p.x += p.vx * step
            p.y += p.vy * step
            p.life -= step
            if (p.life <= 0f) p.active = false
            if (!p.active) continue

            grid.query(p.x, p.y, 80f) { idx ->
                val e = enemies[idx]
                if (!e.active) return@query
                val dx = e.x - p.x
                val dy = e.y - p.y
                val rr = e.radius + p.radius
                if (dx * dx + dy * dy <= rr * rr) {
                    if (p.pierce >= 0) {
                        e.hp -= p.damage
                        p.pierce--
                        if (p.pierce < 0) p.active = false
                    } else if (p.damage < 20f) {
                        player.hp = clamp(player.hp - p.damage, 0f, player.maxHp)
                        p.active = false
                    }
                    if (e.hp <= 0f) {
                        e.active = false
                        player.kills++
                        spawnGem(e.x, e.y, if (e.type == EnemyType.BOSS) 12f else 2f)
                    }
                }
            }
        }
    }

    private fun spawnGem(x: Float, y: Float, value: Float) {
        val g = gems.firstOrNull { !it.active } ?: Gem().also { gems.add(it) }
        g.active = true
        g.x = x
        g.y = y
        g.value = value
    }

    private fun updateGems(step: Float) {
        for (g in gems) if (g.active) {
            val dx = player.x - g.x
            val dy = player.y - g.y
            val d2 = dx * dx + dy * dy
            if (d2 < magnet * magnet) {
                val inv = 1f / kotlin.math.sqrt(d2 + 0.0001f)
                g.x += dx * inv * 360f * step
                g.y += dy * inv * 360f * step
            }
            if (d2 < 26f * 26f) {
                g.active = false
                player.xp += g.value * xpGain
                if (player.xp >= player.xpToLevel) {
                    player.xp -= player.xpToLevel
                    player.level++
                    player.xpToLevel = 10f + player.level * 4.4f
                    pausedForLevel = true
                }
            }
        }
    }

    private fun updateParticles(step: Float) {
        for (pt in particles) if (pt.active) {
            pt.life -= step
            pt.x += pt.vx * step
            pt.y += pt.vy * step
            if (pt.life <= 0f) pt.active = false
        }
    }
}
