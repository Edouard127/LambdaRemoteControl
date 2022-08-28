package com.lambda.events

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event
import net.minecraft.util.math.BlockPos

class StartPathingEvent(val goal: BlockPos) : Event, Cancellable()