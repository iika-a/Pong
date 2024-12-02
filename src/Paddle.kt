data class Paddle (
    val xPos: Int = 0,
    var paddleWidth: Int = 100,
    val paddleHeight: Int = 5,
    var paddleSpeed: Int = 10,
    var leftPress: Boolean = false,
    var rightPress: Boolean = false,
    val side: Int = 0,
    var leftKey: Int = 0,
    var rightKey: Int = 0
): GameObject(xPos, if (side == 1) 720 - paddleHeight else 0, 0.0, 0.0, paddleWidth, paddleHeight)