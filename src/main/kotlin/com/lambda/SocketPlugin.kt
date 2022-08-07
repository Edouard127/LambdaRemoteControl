package com.lambda

import com.lambda.client.plugin.api.Plugin
import com.lambda.modules.RemoteControl

internal object SocketPlugin : Plugin() {

    override fun onLoad() {
        modules.add(RemoteControl)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}