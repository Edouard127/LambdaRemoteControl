package com.lambda.interfaces

import com.lambda.client.event.SafeClientEvent
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import com.lambda.classes.worker.Worker
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.math.BlockPos


interface IJob {
    fun create(type: EWorkerType, position: BlockPos, entity: EntityPlayerSP): Worker
    fun destroy()
    fun SafeClientEvent.status(): EWorkerStatus
}