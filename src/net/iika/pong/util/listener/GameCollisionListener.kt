package net.iika.pong.util.listener

import net.iika.pong.logic.gameobject.GameObject
import net.iika.pong.logic.gameobject.Paddle
import net.iika.pong.logic.gameobject.PowerUp
import net.iika.pong.util.gameenum.PowerUpType
import net.iika.pong.logic.gameobject.Ball
import net.iika.pong.util.gameenum.CollisionEvent
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class GameCollisionListener: CollisionListener {
    private var direction = 0

    override fun onCollision(event: CollisionEvent, obj1: GameObject, obj2: GameObject, intersect: Double, gameObjectList: CopyOnWriteArrayList<GameObject>) {
        when (event) {
            CollisionEvent.BALL_PADDLE -> {
                obj1.yVelocity *= -1
                obj1.yPosition += intersect
            }
            CollisionEvent.BALL_WALL -> {
                obj1.xVelocity *= -1
                obj1.xPosition += intersect
            }
            CollisionEvent.BALL_OBSTACLE_SIDE -> {
                obj1.xVelocity *= -1
                obj1.xPosition += intersect
            }
            CollisionEvent.BALL_OBSTACLE_TOP_BOTTOM -> {
                obj1.yVelocity *= -1
                obj1.yPosition += intersect
            }
            CollisionEvent.BALL_BALL_SIDE -> {
                obj1.xVelocity *= -1
                obj1.xPosition += intersect
                obj2.xVelocity *= -1
                obj2.xPosition += -intersect
            }
            CollisionEvent.BALL_BALL_TOP_BOTTOM -> {
                obj1.yVelocity *= -1
                obj1.yPosition += intersect
                obj2.yVelocity *= -1
                obj2.yPosition += -intersect
            }
            CollisionEvent.PADDLE_WALL -> {
                obj1.xPosition += intersect
            }
            CollisionEvent.PADDLE_POWERUP -> applyPowerUp(obj1 as Paddle, obj2 as PowerUp, gameObjectList)
        }
    }

    override fun applyPowerUp(paddle: Paddle, powerUp: PowerUp, gameObjectList: CopyOnWriteArrayList<GameObject>) {
        when (powerUp.type) {
            PowerUpType.INCREASE_PADDLE_SIZE -> paddle.move(0.0, 0.0, (15..25).random().toDouble(), 0.0)
            PowerUpType.INCREASE_PADDLE_SPEED -> paddle.paddleSpeed += 100
            PowerUpType.RANDOMIZE_BALL_ANGLE -> {
                val ball = gameObjectList.filterIsInstance<Ball>().random()
                ball.velocityAngle = getRandomAngle(ball.initialDirection)
            }
            PowerUpType.RANDOMIZE_BALL_SPEED -> gameObjectList.filterIsInstance<Ball>().random().ballSpeed = (475..625).random().toDouble()
            PowerUpType.SPAWN_BALL -> {
                val velocityAngle = getRandomAngle(direction)
                val ballSpeed = (550..600).random().toDouble()
                val xVelocity = ballSpeed * cos(velocityAngle)
                val yVelocity = ballSpeed * sin(velocityAngle)
                gameObjectList.add(
                    Ball(
                        xPos = ((1280/3)..(2 * 1280/3)).random().toDouble(),
                        yPos = 720/2.0,
                        velocityAngle = velocityAngle,
                        ballSpeed = ballSpeed,
                        xVel = xVelocity,
                        yVel = yVelocity,
                        isTemporary = true,
                        initialDirection = direction++
                    )
                )
            }
        }
    }

    fun checkIntersect(obj1: GameObject, obj2: GameObject): Boolean {
        return obj1.xPosition < obj2.xPosition + obj2.width &&
                obj1.xPosition + obj1.width > obj2.xPosition &&
                obj1.yPosition < obj2.yPosition + obj2.height &&
                obj1.yPosition + obj1.height > obj2.yPosition
    }

    private fun getRandomAngle(direction: Int): Double {
        return if (direction == 0) Random.nextDouble(PI/9, 7 * PI/18) else Random.nextDouble(PI/9 + PI, 7 * PI/18 + PI)
    }
}