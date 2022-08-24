package com.lambda.utils


class Fragment(var fragment: ByteArray, var offset: Int, var length: Int, var hash: Int, var sum: Int) {

    init {
        if (length > 0) {
            for (i in offset until offset + length) {
                sum += fragment[i]
            }
        }
    }

    // fun errored(): Boolean = this.equals(fragment)

    override fun toString(): String = "Fragment(fragment=$fragment, offset=$offset, length=$length, hash=$hash, sum=$sum)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Fragment

        if (!fragment.contentEquals(other.fragment)) return false
        if (offset != other.offset) return false
        if (length != other.length) return false
        if (hash != other.hash) return false
        if (sum != other.sum) return false

        return true
    }
}