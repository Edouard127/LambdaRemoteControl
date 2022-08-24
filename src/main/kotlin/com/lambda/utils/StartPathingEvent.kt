package com.lambda.utils

import com.lambda.client.event.Cancellable
import com.lambda.client.event.Event
import net.minecraft.util.math.BlockPos

class StartPathingEvent(val goal: BlockPos) : Event, Cancellable()