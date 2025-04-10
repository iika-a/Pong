package pink.iika.pong.logic.gameobject

data class Ball (
    val xPos: Double = 0.0,
    val yPos: Double = 0.0,
    val xVel: Double = 0.0,
    val yVel: Double = 0.0,
    val ballRadius: Double = 10.0,
    var velocityAngle: Double = 0.0,
    var ballSpeed: Double = 0.0,
    var processed: Boolean = false,
    val isTemporary: Boolean = false,
    val initialDirection: Int = 0,
    var isImmune: Boolean = false
): GameObject(xPos, yPos, xVel, yVel, ballRadius * 2, ballRadius * 2)