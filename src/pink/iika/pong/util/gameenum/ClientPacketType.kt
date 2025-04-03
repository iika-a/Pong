package pink.iika.pong.util.gameenum

enum class ClientPacketType {
    JOIN_LOBBY,
    JOIN_ACCEPTED_ACK,
    EXIT_LOBBY,
    PADDLE_LEFT_START,
    PADDLE_LEFT_END,
    PADDLE_RIGHT_START,
    PADDLE_RIGHT_END,
    START_GAME,
    STOP_GAME_ACK,
    GET_ROOMS
}