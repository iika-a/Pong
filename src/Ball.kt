data class Ball (
    val xPos: Int = 0,
    val yPos: Int = 0,
    val xVel: Double = 0.0,
    val yVel: Double = 0.0,
    val ballRadius: Int = 10,
    var velocityAngle: Double = 0.0,
    var ballSpeed: Double = 0.0,
    var processed: Boolean = false,
    val isTemporary: Boolean = false
): GameObject(xPos, yPos, xVel, yVel, ballRadius, ballRadius)