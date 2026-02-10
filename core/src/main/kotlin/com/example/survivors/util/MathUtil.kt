package com.example.survivors.util

import com.badlogic.gdx.math.Vector2
import kotlin.math.max
import kotlin.math.min

fun clamp(value: Float, minV: Float, maxV: Float): Float = max(minV, min(maxV, value))

fun safeNormalize(v: Vector2): Vector2 {
    val len2 = v.len2()
    return if (len2 > 0.000001f) v.scl(1f / kotlin.math.sqrt(len2)) else v.setZero()
}

fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * clamp(t, 0f, 1f)
