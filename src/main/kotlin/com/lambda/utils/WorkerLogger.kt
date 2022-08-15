package com.lambda.utils

import net.minecraft.util.math.BlockPos


class WorkerLogger {
    private lateinit var lastPositions: Array<BlockPos>

    fun getLastPositions(): Array<BlockPos> {
        return lastPositions.filter { lastPositions.size < 200 }.toTypedArray()
    }
    fun saveMemory() {
        lastPositions = lastPositions.filter { lastPositions.size < 200 }.toTypedArray()
    }
    fun addPosition(pos: BlockPos) {
        lastPositions += pos
    }
    fun isWorking(): Boolean {
        return lastPositions.first() != lastPositions.last()
    }
    fun getCurrentPosition(): BlockPos {
        return lastPositions.last()
    }
}

