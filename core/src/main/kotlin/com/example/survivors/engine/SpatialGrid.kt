package com.example.survivors.engine

import kotlin.math.floor

class SpatialGrid(
    private val cellSize: Float,
    private val widthCells: Int,
    private val heightCells: Int,
    private val worldHalf: Float
) {
    private val cells = IntArray(widthCells * heightCells * 32)
    private val counts = IntArray(widthCells * heightCells)

    fun clear() {
        java.util.Arrays.fill(counts, 0)
    }

    fun insert(index: Int, x: Float, y: Float) {
        val cx = ((x + worldHalf) / cellSize).toInt().coerceIn(0, widthCells - 1)
        val cy = ((y + worldHalf) / cellSize).toInt().coerceIn(0, heightCells - 1)
        val c = cy * widthCells + cx
        val offset = c * 32 + counts[c]
        if (counts[c] < 32) {
            cells[offset] = index
            counts[c]++
        }
    }

    inline fun query(x: Float, y: Float, radius: Float, callback: (Int) -> Unit) {
        val minX = floor((x - radius + worldHalf) / cellSize).toInt().coerceIn(0, widthCells - 1)
        val maxX = floor((x + radius + worldHalf) / cellSize).toInt().coerceIn(0, widthCells - 1)
        val minY = floor((y - radius + worldHalf) / cellSize).toInt().coerceIn(0, heightCells - 1)
        val maxY = floor((y + radius + worldHalf) / cellSize).toInt().coerceIn(0, heightCells - 1)
        for (cy in minY..maxY) for (cx in minX..maxX) {
            val c = cy * widthCells + cx
            val count = counts[c]
            val start = c * 32
            for (i in 0 until count) callback(cells[start + i])
        }
    }
}
