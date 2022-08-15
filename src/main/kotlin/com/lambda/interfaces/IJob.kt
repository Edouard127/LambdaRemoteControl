package com.lambda.interfaces

import com.lambda.client.event.SafeClientEvent
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import net.minecraft.util.math.BlockPos


interface IJob {
    fun create(type: EWorkerType, position: BlockPos): Worker
    fun destroy()
    fun SafeClientEvent.status(): EWorkerStatus
}