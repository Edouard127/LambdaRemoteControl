package com.lambda.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.client.util.BaritoneUtils
import com.lambda.client.util.math.VectorUtils.distanceTo
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.client.util.threads.defaultScope
import com.lambda.enums.EJobEvents
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.math.log

class JobUtils(val logger: WorkerLogger, private val jobs: MutableList<Job> = mutableListOf()) {


    fun checkJobs() {
        println("Checking jobs")
        defaultScope.launch {
            jobs.firstOrNull()?.let {
                LambdaEventBus.post(BaritoneEvents(job = it))
                while (!it.isDone) {
                    it.isDone = !pathing
                    if (it.isDone) {
                        jobs.remove(it)
                    }
                }
            }
        }
    }
    fun executeJob(job: Job) {
        job.emitEvent(EJobEvents.JOB_STARTED)
        MessageSendHelper.sendBaritoneCommand(*job.args)
        /* So the job is executed if thisway is used */
        MessageSendHelper.sendBaritoneCommand("path")
        running = true
    }
    fun addJob(job: Job) {
        jobs.add(job)
        println(jobs.size)
    }
    fun removeJob(job: Job) {
        jobs.remove(job)
        running = false
    }
    fun cancelJob(job: Job) {
        job.cancel()
    }
    fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
    }
    fun cancelAllJobsExcept(job: Job) {
        jobs.forEach { if (it != job) it.cancel() }
    }

    val pathing
        get() = BaritoneUtils.isPathing && BaritoneUtils.isActive
    var running = false

    // Make a function to add a job to the list and execute it once the job is finished
}