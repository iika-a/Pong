package net.iika.pong.util.gameenum

enum class ServerPacketType {
    ADD_POWER_UP,
    REMOVE_POWER_UP,
    COUNTDOWN_TICK,
    START_GAME,
    STOP_GAME,
    REFRESH_SCORE,
    REFRESH_GAINS,
    GAME_TICK
}