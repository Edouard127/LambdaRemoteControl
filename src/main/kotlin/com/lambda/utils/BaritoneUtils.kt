package com.lambda.utils

import baritone.api.utils.BetterBlockPos
import baritone.api.utils.Helper.mc
import com.lambda.client.event.LambdaEventBus
import com.lambda.client.util.BaritoneUtils
import net.minecraft.util.math.BlockPos

class BaritoneUtils
{
    var lastPathEvent: BlockPos? = null
    fun pathingGoalCheck() {
        val goal = BaritoneUtils.primary?.pathingBehavior?.current?.path?.dest
        val newPos = mc.player.position
        if (lastPathEvent != newPos) {
            lastPathEvent?.let { oldPos ->
                LambdaEventBus.post(UpdatePathingEvent(newPos, oldPos))
                lastPathEvent = newPos
            }
        }
        if (lastPathEvent == null && goal != null) {
            LambdaEventBus.post(StartPathingEvent(newPos))
            lastPathEvent = newPos
        }
        if (lastPathEvent != null && goal == null) {
            LambdaEventBus.post(StopPathingEvent(newPos))
            lastPathEvent = null
        }
    }
}