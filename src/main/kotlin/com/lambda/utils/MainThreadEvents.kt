package com.lambda.utils

import com.lambda.client.event.Event

class MainThreadEvents(val instance: Any?): Event {
    fun <T> cloneInstance(): T? {
        if (instance == null) return null
        if (instance !is Cloneable) return null
        instance::class.java.declaredFields.forEach { field ->
            field.isAccessible = true
            field.set(instance, field.get(instance))
        }
        // Return the new instance
        return instance as? T?
    }
}