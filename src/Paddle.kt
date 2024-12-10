data class Paddle (
    val xPos: Double = 0.0,
    var paddleWidth: Double = 100.0,
    val paddleHeight: Double = 5.0,
    var paddleSpeed: Double = 10.0,
    var leftPress: Boolean = false,
    var rightPress: Boolean = false,
    val side: Int = 0,
    var leftKey: Int = 0,
    var rightKey: Int = 0
): GameObject(xPos, if (side == 1) 1080 - paddleHeight else 0.0, 0.0, 0.0, paddleWidth, paddleHeight)