import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sign
import kotlin.random.Random

class GameCollisionListener(private val width: Int, private val height: Int): CollisionListener {
    override fun onCollision(event: CollisionEvent, obj1: GameObject, obj2: GameObject, gameObjectList: ArrayList<GameObject>) {
        when (event) {
            CollisionEvent.BALL_PADDLE -> {
                obj1.yVelocity *= -1
                obj1.yPosition += 2 * obj1.yVelocity.sign
            }
            CollisionEvent.BALL_WALL -> {
                obj1.xVelocity *= -1
                if(obj1.xPosition < 0 || obj1.xPosition + 2 * obj1.width > width)obj1.xPosition += 2 * obj1.xVelocity.sign
            }
            CollisionEvent.BALL_OBSTACLE_SIDE -> {
                TODO("finish collision listener")
                obj1.xVelocity *= -1
                obj1.xPosition += 2 * obj1.xVelocity.sign
            }
            CollisionEvent.BALL_OBSTACLE_TOP_BOTTOM -> {
                obj1.yVelocity *= -1
                obj1.yPosition += 2 * obj1.yVelocity.sign
            }
            CollisionEvent.BALL_BALL_SIDE -> {
                obj1.xVelocity *= -1
                obj1.xPosition += 2 * obj1.xVelocity.sign
                obj2.xVelocity *= -1
                obj2.xPosition += 2 * obj2.xVelocity.sign
            }
            CollisionEvent.BALL_BALL_TOP_BOTTOM -> {
                obj1.yVelocity *= -1
                obj1.yPosition += 2 * obj1.yVelocity.sign
                obj2.yVelocity *= -1
                obj2.yPosition += 2 * obj2.yVelocity.sign
            }
            CollisionEvent.PADDLE_POWERUP -> applyPowerUp(obj1 as Paddle, obj2 as PowerUp, gameObjectList)
        }
    }

    override fun applyPowerUp(paddle: Paddle, powerUp: PowerUp, gameObjectList: ArrayList<GameObject>) {
        when (powerUp.type) {
            PowerUpType.INCREASE_PADDLE_SIZE -> paddle.paddleWidth += (15..25).random()
            PowerUpType.INCREASE_PADDLE_SPEED -> paddle.paddleSpeed += 100
            PowerUpType.RANDOMIZE_BALL_ANGLE -> gameObjectList.filterIsInstance<Ball>().random().velocityAngle = getRandomAngle()
            PowerUpType.RANDOMIZE_BALL_SPEED -> gameObjectList.filterIsInstance<Ball>().random().ballSpeed = (475..625).random().toDouble()
            PowerUpType.SPAWN_BALL -> {
                val velocityAngle = getRandomAngle() + if((0..1).random() == 1) PI else 0.0
                val ballSpeed = (550..600).random().toDouble()
                val xVelocity = ballSpeed * cos(velocityAngle)
                val yVelocity = ballSpeed * sin(velocityAngle)
                gameObjectList.add(Ball(
                    xPos = ((1280/3)..(2 * 1280/3)).random().toDouble(),
                    yPos = 720/2.0,
                    velocityAngle = velocityAngle,
                    ballSpeed = ballSpeed,
                    xVel = xVelocity,
                    yVel = yVelocity,
                    isTemporary = true))
            }
        }
    }

    private fun getRandomAngle(): Double {
        return Random.nextDouble(PI /9, 7 * PI /18)
    }
}