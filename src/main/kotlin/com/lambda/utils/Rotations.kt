package com.lambda.utils

class Rotations {
    class Rotation(val x: Float, val y: Float) {
        override fun toString(): String {
            return "Rotation(x=$x, y=$y)"
        }
    }
    enum class Yaw(val v: Float) {
        NORTH(v = -180.0f),
        EAST(v = -90.0f),
        SOUTH(v = 0.0f),
        WEST(v = 90.0f)
    }
    enum class Pitch(val v: Float) {
        UP(v = -90.0f),
        DOWN(v = 90.0f),
        MIDDLE(v = 0.0f)
    }
}