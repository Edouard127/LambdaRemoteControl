package com.lambda.classes.worker

import baritone.api.utils.Helper.mc
import com.lambda.client.event.LambdaEventBus
import com.lambda.enums.EJobEvents
import com.lambda.enums.EWorkerType
import com.lambda.events.JobEvents
import com.lambda.utils.BaritoneUtils
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.math.BlockPos

// Make a class that implements Worker class
class Job
    (val type: EWorkerType, val goal: BlockPos, override val entity: EntityPlayerSP)
    : Worker() {


    override fun getJob(): String = "Job;type+${this.type.byte}:Status+${BaritoneUtils().status.byte}:Goal+${this.goal}:Player+${mc.player.name}:Position+${mc.player.position}"
    override fun end() {
        this.finished = true
        this.emitEvent(EJobEvents.JOB_FINISHED)
    }

    override fun cancel() {
        this.cancelled = true
        this.emitEvent(EJobEvents.JOB_CANCELLED)
    }

    override fun emitEvent(event: EJobEvents) {
        LambdaEventBus.post(JobEvents(event, this))
    }

}



