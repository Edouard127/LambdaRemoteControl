package com.lambda.utils

import com.lambda.enums.EJobEvents
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import net.minecraft.util.math.BlockPos


abstract class Worker {

    abstract val cancelled: Boolean

    abstract val cancelable: Boolean
    abstract fun store()
    abstract fun run()
    abstract fun getProgress(): String
    abstract fun emitEvent(event: EJobEvents)
    abstract fun getPos(): BlockPos
    abstract fun getStatus(): EWorkerStatus
    abstract fun getJob(): String
    abstract fun remove()

    abstract fun cancel()
}