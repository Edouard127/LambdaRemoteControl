package com.lambda.utils

import com.lambda.interfaces.IFragmentedPacket

class FragmentedPacket(val packet: Packet, private val fragments: List<Fragment>): IFragmentedPacket {
    override fun getFragments(): List<Fragment> {
        return fragments
    }

    override fun getFragmentId(): Int {
        return fragments.firstOrNull()?.hash ?: 0
    }

    override fun getFragmentOffset(): Int {
        return fragments.firstOrNull()?.offset ?: 0
    }

    override fun getFragmentSize(): Int {
        return fragments.firstOrNull()?.length ?: 0
    }

    override fun getTotalFragments(): Int {
        return fragments.size
    }

    override fun toString(): String {
        return "FragmentedPacket(packet=$packet, fragment=${fragments.joinToString(", ") { it.toString() } })"
    }

    fun getString(): String {
        return "${packet.length} 1 ${packet.getPacket().byte} ${packet.getFlags().byte} ${packet.args.getString()}"
    }
}