package com.lambda.classes.worker.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.enums.EJobEvents
import com.lambda.events.JobEvents
import com.lambda.classes.worker.Job

class JobUtils(private val jobs: MutableList<JobTracker> = mutableListOf()) {

    private var jobEvent: JobTracker? = null
    fun checkJobs() {
        if (jobs.isNotEmpty()) {
            jobs.firstOrNull()?.run {
                if (jobEvent != this) {
                    jobEvent = this
                    LambdaEventBus.post(JobEvents(event = EJobEvents.JOB_STARTED, instance = this.job))
                }
                if (this.job.finished) {
                    LambdaEventBus.post(JobEvents(event = EJobEvents.JOB_FINISHED, instance = this.job))
                    jobs.remove(this)
                    jobEvent = null
                }
            } ?: run {
                LambdaEventBus.post(JobEvents(event = EJobEvents.JOB_FINISHED, instance = null))
            }
        }
    }
    fun currentJob(): JobTracker? = jobs.firstOrNull()
    fun getJobsString(): String = jobs.joinToString(", ") { it.job.getJob() }
    fun addJob(job: JobTracker) {
        jobs.add(job)
    }
}