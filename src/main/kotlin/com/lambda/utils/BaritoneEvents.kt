package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event

class BaritoneEvents(val job: Job) : Event, Cancellable()