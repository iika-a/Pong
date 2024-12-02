open class GameObject (var xPosition: Int = 0, var yPosition: Int = 0, var xVelocity: Double = 0.0, var yVelocity: Double = 0.0, var width: Int = 0, var height: Int = 0) {
    fun move(moveX: Int, moveY: Int, resizeX: Int = 0, resizeY: Int = 0) {
        xPosition += moveX
        yPosition += moveY
        width += resizeX
        height += resizeY
    }
}