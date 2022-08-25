package com.lambda.utils

import com.lambda.enums.EJobEvents
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import net.minecraft.util.math.BlockPos


abstract class Worker {

    abstract var cancelled: Boolean
    abstract var finished: Boolean
    abstract var working: Boolean

    abstract fun emitEvent(event: EJobEvents)
    abstract fun getJob(): String
    abstract fun end(): Unit
    abstract fun cancel(): Unit
}