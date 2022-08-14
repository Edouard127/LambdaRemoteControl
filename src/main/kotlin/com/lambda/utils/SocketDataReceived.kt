package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event
import com.lambda.interfaces.FlagType
import com.lambda.interfaces.Packet

class SocketDataReceived(val bit: Packet, val type: FlagType, vararg val args: Array<String>) : Event, Cancellable()