package com.lambda.utils

import baritone.api.utils.Helper.mc
import com.lambda.client.event.LambdaEventBus
import com.lambda.enums.EJobEvents
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.math.BlockPos

// Make a class that implements Worker class
class Job
    (val type: EWorkerType,
     val goal: BlockPos,
     override var cancelled: Boolean = false,
     override var finished: Boolean = false,
     override var working: Boolean = false,
    )
    : Worker() {


    override fun getJob(): String = "Job type:${this.type.byte} Status:${BaritoneUtils().status.byte} Goal:${this.goal} Player:${mc.player.name} "+if (this.working) "Position: ${mc.player.position}" else "Scheduled"
    override fun end() {
        this.finished = true
    }

    override fun cancel() {
        this.cancelled = true
    }

    override fun emitEvent(event: EJobEvents) {
        LambdaEventBus.post(JobEvents(event, this))
    }

}



