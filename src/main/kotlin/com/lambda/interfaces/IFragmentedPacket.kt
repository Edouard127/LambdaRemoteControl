package com.lambda.interfaces

import com.lambda.classes.packet.Fragment

interface IFragmentedPacket {
    fun getFragments(): List<Fragment>
    fun getFragmentId(): Int
    fun getFragmentOffset(): Int
    fun getFragmentSize(): Int
    fun getTotalFragments(): Int
    override fun toString(): String
}