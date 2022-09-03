package com.lambda.utils

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.EntityPlayer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.UUID

class FriendUtils(private val friends: ArrayList<EntityPlayer> = ArrayList()) {

    fun getUUID(name: String): UUID? {
        val uuid: String
        try {
            val input = BufferedReader(InputStreamReader(URL("https://api.mojang.com/users/profiles/minecraft/$name").openStream()))
            uuid = (JsonParser().parse(input) as JsonObject).get("id").toString().replace("\"", "")
            uuid.replace("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(), "$1-$2-$3-$4-$5")
            input.close()
        } catch (e: Exception) {
            return null
        }
        return UUID.fromString(uuid)
    }
    fun getPlayerProfile(player: String): EntityPlayer? {
        val uuid = getUUID(player) ?: return null
        val profile = GameProfile(uuid, player)
        return friends.getOrNull(friends.indexOfFirst { it.gameProfile == profile })
    }
    fun addFriend(player: EntityPlayer) {
        if (!friends.contains(player)) friends.add(player)
    }
    fun addFriend(player: String) {
        val profile = getPlayerProfile(player) ?: return
        if (!friends.contains(profile)) friends.add(profile)
    }
    fun removeFriend(player: EntityPlayer) {
        if (friends.contains(player)) friends.remove(player)
    }
    fun removeFriend(player: String) {
        val profile = getPlayerProfile(player) ?: return
        if (friends.contains(profile)) friends.remove(profile)
    }
    fun isFriend(player: EntityPlayer): Boolean {
        return friends.contains(player)
    }
}
