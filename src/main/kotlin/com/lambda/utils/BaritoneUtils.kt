package com.lambda.utils

import baritone.api.utils.Helper.mc
import com.lambda.client.event.LambdaEventBus
import com.lambda.client.util.BaritoneUtils
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.enums.EWorkerStatus
import com.lambda.events.StartPathingEvent
import com.lambda.events.StopPathingEvent
import com.lambda.events.UpdatePathingEvent
import net.minecraft.util.math.BlockPos

class BaritoneUtils
{
    private var lastPathEvent: BlockPos? = null
    private val queuedCommands = mutableListOf<String>()
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
            LambdaEventBus.post(StartPathingEvent(goal))
            lastPathEvent = newPos
        }
        if (lastPathEvent != null && goal == null) {
            LambdaEventBus.post(StopPathingEvent(newPos))
            lastPathEvent = null
        }
    }
    fun commandQueueCheck() {
        if (status == EWorkerStatus.IDLE) {
            queuedCommands.removeFirstOrNull()?.run {
                MessageSendHelper.sendBaritoneCommand(this)
            }
        }
    }
    fun queueCommand(command: String) {
        queuedCommands.add(command)
    }
    fun removeCommandAt(index: Int = 0) {
        queuedCommands.removeAt(index)
    }
    val status: EWorkerStatus
        get() = if (BaritoneUtils.primary?.pathingBehavior?.current?.path?.dest != null) EWorkerStatus.BUSY else EWorkerStatus.IDLE

}