package com.lambda.interfaces

import com.lambda.utils.Job

interface IHighwayToolsHandler {
    fun parse(data: ByteArray)
    fun createJob(args: Job)
}