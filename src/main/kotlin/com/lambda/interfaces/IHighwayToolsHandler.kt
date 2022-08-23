package com.lambda.interfaces

import com.lambda.enums.EHighwayArguments
import com.lambda.utils.Job

interface IHighwayToolsHandler {
    fun getPacket(): EHighwayArguments
    fun getArguments(): Array<String>
    fun createJob(args: Job)
}