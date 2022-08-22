package com.lambda.utils

import com.lambda.modules.RemoteControl
import scala.Console

object Debug {
    fun log(vararg args: String): String {
        val a = args.joinToString(" ")
        if (RemoteControl.debug) a.also { println(it) }
        return a
    }
    fun warn(vararg args: String): String {
        val a = args.joinToString(" ")
        if (RemoteControl.debug) a.also { println(Console.YELLOW() + it + Console.RESET()) }
        return a
    }
    fun error(vararg args: String): String {
        val a = args.joinToString(" ")
        if (RemoteControl.debug) a.also { println(Console.RED() + it + Console.RESET()) }
        return a
    }
    fun blue(vararg args: String): String {
        val a = args.joinToString(" ")
        if (RemoteControl.debug) a.also { println(Console.BLUE() + it + Console.RESET()) }
        return a
    }
    fun purple(vararg args: String): String {
        val a = args.joinToString(" ")
        if (RemoteControl.debug) a.also { println(Console.MAGENTA() + it + Console.RESET()) }
        return a
    }
}