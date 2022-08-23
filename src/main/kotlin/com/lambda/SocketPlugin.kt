package com.lambda

import com.lambda.client.plugin.api.Plugin
import com.lambda.modules.RemoteControl
import com.lambda.utils.Debug

internal object SocketPlugin : Plugin() {

    override fun onLoad() {
        Debug.purple("SocketPlugin loaded")
        modules.add(RemoteControl)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}