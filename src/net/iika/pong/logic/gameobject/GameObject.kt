package net.iika.pong.logic.gameobject

open class GameObject (var xPosition: Double = 0.0, var yPosition: Double = 0.0, var xVelocity: Double = 0.0, var yVelocity: Double = 0.0, var width: Double = 0.0, var height: Double = 0.0) {
    fun move(moveX: Double, moveY: Double, resizeX: Double = 0.0, resizeY: Double = 0.0) {
        xPosition += moveX
        yPosition += moveY
        width += resizeX
        height += resizeY
    }
}