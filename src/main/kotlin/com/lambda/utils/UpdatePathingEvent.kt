package com.lambda.utils

import baritone.api.utils.BetterBlockPos
import com.lambda.client.event.Event
import net.minecraft.util.math.BlockPos

class UpdatePathingEvent(val newPos: BlockPos, val oldPos: BlockPos) : Event