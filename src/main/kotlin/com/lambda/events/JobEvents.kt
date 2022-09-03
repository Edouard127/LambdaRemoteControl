package com.lambda.events

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event
import com.lambda.enums.EJobEvents
import com.lambda.classes.worker.Job

class JobEvents(val event: EJobEvents, val instance: Job?) : Event, Cancellable()