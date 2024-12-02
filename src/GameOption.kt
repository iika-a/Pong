enum class GameOption(private val description: String) {
    NORMAL_MODE("Normal Mode"),
    CHAOS_MODE("Chaos Mode"),
    BORING_MODE("Boring Mode"),
    DOUBLE_BALL("Double Balls"),
    DOUBLE_PADDLE("Double Paddles"),
    SPLIT_GAME("Split Game"),
    SINGLEPLAYER("Singleplayer"),
    MULTIPLAYER("Multiplayer"),
    NO_OBSTACLES("No Obstacles"),
    ONE_OBSTACLE("One Obstacle"),
    TWO_OBSTACLES("Two Obstacles"),
    THREE_OBSTACLES("Three Obstacles"),
    FOUR_OBSTACLES("Four Obstacles"),
    ONE_BIG_BLOCK("One Big Block");

    override fun toString(): String {
        return description
    }
}