package com.lambda.classes.worker

import com.lambda.enums.EJobEvents
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.math.BlockPos


abstract class Worker {

    var cancelled: Boolean = false
    var finished: Boolean = false
    var working: Boolean = false
    abstract val entity: EntityPlayerSP

    abstract fun emitEvent(event: EJobEvents)
    abstract fun getJob(): String
    abstract fun end(): Unit
    abstract fun cancel(): Unit
}