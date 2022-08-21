package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event
import com.lambda.enums.EJobEvents

class JobEvents(val event: EJobEvents, val instance: Job) : Event, Cancellable()