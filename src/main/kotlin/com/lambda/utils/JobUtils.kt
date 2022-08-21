package com.lambda.utils

import com.lambda.client.util.math.VectorUtils.distanceTo
import com.lambda.client.util.threads.defaultScope
import com.lambda.enums.EJobEvents
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation

class JobUtils {
    val jobs = mutableListOf<Job>()

    fun checkJobs() {
        if (jobs.isNotEmpty()) {
            for (job in jobs) {
                if (job.cancelable && job.cancelled) {
                    job.cancel()
                }
                if (job.player.position.distanceTo(job.destination) < 10) {
                    job.emitEvent(EJobEvents.JOB_INITIALIZED)
                }
                if (job.player.health <= 0) {
                    job.emitEvent(EJobEvents.JOB_FAILED)
                }
                /*if (job.player.shouldHeal())*/
            }
        }

    }
}