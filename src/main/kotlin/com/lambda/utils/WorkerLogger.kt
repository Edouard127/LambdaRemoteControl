package com.lambda.utils

import com.lambda.client.commons.utils.MathUtils
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.util.items.originalName
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
    fun SafeClientEvent.playerInformations(): String {
        val s = StringBuilder()
        s.append("Player: ${player.name}\n")
        s.append("Health: ${player.health}\n")
        s.append("Food: ${player.foodStats.foodLevel}\n")
        s.append("Players in render: ${mc.world.playerEntities.size}\n")
        s.append("Coordinates: ${player.position}\n")
        s.append("Main hand: ${player.heldItemMainhand.originalName}\n")
        s.append("Off hand: ${player.heldItemOffhand.originalName}\n")
        s.append(armorInformations())
        return s.toString()
    }
    fun SafeClientEvent.armorInformations(): String {
        val s = StringBuilder()
        s.append("Armor: \n")
        for ((index, itemStack) in player.armorInventoryList.reversed().withIndex()) {
            val dura = itemStack.maxDamage - itemStack.itemDamage
            val duraMultiplier = dura / itemStack.maxDamage.toFloat()
            val duraPercent = MathUtils.round(duraMultiplier * 100.0f, 1).toFloat()
            s.append("${itemStack.originalName}: $duraPercent%\n")
        }
        return s.toString()
    }
}

