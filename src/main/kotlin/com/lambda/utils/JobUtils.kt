package com.lambda.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.enums.EJobEvents

class JobUtils(private val jobs: MutableList<Job> = mutableListOf()) {

    var jobEvent: Job? = null
    fun checkJobs() {
        if (jobs.isNotEmpty()) {
            jobs.firstOrNull().run {
                if (this != null) {
                    if (jobEvent != this) {
                        jobEvent = this
                        LambdaEventBus.post(JobEvents(event = EJobEvents.JOB_STARTED, instance = this))
                    }
                    if (this.finished) {
                        LambdaEventBus.post(JobEvents(event = EJobEvents.JOB_FINISHED, instance = this))
                        jobs.remove(this)
                        jobEvent = null
                    }
                }
                if (jobEvent != null && this == null) {
                    // All jobs are done
                }
            }
        }
    }
    fun currentJob(): Job? = jobs.firstOrNull()

    fun getJobs(): List<Job> = jobs

    fun getJobsString(): String = jobs.joinToString { it.getJob() } + "\n"
    fun finishJob() {
        currentJob()?.run {
            this.finished = true
        }
    }
    fun executeJob(job: Job) {
        job.emitEvent(EJobEvents.JOB_STARTED)
    }
    fun addJob(job: Job) {
        jobs.add(job)
    }
    fun removeJob(job: Job) {
        jobs.remove(job)
    }
    fun remoteJob(job: Int) {
        jobs.removeAt(job)
    }
    fun cancelJob(job: Job) {
        jobs.remove(job)
        job.emitEvent(EJobEvents.JOB_CANCELLED)
        //job.cancel()
    }
    fun cancelAllJobs() {
        jobs.forEach { it.cancel() }
    }
    fun cancelAllJobsExcept(job: Job) {
        jobs.forEach { if (it != job) it.cancel() }
    }
}