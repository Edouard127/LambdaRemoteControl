package com.lambda.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.enums.EJobEvents
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.math.BlockPos

// Make a class that implements Worker class
class Job
    (val type: EWorkerType,
     val destination: BlockPos,
     override val cancelable: Boolean = true,
     val player: EntityPlayerSP,
     private val jobs: JobUtils,
     override val cancelled: Boolean = false,
     var isDone: Boolean = false,
     val args: Array<String>)
    : Worker() {

    override fun store() {
        // Store the job in the job list
        jobs.addJob(this)
        this.emitEvent(EJobEvents.JOB_STARTED)
        Debug.log("Job started")
    }

    override fun run() {
        TODO("Not yet implemented")
    }

    override fun getProgress(): String {
        TODO("Not yet implemented")
    }

    override fun remove() {
        // Remove the job from the job list
        jobs.removeJob(this)
        this.emitEvent(EJobEvents.JOB_CANCELLED)
        Debug.log("Job cancelled")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    override fun getJob(): String {
        val job = StringBuilder()
        job.append("Job type:${this.type.byte} ")
        job.append("Destination:${this.destination.x},${this.destination.y},${this.destination.z} ")
        job.append("Cancelable:${this.cancelable} ")
        job.append("Player:${this.player.name} ")
        return job.toString()
    }

    override fun emitEvent(event: EJobEvents) {
        LambdaEventBus.post(JobEvents(event, this))
    }

    override fun getPos(): BlockPos {
        TODO("Not yet implemented")
    }

    override fun getStatus(): EWorkerStatus {
        TODO("Not yet implemented")
    }

    /*override val cancelled: Boolean
        get() = cancelled

    override fun cancel() {
        cancelled = true
    }*/
    /*override fun getStatus(): EWorkerStatus {
        return Jobs
    }
    override fun getPos(): BlockPos {
        return BlockPos.ORIGIN
    }*/

}



