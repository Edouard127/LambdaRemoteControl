package com.lambda.utils

import com.lambda.client.event.SafeClientEvent
import com.lambda.enums.EWorkerStatus
import com.lambda.enums.EWorkerType
import com.lambda.interfaces.IJob
import net.minecraft.util.math.BlockPos

class Job : IJob {
    override fun create(type: EWorkerType, position: BlockPos): Worker {
        return Worker(type, position)
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun SafeClientEvent.status(): EWorkerStatus {
        val worker = getWorker()
        return TODO("Not yet implemented")
    }
    fun getWorker(): Worker {
        TODO("Not yet implemented")
    }

}



