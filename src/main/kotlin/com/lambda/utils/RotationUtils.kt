package com.lambda.utils

class Rotation(val x: Float, val y: Float)


class RotationUtils {
    fun getRotation(yaw: String, pitch: String): Rotation {
        return Rotation(yaw.toFloat(), pitch.toFloat())
    }

    fun getYaw(rotation: Float): String {
        return when (rotation) {
            -180.0f -> "NORTH"
            -90.0f -> "EAST"
            0.0f -> "SOUTH"
            90.0f -> "WEST"
            else -> "NORTH"
        }
    }

    fun getYaw(rotation: String): Float {
        return when (rotation) {
            "NORTH" -> -180.0f
            "EAST" -> -90.0f
            "SOUTH" -> 0.0f
            "WEST" -> 90.0f
            else -> -180.0f
        }
    }

    fun getPitch(rotation: Float): String {
        return when (rotation) {
            -180.0f -> "DOWN"
            -90.0f -> "UP"
            0.0f -> "DOWN"
            90.0f -> "UP"
            else -> "DOWN"
        }
    }

    fun getPitch(rotation: String): Float {
        return when (rotation) {
            "DOWN" -> 90.0f
            "UP" -> -90.0f
            "HORIZONTAL" -> 0.0f
            else -> 0.0f
        }
    }
}