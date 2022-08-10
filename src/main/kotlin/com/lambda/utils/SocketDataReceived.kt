package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event

class SocketDataReceived(val bit: List<String>, vararg val args: Array<String>) : Event, Cancellable()