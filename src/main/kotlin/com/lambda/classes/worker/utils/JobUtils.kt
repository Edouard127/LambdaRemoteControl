package com.lambda.classes.worker.utils

import com.lambda.client.event.LambdaEventBus
import com.lambda.enums.EJobEvents
import com.lambda.events.JobEvents
import com.lambda.classes.worker.Job

class JobUtils(private val jobs: MutableList<JobTracker> = mutableListOf()) {

    private var jobEvent: JobTracker? = null
    fun checkJobs() {
        if (jobs.isNotEmpty()) {
            jobs.firstOrNull().run {
                if (this != null) {
                    if (jobEvent != this) {
                        jobEvent = this
                        LambdaEventBus.post(JobEvents(event = EJobEvents.JOB_STARTED, instance = this.job))
                    }
                    if (this.job.finished) {
                        LambdaEventBus.post(JobEvents(event = EJobEvents.JOB_FINISHED, instance = this.job))
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
    fun currentJob(): JobTracker? = jobs.firstOrNull()
    fun getJobsString(): String = jobs.joinToString { it.job.getJob() } + "\n"
    fun addJob(job: JobTracker) {
        jobs.add(job)
    }
}