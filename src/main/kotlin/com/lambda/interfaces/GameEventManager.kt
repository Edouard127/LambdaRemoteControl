package com.lambda.interfaces

import com.lambda.client.event.SafeClientEvent
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.ServerData

interface GameEventManager {
    fun SafeClientEvent.login(server: ServerData)
    fun SafeClientEvent.logout(reason: String)
    val SafeClientEvent.getScreen : GuiScreen
}