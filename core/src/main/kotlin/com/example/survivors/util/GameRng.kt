package com.example.survivors.util

import kotlin.random.Random

class GameRng(seed: Long) {
    private var random = Random(seed)
    var seed: Long = seed
        private set

    fun reseed(newSeed: Long) {
        seed = newSeed
        random = Random(newSeed)
    }

    fun nextFloat() = random.nextFloat()
    fun range(min: Float, max: Float): Float = min + (max - min) * nextFloat()
    fun nextInt(maxExclusive: Int): Int = random.nextInt(maxExclusive)
}
