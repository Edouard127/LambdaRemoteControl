package com.lambda.utils


class RotationUtils {
    fun getRotation(yaw: String, pitch: String): Rotations.Rotation {
        return Rotations.Rotation(
            getYaw(yaw),
            getPitch(pitch)
        )
    }

    private fun getYaw(rotation: String): Float {
        return Rotations.Yaw.valueOf(rotation).v
    }

    private fun getPitch(rotation: String): Float {
        return Rotations.Pitch.valueOf(rotation).v
    }
}