package com.lambda.utils

import com.lambda.interfaces.IHighwayToolsHandler
import java.util.*
import kotlin.reflect.KTypeParameter

class HighwayToolsHandler : IHighwayToolsHandler {
    override fun parse(data: ByteArray) {
        val byte = data[0]

    }

    override fun createJob(args: Job) {
        
    }
}

enum class HighwayArguments(val byte: Int, val n: Int, val string: String) {
    TOGGLE(byte = 0x00, n = 0, string = "Enable"),
    MODE(byte = 0x01, n = 1, string = "Mode"),
    WIDTH(byte = 0x02, n = 1, string = "Width"),
    HEIGHT(byte = 0x03, n = 1, string = "Height"),
    BACK_FILL(byte = 0x04, n = 1, string = "Backfill"),
    CLEAR_SPACE(byte = 0x05, n = 1, string = "ClearSpace"),
    CLEAN_FLOOR(byte = 0x06, n = 1, string = "CleanFloor"),
    CLEAN_RIGHT_WALL(byte = 0x07, n = 1, string = "CleanRightWall"),
    CLEAN_LEFT_WALL(byte = 0x08, n = 1, string = "CleanLeftWall"),
    CLEAN_ROOF(byte = 0x09, n = 1, string = "CleanRoof"),
    CLEAN_CORNER(byte = 0x0A, n = 1, string = "CleanCorner"),
    CORNER_BLOCK(byte = 0x0B, n = 1, string = "CornerBlock"),
    RAILING(byte = 0x0C, n = 1, string = "Railing"),
    RAILING_HEIGHT(byte = 0x0D, n = 1, string = "RailingHeight"),
    MATERIAL(byte = 0x0E, n = 1, string = "Material"),
    FILLER_MATERIAL(byte = 0x0F, n = 1, string = "FillerMat"),
    FOOD_ITEM(byte = 0x10, n = 1, string = "FoodItem"),
    IGNORE_LIST(byte = 0x10, n = 2, string = "IgnoreList"),
    MAX_REACH(byte = 0x11, n = 1, string = "MaxReach"),
    SHUFFLE_TASKS(byte = 0x12, n = 1, string = "ShufleTasks"),
    RUBBERBAND_TIMEOUT(byte = 0x13, n = 1, string = "RubberbandTimeout"),
    TASK_TIMEOUT(byte = 0x14, n = 1, string = "TaskTimeout"),
    PACKET_MOVE_SPEED(byte = 0x15, n = 1, string = "PacketMoveSpeed"),
    BREAK_DELAY(byte = 0x16, n = 1, string = "BreakDelay"),
    MINING_SPEED_FACTOR(byte = 0x17, n = 1, string = "MiningSpeedFactor"),
    INTERACTION_LIMIT(byte = 0x18, n = 1, string = "InteractionLimit"),
    MULTI_BREAK(byte = 0x19, n = 1, string = "MultiBreak"),
    PACKET_FLOOD(byte = 0x1A, n = 1, string = "PacketFlood"),
    ENDER_CHEST_INSTANT_MINE(byte = 0x1B, n = 1, string = "EncherChestInstantMine"),
    PLACE_DELAY(byte = 0x1C, n = 1, string = "PlaceDelay"),
    DYNAMIC_PLACE_DELAY(byte = 0x1D, n = 1, string = "DynamicPlaceDelay"),
    ILLEGAL_PLACEMENTS(byte = 0x1E, n = 1, string = "IllegalPlacements"),
    SCAFFOLD(byte = 0x1F, n = 1, string = "Scaffold"),
    PLACE_DEEP_SEARCH(byte = 0x20, n = 1, string = "PlaceDeepSearch"),
    MANAGE_STORAGE(byte = 0x21, n = 1, string = "ManageStorage"),
    SEARCH_ENDER_CHEST(byte = 0x22, n = 1, string = "SearchEnderChest"),
    LEAVE_EMPTY_SHULKERS(byte = 0x23, n = 1, string = "LeaveEmptyShulkers"),
    GRIND_OBSIDIAN(byte = 0x24, n = 1, string = "GrindObsidian"),
    FAST_FILL(byte = 0x25, n = 1, string = "FastFill"),
    FREE_SLOTS(byte = 0x26, n = 1, string = "FreeSlots"),
    PREFER_ENDER_CHESTS(byte = 0x27, n = 1, string = "PreferEnderChests"),
    MANAGE_FOOD(byte = 0x28, n = 1, string = "ManageFood"),
    SAVE_MATERIAL(byte = 0x29, n = 1, string = "SaveMaterial"),
    SAVE_TOOLS(byte = 0x2A, n = 1, string = "SaveTools"),
    SAVE_ENDER_CHESTS(byte = 0x2B, n = 1, string = "SaveEnderChests"),
    SAVE_FOOD(byte = 0x2C, n = 1, string = "SaveFood"),
    MINIMUM_CONTAINER_DISTANCE(byte = 0x2D, n = 1, string = "MinContainerDistance"),
    DISABLE_MODE(byte = 0x2E, n = 1, string = "DisableMode"),
    PROXY(byte = 0x2F, n = 1, string = "Proxy"),
    PROXY_COMMAND(byte = 0x30, n = 1, "ProxyCommand"),
    ANONYMIZE(byte = 0x31, n = 1, string = "Anonymize"),
    FAKE_SOUNDS(byte = 0x32, n = 1, string = "FakeSounds"),
    SHOW_INFO(byte = 0x33, n = 1, string = "ShowInfo"),
    DEBUG_LEVEL(byte = 0x34, n = 1, string = "DebugLevel"),
    BARITONE_GOAL(byte = 0x35, n = 1, "BaritoneGoal"),
    CURRENT_POS(byte = 0x36, n = 1, string = "CurrentPos");
}
enum class HighwayMode(val byte: Int) {
    HIGHWAY(byte = 0x00),
    FLAT(byte = 0x01),
    TUNNEL(byte = 0x02)
}

enum class DisableMode(val byte: Int) {
    NONE(byte = 0x00),
    ANTI_AFK(byte = 0x01),
    LOGOUT(byte = 0x02)
}

