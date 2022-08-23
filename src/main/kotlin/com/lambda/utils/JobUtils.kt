package com.lambda.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.client.util.BaritoneUtils
import com.lambda.client.util.math.VectorUtils.distanceTo
import com.lambda.client.util.text.MessageSendHelper
import com.lambda.enums.EJobEvents

class JobUtils(private val jobs: MutableList<Job> = mutableListOf()) {

    fun checkJobs() {
        if (jobs.isNotEmpty()) {
            Debug.log("Job(s) found")
            jobs.first().run {
                //val process = GoalNear(player.position, 2147483647).goalPos ?: return

                if (player.position.distanceTo(this.goal) < 3) {
                    this.isDone = true
                    jobs.remove(this)
                }
                if (!BaritoneUtils.isPathing && !BaritoneUtils.isActive) {
                    if (!this.isDone) LambdaEventBus.post(BaritoneEvents(job = this))
                }
            }
        }
    }
    fun executeJob(job: Job) {
        job.emitEvent(EJobEvents.JOB_STARTED)
        MessageSendHelper.sendBaritoneCommand(*job.args)
        /* So the job is executed if thisway is used */
        MessageSendHelper.sendBaritoneCommand("path")
    }
    fun addJob(job: Job) {
        jobs.add(job)
    }
    fun removeJob(job: Job) {
        jobs.remove(job)
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
}