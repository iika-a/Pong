package pink.iika.pong.util.gameenum

enum class ServerPacketType {
    JOIN_ACCEPTED,
    JOIN_DENIED,
    ENABLE_START,
    GAME_TICK,
    COUNTDOWN_TICK,
    START_GAME,
    STOP_GAME,
}