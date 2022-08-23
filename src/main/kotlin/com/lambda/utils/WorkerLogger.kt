package com.lambda.utils

import baritone.api.BaritoneAPI
import baritone.api.utils.Helper.mc
import com.lambda.client.commons.utils.MathUtils
import com.lambda.client.event.SafeClientEvent
import com.lambda.client.util.Wrapper.player
import com.lambda.client.util.items.originalName
import com.lambda.client.util.math.VectorUtils.distanceTo
import net.minecraft.util.math.BlockPos


class WorkerLogger {
    private var lastPositions: Array<BlockPos> = arrayOf(BlockPos.ORIGIN)

    fun getLastPositions(n: Int = 200): Array<BlockPos> {
        return lastPositions.filter { lastPositions.size < n }.toTypedArray()
    }
    fun saveMemory() {
        lastPositions = lastPositions.filter { lastPositions.size < 200 }.toTypedArray()
    }
    fun shouldSaveMemory(): Boolean {
        return lastPositions.size > 200
    }
    fun addPosition(pos: BlockPos) {
        lastPositions += pos
    }
    fun isWorking(): Boolean {
        var l = false
        getLastPositions(5).forEach {
            if (it.distanceTo(lastPositions.last()) < 1) {
                l = true
            }
        }
        return l
    }
    fun getCurrentPosition(): BlockPos {
        return lastPositions.lastOrNull() ?: BlockPos.ORIGIN
    }
    fun getNLast(n: Int) = lastPositions.getOrNull(lastPositions.size-n) ?: BlockPos.ORIGIN
    fun playerInformations(): String {
        val s = StringBuilder()
        s.append("Player:${mc.player.name.joinToString()} ")
        s.append("Health:${mc.player.health} ")
        s.append("Food:${mc.player.foodStats.foodLevel} ")
        s.append("PlayersRender:${mc.world.playerEntities.size} ")
        s.append("Coordinates:${mc.player.position} ")
        s.append("MainHand:${mc.player.heldItemMainhand.originalName.joinToString()} ")
        s.append("OffHand:${mc.player.heldItemOffhand.originalName.joinToString()} ")
        s.append("Working:${BaritoneAPI.getProvider().primaryBaritone.pathingBehavior.isPathing} ")
        s.append(armorInformations())
        return s.toString()
    }
    fun armorInformations(): String {
        val s = StringBuilder()
        s.append("Armor:")
        for (itemStack in mc.player.armorInventoryList.reversed()) {
            if (itemStack.isEmpty) continue
            val dura = itemStack.maxDamage - itemStack.itemDamage
            val duraMultiplier = dura / itemStack.maxDamage.toFloat()
            val duraPercent = MathUtils.round(duraMultiplier * 100.0f, 1).toFloat()
            s.append("${itemStack.originalName}: $duraPercent% ")
        }
        return s.toString()
    }
}

fun String.joinToString(): String {
    return this.split(" ").joinToString("")
}

