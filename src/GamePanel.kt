import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.ArrayList
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.LineBorder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.random.Random

class GamePanel(private val gameObjectList: ArrayList<GameObject>, private val scoreKeeper: ScoreKeeper, private val buttonMouseListener: ButtonMouseListener): JPanel(),
    KeyListener {
    private var player1Gain = 0
    private var player2Gain = 0
    private val gameFont = Font("Segoe UI", 0, 14)
    private val lossLabel = JLabel("You Lose!").apply { font = gameFont }
    private val multiLossLabel = JLabel("Player 1: +$player1Gain      Player 2: +$player2Gain").apply { font = gameFont }
    private val winLabel = JLabel("You Win!").apply { font = gameFont }
    private val winLabel1 = JLabel("<html>Player 1 Won. Continue for 1 point<br>⠀⠀⠀⠀⠀⠀⠀Or exit for 0.5?</html>").apply { font = gameFont.deriveFont(12f) }
    private val winLabel2 = JLabel("<html>Player 2 Won. Continue for 1 point<br>⠀⠀⠀⠀⠀⠀⠀Or exit for 0.5?</html>").apply { font = gameFont.deriveFont(12f) }
    private val scoreLabel1 = JLabel("Player 1 Score: ${scoreKeeper.score1}").apply { font = gameFont }
    private val scoreLabel2 = JLabel("Player 2 Score: ${scoreKeeper.score2}").apply { font = gameFont }
    private val replayButton = JButton("Play Again").apply { setButtonSettings(this) }
    private val exitButton = JButton("Return to Menu").apply { setButtonSettings(this) }
    private val continueButton = JButton("Continue Game").apply { setButtonSettings(this) }
    private var gameManager: GameEventListener? = null
    private var playerNum = 0
    private var powerUpList = ArrayList<PowerUp>()
    private var obstacleList = ArrayList<Obstacle>()
    private var isSplitGame = false
    private var colors = arrayOf(Color(0xE4A8CA), Color(0xCCAA87), Color(0xBB6588), Color(0x8889CC))

    init {
        this.background = Color.WHITE

        lossLabel.horizontalAlignment = JLabel.CENTER
        lossLabel.isVisible = false

        multiLossLabel.horizontalAlignment = JLabel.CENTER
        multiLossLabel.isVisible = false

        winLabel.horizontalAlignment = JLabel.CENTER
        winLabel.isVisible = false

        winLabel1.horizontalAlignment = JLabel.CENTER
        winLabel1.isVisible = false

        winLabel2.horizontalAlignment = JLabel.CENTER
        winLabel2.isVisible = false

        scoreLabel1.horizontalAlignment = JLabel.RIGHT
        scoreLabel1.isVisible = false

        scoreLabel2.horizontalAlignment = JLabel.LEFT
        scoreLabel2.isVisible = false

        replayButton.addActionListener {gameManager?.onGameEvent(GameEvent.REPLAY_GAME)}
        exitButton.addActionListener {
            when (checkForWin()) {
                1 -> {
                    gameManager?.onGameEvent(GameEvent.ADD_SCORE_ONE_HALF)
                    gameManager?.onGameEvent(GameEvent.EXIT_TO_MENU)
                }

                2 -> {
                    gameManager?.onGameEvent(GameEvent.ADD_SCORE_TWO_HALF)
                    gameManager?.onGameEvent(GameEvent.EXIT_TO_MENU)
                }
            }
            gameManager?.onGameEvent(GameEvent.EXIT_TO_MENU)
        }
        continueButton.addActionListener {
            when(checkForWin()) {
                1 -> gameManager?.onGameEvent(GameEvent.CONTINUE_GAME_ONE)
                2 -> gameManager?.onGameEvent(GameEvent.CONTINUE_GAME_TWO)
            } }

        this.add(lossLabel)
        this.add(multiLossLabel)
        this.add(winLabel)
        this.add(winLabel1)
        this.add(winLabel2)
        this.add(scoreLabel1)
        this.add(scoreLabel2)
        this.add(replayButton)
        this.add(exitButton)
        this.add(continueButton)
        this.addKeyListener(this)
    }

    override fun keyPressed(e: KeyEvent?) {
        for (paddle in gameObjectList) {
            when (paddle) {
                is Paddle -> {
                    if (e?.keyCode == paddle.leftKey) paddle.leftPress = true
                    if (e?.keyCode == paddle.rightKey) paddle.rightPress = true
                }
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        for (paddle in gameObjectList) {
            when (paddle) {
                is Paddle -> {
                    if (e?.keyCode == paddle.leftKey) paddle.leftPress = false
                    if (e?.keyCode == paddle.rightKey) paddle.rightPress = false
                }
            }
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        //no implementation needed
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)

        val obstacles = ArrayList(obstacleList)
        val powerUps = ArrayList(powerUpList)
        val gameObjects = ArrayList(gameObjectList)

        for (obstacle in obstacles) {
            g?.color = Color.BLACK
            g?.fillRect(obstacle.xPosition - 2, obstacle.yPosition - 2, obstacle.width + 4, obstacle.height + 4)

            g?.color = colors[2]
            g?.fillRect(obstacle.xPosition, obstacle.yPosition, obstacle.width, obstacle.height)
        }

        for(powerUp in powerUps) {
            g?.color = Color.BLACK
            when (powerUp.side) {
                1 -> g?.fillRect(powerUp.xPosition - 2, this.height - 10 - 2, 54, 12)
                2 -> g?.fillRect(powerUp.xPosition - 2, 0, 54, 12)
            }

            when (powerUp.type) {
                PowerUpType.INCREASE_PADDLE_SIZE -> g?.color = Color.RED
                PowerUpType.INCREASE_PADDLE_SPEED -> g?.color = Color.BLUE
                PowerUpType.RANDOMIZE_BALL_SPEED -> g?.color = Color.GREEN
                PowerUpType.RANDOMIZE_BALL_ANGLE -> g?.color = Color.YELLOW
                PowerUpType.SPAWN_BALL -> g?.color = Color.MAGENTA
            }

            when (powerUp.side) {
                1 -> g?.fillRect(powerUp.xPosition, this.height - 10, 50, 10)
                2 -> g?.fillRect(powerUp.xPosition, 0, 50, 10)
            }
        }

        for (gameObject in gameObjects) {
            when (gameObject) {
                is Ball -> {
                    g?.color = Color.BLACK
                    g?.fillOval(gameObject.xPosition - 2, gameObject.yPosition - 2, 2 * gameObject.ballRadius + 4, 2 * gameObject.ballRadius + 4)
                    g?.color = colors[3]
                    g?.fillOval(gameObject.xPosition, gameObject.yPosition, 2 * gameObject.ballRadius, 2 * gameObject.ballRadius)
                }

                is Paddle -> {
                    g?.color = Color.BLACK
                    when (gameObject.side) {
                        2 -> {
                            g?.fillRect(gameObject.xPosition - 2, 0, gameObject.paddleWidth + 4, gameObject.paddleHeight + 2)
                            g?.color = colors[1]
                            g?.fillRect(gameObject.xPosition, 0, gameObject.paddleWidth, gameObject.paddleHeight)
                        }

                        1 -> {
                            g?.fillRect(gameObject.xPosition - 2, this.height - gameObject.paddleHeight - 2, gameObject.paddleWidth + 4, gameObject.paddleHeight + 4)
                            g?.color = colors[0]
                            g?.fillRect(gameObject.xPosition, this.height - gameObject.paddleHeight, gameObject.paddleWidth, gameObject.paddleHeight)
                        }
                    }
                }
            }
        }

        if (checkForLoss() != 0 || checkForWin() != 0) {
            g?.color = Color.BLACK
            g?.fillRect(this.width/2 - 125 - 2, this.height/2 - 35 - 2, 250 + 4, 185 + 4)
            g?.color = Color(0xFFD1DC)
            g?.fillRect(this.width/2 - 125, this.height/2 - 35, 250, 185)
        }
    }

    fun advanceGame(dt: Double) {
        for (gameObject in gameObjectList) {
            when (gameObject) {
                is Ball -> gameObject.move(round(gameObject.xVelocity * dt).toInt(), round(gameObject.yVelocity * dt).toInt())

                is Paddle -> {
                    if (gameObject.leftPress) gameObject.move(round(-gameObject.paddleSpeed * dt).toInt(), 0)
                    if (gameObject.rightPress) gameObject.move(round(gameObject.paddleSpeed * dt).toInt(), 0)
                }
            }
        }

        doCollisionLogic()

        when (checkForLoss()) {
            1 -> {
                refreshGains()
                gameManager?.onGameEnd()
                multiLossLabel.isVisible = true
                replayButton.isVisible = true
                exitButton.isVisible = true
            }

            2 -> {
                gameManager?.onGameEnd()
                lossLabel.isVisible = true
                replayButton.isVisible = true
                exitButton.isVisible = true
            }
        }

        when (checkForWin()) {
            1 -> {
                gameManager?.onGameEnd()
                winLabel1.isVisible = true
                continueButton.isVisible = true
                exitButton.isVisible = true
            }

            2 -> {
                gameManager?.onGameEnd()
                winLabel2.isVisible = true
                continueButton.isVisible = true
                exitButton.isVisible = true
            }

            3 -> {
                gameManager?.onGameEnd()
                replayButton.isVisible = true
                exitButton.isVisible = true
            }
        }
        repaint()
    }

    private fun doCollisionLogic() {
        for (gameObject in gameObjectList) {
            when (gameObject) {
                is Ball -> {
                    if (gameObject.xPosition <= 0 || gameObject.xPosition + 2 * gameObject.ballRadius >= this.width) {
                        gameObject.xVelocity *= -1
                        if (gameObject.xPosition <= 0) gameObject.xPosition += 2
                        if (gameObject.xPosition + 2 * gameObject.ballRadius >= this.width) gameObject.xPosition -= 2
                    }

                    for (otherObject in gameObjectList) {
                        if (otherObject is Ball) {
                            if (otherObject != gameObject) doObstacleLogic(gameObject, otherObject)
                        }

                        if (otherObject is Paddle) {
                            val paddleTop = if (otherObject.side == 1) this.height - otherObject.paddleHeight else 0
                            val paddleBottom = paddleTop + otherObject.paddleHeight

                            if (gameObject.yPosition + 2 * gameObject.ballRadius >= paddleTop &&
                                gameObject.yPosition <= paddleBottom &&
                                gameObject.xPosition + gameObject.ballRadius >= otherObject.xPosition &&
                                gameObject.xPosition <= otherObject.xPosition + otherObject.paddleWidth
                            ) {
                                gameObject.yVelocity *= -1
                                if (otherObject.side == 1) {
                                    gameObject.yPosition = paddleTop - 2 * gameObject.ballRadius - 1
                                } else {
                                    gameObject.yPosition = paddleBottom + 1
                                }
                            }
                        }
                    }

                    for (obstacle in obstacleList) doObstacleLogic(gameObject, obstacle)
                }

                is Paddle -> {
                    if (gameObject.xPosition < 0) gameObject.xPosition = 0
                    if (gameObject.xPosition + gameObject.paddleWidth > this.width) gameObject.xPosition = this.width - gameObject.paddleWidth
                }
            }
        }

        doPowerUpLogic()
    }

    private fun doObstacleLogic(ball: Ball, collisionObject: GameObject) {
        val ballLeft = ball.xPosition
        val ballRight = ball.xPosition + 2 * ball.ballRadius
        val ballTop = ball.yPosition
        val ballBottom = ball.yPosition + 2 * ball.ballRadius

        val obstacleLeft = collisionObject.xPosition
        val obstacleRight = collisionObject.xPosition + collisionObject.width
        val obstacleTop = collisionObject.yPosition
        val obstacleBottom = collisionObject.yPosition + collisionObject.height

        if (ballRight > obstacleLeft && ballLeft < obstacleRight && ballBottom > obstacleTop && ballTop < obstacleBottom) {
            if (ballLeft < obstacleLeft) {
                ball.xVelocity *= -1
                ball.xPosition = obstacleLeft - 2 * ball.ballRadius
            } else if (ballRight > obstacleRight) {
                ball.xVelocity *= -1
                ball.xPosition = obstacleRight
            } else if (ballTop < obstacleTop) {
                ball.yVelocity *= -1
                ball.yPosition = obstacleTop - 2 * ball.ballRadius
            } else if (ballBottom > obstacleBottom) {
                ball.yVelocity *= -1
                ball.yPosition = obstacleBottom
            }
        }
    }

    private fun doPowerUpLogic() {
        val iterator = powerUpList.iterator()
        while (iterator.hasNext()) {
            val powerUp = iterator.next()

            var powerUpConsumed = false

            for (gameObject in gameObjectList) {
                if (gameObject is Paddle) {
                    val paddleLeft = gameObject.xPosition
                    val paddleRight = gameObject.xPosition + gameObject.paddleWidth
                    val powerUpLeft = powerUp.xPosition
                    val powerUpRight = powerUp.xPosition + 50

                    if (((powerUpLeft in paddleLeft..paddleRight) || (powerUpRight in paddleLeft..paddleRight)) && powerUp.side == gameObject.side) {
                        applyPowerup(gameObject, powerUp.type)
                        powerUpConsumed = true
                        break
                    }
                }
            }

            if (powerUpConsumed) {
                iterator.remove()
            }
        }
    }


    fun initializeBall() {
        var i = 0
        var j = 0

        val iterator = gameObjectList.iterator()
        while (iterator.hasNext()) {
            val gameObject = iterator.next()

            if (gameObject is Ball) {
                gameObject.xPosition = ((this.width/3)..(2 * this.width/3)).random()
                gameObject.yPosition = this.height / 2

                if (isSplitGame && j % 2 == 0) gameObject.xPosition = this.width / 2 - this.width / 4
                if (isSplitGame && j++ % 2 == 1) gameObject.xPosition = this.width / 2 + this.width / 4
                if (gameObject.isTemporary) iterator.remove()

                gameObject.processed = false
                gameObject.ballSpeed = (550..625).random().toDouble()
                if (i++ % 2 == 0) gameObject.velocityAngle = getRandomAngle()
                else gameObject.velocityAngle = getRandomAngle() + PI
                gameObject.xVelocity = gameObject.ballSpeed * cos(gameObject.velocityAngle)
                gameObject.yVelocity = gameObject.ballSpeed * sin(gameObject.velocityAngle)
            }
        }
    }

    fun initializePaddles(paddleNum: Int = 0) {
        var i = 0

        for (gameObject in gameObjectList) {
            if (playerNum == 2 && gameObject is Paddle) {
                if (paddleNum == 0 || gameObject.side == paddleNum) {
                    gameObject.paddleWidth = 100
                    gameObject.paddleSpeed = 625
                    gameObject.xPosition = this.width / 2 - gameObject.paddleWidth / 2
                }
                if (gameObject.side == 1 && !isSplitGame) gameObject.xPosition = this.width / 2 - gameObject.paddleWidth / 2
                if (isSplitGame && i % 2 == 0) gameObject.xPosition = this.width / 2 - this.width / 4 - gameObject.paddleWidth / 2
                if (isSplitGame && i++ % 2 == 1) gameObject.xPosition = this.width / 2 + this.width / 4 - gameObject.paddleWidth / 2
            } else if (playerNum == 1 && gameObject is Paddle) {
                if (gameObject.side == 2) {
                    gameObject.xPosition = this.width / 2 - gameObject.paddleWidth / 2
                    gameObject.paddleWidth = 2000
                    gameObject.paddleSpeed = 0
                } else if (gameObject.side == 1) {
                    gameObject.xPosition = this.width / 2 - gameObject.paddleWidth / 2

                    gameObject.paddleWidth = 100
                    gameObject.paddleSpeed = 625
                    if (isSplitGame && i % 2 == 0) gameObject.xPosition = this.width / 2 + this.width / 4 - gameObject.paddleWidth / 2
                    if (isSplitGame && i++ % 2 == 1) gameObject.xPosition = this.width / 2 - this.width / 4 - gameObject.paddleWidth / 2
                }
            }
        }
    }

    fun initializeComponents() {
        lossLabel.setBounds(this.width/2 - 100, this.height/2 - 30, 200, 50)
        multiLossLabel.setBounds(this.width/2 - 100, this.height/2 - 30, 200, 50)
        replayButton.setBounds(this.width/2 - 100, this.height/2 + 22, 200, 50)
        continueButton.setBounds(this.width/2 - 100, this.height/2 + 22, 200, 50)
        exitButton.setBounds(this.width/2 - 100, this.height/2 + 75, 200, 50)
        winLabel.setBounds(this.width/2 - 100, this.height/2 - 30, 200, 50)
        winLabel1.setBounds(this.width/2 - 100, this.height/2 - 55, 200, 100)
        winLabel2.setBounds(this.width/2 - 100, this.height/2 - 55, 200, 100)
        scoreLabel1.setBounds(this.width - 200, this.height - 50, 200, 50)
        scoreLabel2.setBounds(0, 0, 200, 50)
        player1Gain = 0
        player2Gain = 0

        winLabel.isVisible = false
        winLabel1.isVisible = false
        winLabel2.isVisible = false
        lossLabel.isVisible = false
        multiLossLabel.isVisible = false
        replayButton.isVisible = false
        exitButton.isVisible = false
        continueButton.isVisible = false
        when (playerNum) {
            1 -> {
                scoreLabel1.isVisible = false
                scoreLabel2.isVisible = false
            }

            2 -> {
                scoreLabel1.isVisible = true
                scoreLabel2.isVisible = true
            }
        }
    }

    private fun getRandomAngle(): Double {
        return Random.nextDouble(PI /9, 7 * PI /18)
    }

    fun speedUpBall(increment: Double = 10.0) {
        for (gameObject in gameObjectList) {
            if (gameObject is Ball) {
                gameObject.ballSpeed += increment

                if (gameObject.velocityAngle < PI) {
                    gameObject.xVelocity = gameObject.ballSpeed * cos(gameObject.velocityAngle) * (if (gameObject.xVelocity < 0) -1 else 1)
                    gameObject.yVelocity = gameObject.ballSpeed * sin(gameObject.velocityAngle) * (if (gameObject.yVelocity < 0) -1 else 1)
                } else {
                    gameObject.xVelocity = gameObject.ballSpeed * cos(gameObject.velocityAngle + PI) * (if (gameObject.xVelocity < 0) -1 else 1)
                    gameObject.yVelocity = gameObject.ballSpeed * sin(gameObject.velocityAngle + PI) * (if (gameObject.yVelocity < 0) -1 else 1)
                }
            }
        }
    }

    private fun checkForLoss(): Int {
        var allBallsOffScreen = true

        for (gameObject in gameObjectList) {
            if (gameObject is Ball) {
                when {
                    gameObject.yPosition + 2 * gameObject.ballRadius < 0 -> {
                        if (!gameObject.processed) {
                            gameManager?.onGameEvent(GameEvent.ADD_SCORE_ONE)
                            refreshScore()
                            player1Gain++
                        }
                        gameObject.processed = true

                    }

                    gameObject.yPosition > this.height -> {
                        if (!gameObject.processed && playerNum == 2) {
                            gameManager?.onGameEvent(GameEvent.ADD_SCORE_TWO)
                            refreshScore()
                            player2Gain++
                        }
                        gameObject.processed = true
                    }
                    else -> allBallsOffScreen = false
                }
            }
        }

        return if (allBallsOffScreen) if (playerNum == 1) 2 else 1 else 0
    }


    private fun checkForWin(): Int {
        if (playerNum == 1) {
            for (gameObject in gameObjectList) {
                if (gameObject is Paddle && gameObject.side == 1 && gameObject.paddleWidth > this.width) {
                    return 3
                }
            }
        } else if (playerNum == 2) {
            var player1Win = false
            var player2Win = false

            for (gameObject in gameObjectList) {
                if (gameObject is Paddle) {
                    when (gameObject.side) {
                        1 -> if (gameObject.paddleWidth > this.width / 2) player2Win = true
                        2 -> if (gameObject.paddleWidth > this.width / 2) player1Win = true
                    }
                }
            }

            if (player1Win) return 2
            if (player2Win) return 1
        }

        return 0
    }

    private fun applyPowerup(paddle: Paddle, type: PowerUpType) {
        when (type) {
            PowerUpType.INCREASE_PADDLE_SIZE -> paddle.paddleWidth += (15..25).random()
            PowerUpType.INCREASE_PADDLE_SPEED -> paddle.paddleSpeed += 100
            PowerUpType.RANDOMIZE_BALL_ANGLE -> gameObjectList.filterIsInstance<Ball>().random().velocityAngle = getRandomAngle()
            PowerUpType.RANDOMIZE_BALL_SPEED -> gameObjectList.filterIsInstance<Ball>().random().ballSpeed = (475..575).random().toDouble()
            PowerUpType.SPAWN_BALL -> {
            val velocityAngle = getRandomAngle()
            val ballSpeed = (550..625).random().toDouble()
            val xVelocity = ballSpeed * cos(velocityAngle)
            val yVelocity = ballSpeed * sin(velocityAngle)
            gameObjectList.add(Ball(
                xPos = ((this.width/3)..(2 * this.width/3)).random(),
                yPos = this.height/2,
                velocityAngle = velocityAngle,
                ballSpeed = ballSpeed,
                xVel = xVelocity,
                yVel = yVelocity,
                isTemporary = true))
            }
        }
    }

    private fun refreshScore() {
        scoreLabel1.text = "Player 1 Score: ${scoreKeeper.score1}"
        scoreLabel2.text = "Player 2 Score: ${scoreKeeper.score2}"
    }

    private fun refreshGains() {
        multiLossLabel.text = "Player 1: +$player1Gain      Player 2: +$player2Gain"
    }

    private fun setButtonSettings(button: JButton) {
        val menuFont = Font("Segoe UI", 0, 18)
        val buttonColor = Color(0xD1F6FF)
        val buttonBorder = LineBorder(Color.WHITE, 3)

        button.font = menuFont
        button.background = buttonColor
        button.border = buttonBorder
        button.addMouseListener(buttonMouseListener)
    }

    fun setPlayers(playerNum: Int) { this.playerNum = playerNum }
    fun setGameEventListener(listener: GameEventListener) { this.gameManager = listener }
    fun setPowerUpList(list: ArrayList<PowerUp>) { this.powerUpList = list }
    fun setObstacleList(list: ArrayList<Obstacle>) { this.obstacleList = list }
    fun setSplitGame(sg: Boolean) { this.isSplitGame = sg }
    fun setColors(colors: Array<Color>) { this.colors = colors }
}