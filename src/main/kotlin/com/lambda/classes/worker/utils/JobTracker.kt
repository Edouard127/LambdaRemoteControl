package com.lambda.classes.worker.utils

import com.lambda.classes.worker.Job
import com.lambda.classes.worker.Worker
import com.lambda.client.event.LambdaEventBus
import com.lambda.client.util.math.VectorUtils.distanceTo
import com.lambda.client.util.threads.safeListener
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.gameevent.TickEvent

class JobTracker(private val worker: Job, private val length: Int = 30) {
    private val log = ArrayDeque<BlockPos>()
    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (it.phase != TickEvent.Phase.END) return@safeListener
            synchronized(Any()) {
                if (log.size >= length) log.removeFirst()
                log.addLast(worker.entity.position)
            }
        }
    }
    fun calcBlocksPerSeconds(): Double {
        synchronized(Any()) {
            if (log.size < 2) return 0.0
            val time = (log.last().distanceTo(log.first()) / length) * 20.0
            if (time == 0.0) return 0.0
            return log.size / time
        }
    }
    fun isStuck(): Boolean {
        synchronized(Any()) {
            if (log.size < 2) return false
            val time = (log.last().distanceTo(log.first()) / length) * 20.0
            if (time == 0.0) return false
            return log.size / time < 0.5
        }
    }
    val job: Job = worker
    init {
        LambdaEventBus.subscribe(this)
    }
}