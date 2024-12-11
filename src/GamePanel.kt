import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.geom.Ellipse2D
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.LineBorder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.min
import kotlin.random.Random

class GamePanel(private val gameObjectList: CopyOnWriteArrayList<GameObject>, private val scoreKeeper: ScoreKeeper, private val buttonMouseListener: ButtonMouseListener): JPanel(), KeyListener {
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
    private var gameManager: GameListener? = null
    private var collisionListener: GameCollisionListener = GameCollisionListener()
    private var playerNum = 0
    private var powerUpList = CopyOnWriteArrayList<PowerUp>()
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
        for (paddle in gameObjectList.filterIsInstance<Paddle>()) {
            if (e?.keyCode == paddle.leftKey) paddle.leftPress = true
            if (e?.keyCode == paddle.rightKey) paddle.rightPress = true
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        for (paddle in gameObjectList.filterIsInstance<Paddle>()) {
            if (e?.keyCode == paddle.leftKey) paddle.leftPress = false
            if (e?.keyCode == paddle.rightKey) paddle.rightPress = false
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        //no implementation needed
    }

    override fun paintComponent(g: Graphics?) {
        val g2d = g as Graphics2D
        super.paintComponent(g2d)

        for(powerUp in powerUpList) {
            g2d.color = Color.BLACK
            when (powerUp.side) {
                1 -> g2d.fill(Rectangle2D.Double(powerUp.xPosition - 2, this.height - 10 - 2.0, 54.0, 12.0))
                2 -> g2d.fill(Rectangle2D.Double(powerUp.xPosition - 2, 0.0, 54.0, 12.0))
            }

            when (powerUp.type) {
                PowerUpType.INCREASE_PADDLE_SIZE -> g2d.color = Color.RED
                PowerUpType.INCREASE_PADDLE_SPEED -> g2d.color = Color.BLUE
                PowerUpType.RANDOMIZE_BALL_SPEED -> g2d.color = Color.GREEN
                PowerUpType.RANDOMIZE_BALL_ANGLE -> g2d.color = Color.YELLOW
                PowerUpType.SPAWN_BALL -> g2d.color = Color.MAGENTA
            }

            when (powerUp.side) {
                1 -> g2d.fill(Rectangle2D.Double(powerUp.xPosition, this.height - 10.0, 50.0, 10.0))
                2 -> g2d.fill(Rectangle2D.Double(powerUp.xPosition, 0.0, 50.0, 10.0))
            }
        }

        for (obstacle in gameObjectList.filterIsInstance<Obstacle>()) {
            g2d.color = Color.BLACK
            g2d.fill(Rectangle2D.Double(obstacle.xPosition - 2, obstacle.yPosition - 2, obstacle.width + 4, obstacle.height + 4))

            g2d.color = colors[2]
            g2d.fill(Rectangle2D.Double(obstacle.xPosition, obstacle.yPosition, obstacle.width, obstacle.height))
        }

        for (gameObject in gameObjectList) {
            when (gameObject) {
                is Ball -> {
                    g2d.color = Color.BLACK
                    g2d.fill(Ellipse2D.Double(gameObject.xPosition - 2, gameObject.yPosition - 2, gameObject.width + 4, gameObject.height + 4))
                    g2d.color = colors[3]
                    g2d.fill(Ellipse2D.Double(gameObject.xPosition, gameObject.yPosition, gameObject.width, gameObject.height))
                }

                is Paddle -> {
                    g2d.color = Color.BLACK
                    when (gameObject.side) {
                        2 -> {
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition - 2, 0.0, gameObject.width + 4, gameObject.paddleHeight + 2))
                            g2d.color = colors[1]
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition, 0.0, gameObject.width, gameObject.paddleHeight))
                        }

                        1 -> {
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition - 2, this.height - gameObject.paddleHeight - 2, gameObject.width + 4, gameObject.paddleHeight + 4))
                            g2d.color = colors[0]
                            g2d.fill(Rectangle2D.Double(gameObject.xPosition, this.height - gameObject.paddleHeight, gameObject.width, gameObject.paddleHeight))
                        }
                    }
                }
            }
        }

        if (checkForLoss() != 0 || checkForWin() != 0) {
            g2d.color = Color.BLACK
            g2d.fill(Rectangle2D.Double(this.width/2 - 200 - 2.0, this.height/2 - 70 - 2.0, 504.0, 209.0))
            g2d.color = Color(0xFFD1DC)
            g2d.fill(Rectangle2D.Double(this.width/2 - 200.0, this.height/2 - 70.0, 500.0, 205.0))
        }
    }

    fun advanceGame(dt: Double) {
        for (gameObject in gameObjectList) {
            when (gameObject) {
                is Ball -> {
                    gameObject.move(gameObject.xVelocity * dt, gameObject.yVelocity * dt)
                    speedUpBall(5 * dt)
                }

                is Paddle -> {
                    if (gameObject.leftPress) gameObject.move(-gameObject.paddleSpeed * dt, 0.0)
                    if (gameObject.rightPress) gameObject.move(gameObject.paddleSpeed * dt, 0.0)
                }
            }
        }

        val ballInstances = gameObjectList.filterIsInstance<Ball>()
        if (ballInstances.size > 1) {
            println(ballInstances[2])
            println("----------")
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
                    if (gameObject.xPosition <= 0 || gameObject.xPosition + gameObject.width >= this.width) {
                        val wallIntersect = if (gameObject.xPosition <= 0) 0 - gameObject.xPosition
                        else gameObject.xPosition + gameObject.width - this.width

                        collisionListener.onCollision(event = CollisionEvent.BALL_WALL, obj1 = gameObject, intersect =  wallIntersect)
                    }

                    for (otherObject in gameObjectList) {
                        if (otherObject is Paddle) {
                            if (!(gameObject.xPosition <= otherObject.xPosition + otherObject.width && gameObject.xPosition + gameObject.width >= otherObject.xPosition)) continue
                            when (otherObject.side) {
                                1 -> {
                                    if (gameObject.yPosition + gameObject.height >= otherObject.yPosition) {
                                        val intersect = gameObject.yPosition + gameObject.height - otherObject.yPosition
                                        collisionListener.onCollision(CollisionEvent.BALL_PADDLE, gameObject, otherObject, intersect)
                                    }
                                }
                                2 -> {
                                    if (gameObject.yPosition <= otherObject.yPosition + otherObject.height) {
                                        val intersect = otherObject.yPosition + otherObject.height - gameObject.yPosition
                                        collisionListener.onCollision(CollisionEvent.BALL_PADDLE, gameObject, otherObject, intersect)
                                    }
                                }
                            }
                        }

                        if (gameObject != otherObject && collisionListener.checkIntersect(gameObject, otherObject) && otherObject !is Paddle) {
                            val xIntersect = min(gameObject.xPosition + gameObject.width - otherObject.xPosition, otherObject.xPosition + otherObject.width - gameObject.xPosition)
                            val yIntersect = min(gameObject.yPosition + gameObject.height - otherObject.yPosition, otherObject.yPosition + otherObject.height - gameObject.yPosition)

                            when (otherObject) {
                                is Ball -> {
                                    if (xIntersect < yIntersect) collisionListener.onCollision(CollisionEvent.BALL_BALL_SIDE, gameObject, otherObject, xIntersect)
                                    else collisionListener.onCollision(CollisionEvent.BALL_BALL_TOP_BOTTOM, gameObject, otherObject, yIntersect)
                                }
                                is Obstacle -> {
                                    if (xIntersect < yIntersect) collisionListener.onCollision(CollisionEvent.BALL_OBSTACLE_SIDE, gameObject, otherObject, xIntersect)
                                    else collisionListener.onCollision(CollisionEvent.BALL_OBSTACLE_TOP_BOTTOM, gameObject, otherObject, yIntersect)
                                }
                            }
                        }
                    }
                }

                is Paddle -> {
                    if (gameObject.xPosition <= 0) gameObject.xPosition = 0.0
                    if (gameObject.xPosition + gameObject.width >= this.width) gameObject.xPosition = this.width - gameObject.width

                    for (powerUp in powerUpList){
                        if (((gameObject.xPosition in powerUp.xPosition..powerUp.xPosition + powerUp.width) || (gameObject.xPosition + gameObject.width in powerUp.xPosition..powerUp.xPosition + powerUp.width)) && gameObject.side == powerUp.side) {
                            collisionListener.onCollision(CollisionEvent.PADDLE_POWERUP, gameObject, powerUp, 0.0, gameObjectList)
                            powerUpList.remove(powerUp)
                        }
                    }
                }
            }
        }
    }

    fun initializeBall() {
        var i = 0

        for (gameObject in gameObjectList) {
            if (gameObject is Ball) {
                gameObject.xPosition = ((this.width/4)..(2 * this.width/3)).random().toDouble()
                gameObject.yPosition = this.height / 2.0

                if (isSplitGame && i % 2 == 0) gameObject.xPosition = this.width / 2 - this.width / 4.0
                if (isSplitGame && i % 2 == 1) gameObject.xPosition = this.width / 2 + this.width / 4.0
                if (gameObject.isTemporary) gameObjectList.remove(gameObject)

                gameObject.processed = false
                gameObject.ballSpeed = (650..725).random().toDouble()
                gameObject.velocityAngle = getRandomAngle()
                gameObject.xVelocity = gameObject.ballSpeed * cos(gameObject.velocityAngle)
                gameObject.yVelocity = gameObject.ballSpeed * sin(gameObject.velocityAngle) * (if (i++ % 2 == 1) -1 else 1)
            }
        }
    }

    fun initializePaddles(paddleNum: Int = 0) {
        var i = 0

        for (paddle in gameObjectList.filterIsInstance<Paddle>()) {
            if (playerNum == 2) {
                if (paddleNum == 0 || paddle.side == paddleNum) {
                    paddle.width = 150.0
                    paddle.paddleSpeed = 800.0
                    paddle.xPosition = this.width / 2 - paddle.width / 2
                }
                if (paddle.side == 1 && !isSplitGame) paddle.xPosition = this.width / 2 - paddle.width / 2
                if (isSplitGame && i % 2 == 0) paddle.xPosition = this.width / 2 - this.width / 4 - paddle.width / 2
                if (isSplitGame && i++ % 2 == 1) paddle.xPosition = this.width / 2 + this.width / 4 - paddle.width / 2
            } else if (playerNum == 1) {
                if (paddle.side == 2) {
                    paddle.xPosition = this.width / 2 - paddle.width / 2
                    paddle.width = 2000.0
                    paddle.paddleSpeed = 0.0
                } else if (paddle.side == 1) {
                    paddle.xPosition = this.width / 2 - paddle.width / 2

                    paddle.width = 150.0
                    paddle.paddleSpeed = 800.0
                    if (isSplitGame && i % 2 == 0) paddle.xPosition = this.width / 2 + this.width / 4 - paddle.width / 2
                    if (isSplitGame && i++ % 2 == 1) paddle.xPosition = this.width / 2 - this.width / 4 - paddle.width / 2
                }
            }

            if (paddle.side == 1) paddle.yPosition = this.height - paddle.paddleHeight
            else paddle.yPosition = 0.0
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

    private fun speedUpBall(increment: Double = 0.0) {
        for (ball in gameObjectList.filterIsInstance<Ball>()) {
            ball.ballSpeed += increment
            ball.xVelocity = ball.ballSpeed * cos(ball.velocityAngle) * (if (ball.xVelocity < 0) -1 else 1)
            ball.yVelocity = ball.ballSpeed * sin(ball.velocityAngle) * (if (ball.yVelocity < 0) -1 else 1)
        }
    }

    private fun checkForLoss(): Int {
        var allBallsOffScreen = true

        for (gameObject in gameObjectList) {
            if (gameObject is Ball) {
                when {
                    gameObject.yPosition + gameObject.width < 0 -> {
                        if (!gameObject.processed) {
                            gameManager?.onGameEvent(GameEvent.ADD_SCORE_ONE)
                            refreshScore()
                            player1Gain++
                        }
                        gameObjectList.remove(gameObject)
                    }

                    gameObject.yPosition > this.height -> {
                        if (!gameObject.processed && playerNum == 2) {
                            gameManager?.onGameEvent(GameEvent.ADD_SCORE_TWO)
                            refreshScore()
                            player2Gain++
                        }
                        gameObjectList.remove(gameObject)
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
                if (gameObject is Paddle && gameObject.side == 1 && gameObject.width > this.width) {
                    return 3
                }
            }
        } else if (playerNum == 2) {
            var player1Win = false
            var player2Win = false

            for (gameObject in gameObjectList) {
                if (gameObject is Paddle) {
                    when (gameObject.side) {
                        1 -> if (gameObject.width > this.width / 2) player2Win = true
                        2 -> if (gameObject.width > this.width / 2) player1Win = true
                    }
                }
            }

            if (player1Win) return 2
            if (player2Win) return 1
        }

        return 0
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
    fun setGameListener(listener: GameListener) { this.gameManager = listener }
    fun setPowerUpList(list: CopyOnWriteArrayList<PowerUp>) { this.powerUpList = list }
    fun setSplitGame(sg: Boolean) { this.isSplitGame = sg }
    fun setColors(colors: Array<Color>) { this.colors = colors }
}