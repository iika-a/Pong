package pink.iika.pong.logic

import pink.iika.pong.logic.gameobject.Obstacle
import pink.iika.pong.logic.gameobject.Paddle
import pink.iika.pong.logic.gameobject.PowerUp
import pink.iika.pong.util.gameenum.PowerUpType
import pink.iika.pong.logic.gameobject.Ball
import pink.iika.pong.logic.gameobject.GameObject
import pink.iika.pong.util.gameenum.CollisionEvent
import pink.iika.pong.util.gameenum.GameEvent
import pink.iika.pong.util.listener.ButtonMouseListener
import pink.iika.pong.util.listener.GameCollisionListener
import pink.iika.pong.util.listener.GameListener
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Rectangle2D
import java.awt.geom.Ellipse2D
import java.awt.geom.Path2D
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.LineBorder
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI
import kotlin.random.Random

class GamePanel(private val gameObjectList: CopyOnWriteArrayList<GameObject>, private val scoreKeeper: ScoreKeeper, private val buttonMouseListener: ButtonMouseListener): JPanel(), KeyListener {
    @Volatile
    private var isRunning = false
    private var player1Gain = 0
    private var player2Gain = 0
    private val gameFont = Font("Segoe UI", 0, 20)
    private val countLabel = JLabel("3...").apply { font = gameFont.deriveFont(26f) }
    private val lossLabel = JLabel("You Lose!").apply { font = gameFont.deriveFont(26f) }
    private val multiLossLabel = JLabel("Player 1: +$player1Gain      Player 2: +$player2Gain").apply { font = gameFont }
    private val winLabel = JLabel("You Win!").apply { font = gameFont.deriveFont(26f) }
    private val winLabel1 = JLabel("<html>Player 1 Won. Continue for 1 point<br>⠀⠀⠀⠀⠀⠀⠀Or exit for 0.5?</html>").apply { font = gameFont.deriveFont(18f) }
    private val winLabel2 = JLabel("<html>Player 2 Won. Continue for 1 point<br>⠀⠀⠀⠀⠀⠀⠀Or exit for 0.5?</html>").apply { font = gameFont.deriveFont(18f) }
    private val scoreLabel1 = JLabel("Player 1 Score: ${scoreKeeper.score1}").apply { font = gameFont }
    private val scoreLabel2 = JLabel("Player 2 Score: ${scoreKeeper.score2}").apply { font = gameFont }
    private val pauseLabel = JLabel("Game is paused.").apply { font = gameFont.deriveFont(26f) }
    private val replayButton = JButton("Play Again").apply { setButtonSettings(this) }
    private val exitButton = JButton("Return to Menu").apply { setButtonSettings(this) }
    private val continueButton = JButton("Continue Game").apply { setButtonSettings(this) }
    private val resumeButton = JButton("Resume Game").apply { setButtonSettings(this) }
    private var gameManager: GameListener? = null
    private var collisionListener: GameCollisionListener = GameCollisionListener()
    private var playerNum = 0
    private var powerUpList = CopyOnWriteArrayList<PowerUp>()
    private var isSplitGame = false
    private var isDoubleBall = false
    private var colors = arrayOf(Color(0xE4A8CA), Color(0xCCAA87), Color(0xBB6588), Color(0x8889CC))
    private var isPaused = false
    private var scaleX = 1.0
    private var scaleY = 1.0

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
        scoreLabel1.verticalAlignment = JLabel.BOTTOM
        scoreLabel1.isVisible = false

        scoreLabel2.horizontalAlignment = JLabel.LEFT
        scoreLabel2.verticalAlignment = JLabel.TOP
        scoreLabel2.isVisible = false

        countLabel.horizontalAlignment = JLabel.CENTER
        countLabel.verticalAlignment = JLabel.CENTER
        countLabel.isVisible = true

        pauseLabel.horizontalAlignment = JLabel.CENTER
        pauseLabel.verticalAlignment = JLabel.CENTER

        replayButton.addActionListener { gameManager?.onGameEvent(GameEvent.REPLAY_GAME) }
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
            }
        }
        resumeButton.addActionListener {
            resume()
            repaint()
        }

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
        this.add(countLabel)
        this.add(resumeButton)
        this.add(pauseLabel)
        this.addKeyListener(this)
    }

    override fun keyPressed(e: KeyEvent?) {
        for (paddle in gameObjectList.filterIsInstance<Paddle>()) {
            if (e?.keyCode == paddle.leftKey) paddle.leftPress = true
            if (e?.keyCode == paddle.rightKey) paddle.rightPress = true
        }
        if (e?.keyCode == KeyEvent.VK_ESCAPE) {
            pause()
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        super.paintComponent(g2d)

        for (powerUp in powerUpList) {
            g2d.color = Color.BLACK
            when (powerUp.side) {
                1 -> g2d.fill(Rectangle2D.Double(powerUp.xPosition - 2, this.height - 10 - 2.0, 54.0, 12.0))
                2 -> g2d.fill(Rectangle2D.Double(powerUp.xPosition - 2, 0.0, 54.0, 12.0))
            }
            powerUp.width *= scaleX

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
            obstacle.width *= scaleX
            obstacle.height *= scaleY

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

        if (checkForLoss() != 0 || checkForWin() != 0 || (isPaused && !isRunning)) {
            g2d.color = Color.BLACK
            g2d.fill(Rectangle2D.Double(this.width/2 - 200 - 2.0, this.height/2 - 70 - 2.0, 404.0, 309.0))
            g2d.color = Color(0xFFD1DC)
            g2d.fill(Rectangle2D.Double(this.width/2 - 200.0, this.height/2 - 70.0, 400.0, 305.0))
        }

        if (!isRunning && !isPaused) {
            g2d.color = Color.BLACK
            g2d.fill(Rectangle2D.Double(this.width/2 - 27.0, this.height/2 - 27.0, 54.0, 54.0))
            g2d.color = Color(0xFFD1DC)
            g2d.fill(Rectangle2D.Double(this.width/2 - 25.0, this.height/2 - 25.0, 50.0, 50.0))

            for (ball in gameObjectList.filterIsInstance<Ball>()) drawArrow(g2d, ball.xPosition + ball.width/2, ball.yPosition + ball.height/2, ball.velocityAngle)
        }
    }

    fun advanceGame(dt: Double) {
        for (gameObject in gameObjectList) {
            when (gameObject) {
                is Ball -> {
                    gameObject.move(gameObject.xVelocity * dt * scaleX, gameObject.yVelocity * dt * scaleY)
                    speedUpBall(5 * dt)
                }

                is Paddle -> {
                    if (gameObject.leftPress) gameObject.move(-gameObject.paddleSpeed * dt * scaleX, 0.0)
                    if (gameObject.rightPress) gameObject.move(gameObject.paddleSpeed * dt * scaleX, 0.0)
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
                refreshGains()
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
                winLabel.isVisible = true
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
                        else this.width - gameObject.xPosition - gameObject.width

                        gameObject.velocityAngle = atan2(gameObject.yVelocity, -1 * gameObject.xVelocity)
                        collisionListener.onCollision(event = CollisionEvent.BALL_WALL, obj1 = gameObject, intersect = wallIntersect)
                    }

                    for (otherObject in gameObjectList) {
                        if (otherObject is Paddle && GameCollisionListener.checkIntersect(gameObject, otherObject)) {
                            gameObject.velocityAngle = atan2(-1 * gameObject.yVelocity, gameObject.xVelocity)

                            when (otherObject.side) {
                                1 -> {
                                    if (gameObject.yPosition + gameObject.height >= otherObject.yPosition) {
                                        val intersect = otherObject.yPosition - gameObject.yPosition - gameObject.height
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

                        if (otherObject == Obstacle(200.0, 200.0, this.width - 400.0, this.height - 400.0) && !GameCollisionListener.checkIntersect(gameObject, otherObject)) gameObject.isImmune = false

                        if (gameObject != otherObject && GameCollisionListener.checkIntersect(gameObject, otherObject) && otherObject !is Paddle && !gameObject.isImmune) {
                            val xIntersect = smallerAbsoluteValueWithSign(otherObject.xPosition - gameObject.xPosition - gameObject.width, otherObject.xPosition + otherObject.width - gameObject.xPosition)
                            val yIntersect = smallerAbsoluteValueWithSign(otherObject.yPosition - gameObject.yPosition - gameObject.height, otherObject.yPosition + otherObject.height - gameObject.yPosition)
                            if (abs(xIntersect) < abs(yIntersect)) gameObject.velocityAngle = atan2(gameObject.yVelocity, -1 * gameObject.xVelocity)
                            if (abs(xIntersect) > abs(yIntersect)) gameObject.velocityAngle = atan2(-1 * gameObject.yVelocity, gameObject.xVelocity)

                            when (otherObject) {
                                is Ball -> {
                                    if (abs(xIntersect) < abs(yIntersect)) collisionListener.onCollision(CollisionEvent.BALL_BALL_SIDE, gameObject, otherObject, xIntersect)
                                    else collisionListener.onCollision(CollisionEvent.BALL_BALL_TOP_BOTTOM, gameObject, otherObject, yIntersect)
                                }
                                is Obstacle -> {
                                    if (abs(xIntersect) < abs(yIntersect)) collisionListener.onCollision(CollisionEvent.BALL_OBSTACLE_SIDE, gameObject, otherObject, xIntersect)
                                    else collisionListener.onCollision(CollisionEvent.BALL_OBSTACLE_TOP_BOTTOM, gameObject, otherObject, yIntersect)
                                }
                            }
                        }
                    }
                }

                is Paddle -> {
                    if (gameObject.xPosition <= 0) gameObject.xPosition = 0.0
                    if (gameObject.xPosition + gameObject.width >= this.width) gameObject.xPosition = this.width - gameObject.width

                    for (powerUp in powerUpList) {
                        if (((powerUp.xPosition in gameObject.xPosition..(gameObject.xPosition + gameObject.width)) || (powerUp.xPosition + powerUp.width in gameObject.xPosition..(gameObject.xPosition + gameObject.width))) && gameObject.side == powerUp.side) {
                            collisionListener.onCollision(CollisionEvent.PADDLE_POWERUP, gameObject, powerUp, 0.0, gameObjectList, scaleX)
                            powerUpList.remove(powerUp)
                        }
                    }
                }
            }
        }
    }

    fun initializeBall() {
        if (isSplitGame || isDoubleBall) gameObjectList.add(Ball(initialDirection = 1))

        for (gameObject in gameObjectList) {
            if (gameObject is Ball) {
                gameObject.xPosition = ((this.width/4)..(2 * this.width/3)).random().toDouble()
                gameObject.yPosition = this.height / 2.0
                gameObject.ballRadius *= scaleX
                gameObject.height *= scaleY
                gameObject.width *= scaleX

                if (gameObjectList.filterIsInstance<Obstacle>().isNotEmpty() && gameObjectList.filterIsInstance<Obstacle>()[0] == Obstacle(200.0, 200.0, this.width - 400.0, this.height - 400.0)) gameObject.isImmune = true

                if (isSplitGame && gameObject.initialDirection == 0) gameObject.xPosition = this.width / 2 - this.width / 4.0
                if (isSplitGame && gameObject.initialDirection == 1) gameObject.xPosition = this.width / 2 + this.width / 4.0
                if (gameObject.isTemporary) gameObjectList.remove(gameObject)

                gameObject.processed = false
                gameObject.ballSpeed = (650..725).random().toDouble()
                gameObject.velocityAngle = getRandomAngle(gameObject.initialDirection)
                gameObject.xVelocity = gameObject.ballSpeed * cos(gameObject.velocityAngle)
                gameObject.yVelocity = gameObject.ballSpeed * sin(gameObject.velocityAngle)
            }
        }
    }

    fun initializePaddles(paddleNum: Int = 0) {
        var i = 0

        for (paddle in gameObjectList.filterIsInstance<Paddle>()) {
            if (playerNum == 2) {
                if (paddleNum == 0 || paddle.side == paddleNum) {
                    paddle.width = 150.0 * scaleX
                    paddle.paddleSpeed = 800.0
                    paddle.xPosition = this.width / 2 - paddle.width / 2
                }
                if (paddle.side == 1 && !isSplitGame) paddle.xPosition = this.width / 2 - paddle.width / 2
                if (isSplitGame && i % 2 == 0) paddle.xPosition = this.width / 2 - this.width / 4 - paddle.width / 2
                if (isSplitGame && i++ % 2 == 1) paddle.xPosition = this.width / 2 + this.width / 4 - paddle.width / 2
            } else if (playerNum == 1) {
                if (paddle.side == 2) {
                    paddle.xPosition = 0.0
                    paddle.width = 5000.0
                    paddle.paddleSpeed = 0.0
                } else if (paddle.side == 1) {
                    paddle.xPosition = this.width / 2 - paddle.width / 2

                    paddle.width = 150.0 * scaleX
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
        lossLabel.setBounds(this.width/2 - 138, this.height/2 - 66, 276, 85)
        pauseLabel.setBounds(this.width/2 - 138, this.height/2 - 66, 276, 85)
        multiLossLabel.setBounds(this.width/2 - 138, this.height/2 - 66, 276, 85)
        replayButton.setBounds(this.width/2 - 138, this.height/2 + 22, 276, 85)
        resumeButton.setBounds(this.width/2 - 138, this.height/2 + 22, 276, 85)
        continueButton.setBounds(this.width/2 - 138, this.height/2 + 22, 276, 85)
        exitButton.setBounds(this.width/2 - 138, this.height/2 + 110, 276, 85)
        winLabel.setBounds(this.width/2 - 138, this.height/2 - 66, 276, 85)
        winLabel1.setBounds(this.width/2 - 138, this.height/2 - 105, 276, 170)
        winLabel2.setBounds(this.width/2 - 138, this.height/2 - 105, 276, 170)
        scoreLabel1.setBounds(this.width - 275, this.height - 95, 276, 85)
        scoreLabel2.setBounds(0, 15, 276, 85)
        countLabel.setBounds(this.width/2 - 25, this.height/2 - 25, 50, 50)
        player1Gain = 0
        player2Gain = 0

        pauseLabel.isVisible = false
        winLabel.isVisible = false
        winLabel1.isVisible = false
        winLabel2.isVisible = false
        lossLabel.isVisible = false
        multiLossLabel.isVisible = false
        replayButton.isVisible = false
        resumeButton.isVisible = false
        exitButton.isVisible = false
        continueButton.isVisible = false
        setRunning(false)
        countLabel.text = "3..."
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
        repaint()
    }

    private fun getRandomAngle(direction: Int): Double {
        return if (direction == 0) Random.nextDouble(PI/9, 7 * PI/18) else Random.nextDouble(PI/9 + PI, 7 * PI/18 + PI)
    }

    private fun speedUpBall(increment: Double = 0.0) {
        for (ball in gameObjectList.filterIsInstance<Ball>()) {
            ball.ballSpeed += increment
            ball.xVelocity = ball.ballSpeed * cos(ball.velocityAngle)
            ball.yVelocity = ball.ballSpeed * sin(ball.velocityAngle)
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
        val menuFont = Font("Segoe UI", 0, 26)
        val buttonColor = Color(0xD1F6FF)
        val buttonBorder = LineBorder(Color.WHITE, 3)

        button.font = menuFont
        button.background = buttonColor
        button.border = buttonBorder
        button.addMouseListener(buttonMouseListener)
    }

    private fun drawArrow(g2d: Graphics2D, startX: Double, startY: Double, angle: Double) {
        val headLength = 20.0
        val offset = 20.0

        val offsetX = startX + offset * cos(angle)
        val offsetY = startY + offset * sin(angle)

        val endX = offsetX + headLength * cos(angle)
        val endY = offsetY + headLength * sin(angle)

        val leftHeadX = endX - headLength * cos(angle - PI / 6)
        val leftHeadY = endY - headLength * sin(angle - PI / 6)
        val rightHeadX = endX - headLength * cos(angle + PI / 6)
        val rightHeadY = endY - headLength * sin(angle + PI / 6)

        val arrowhead = Path2D.Double().apply {
            moveTo(endX, endY)
            lineTo(leftHeadX, leftHeadY)
            lineTo(rightHeadX, rightHeadY)
            closePath()
        }

        g2d.color = Color(0xFFD1DC)
        g2d.fill(arrowhead)

        g2d.stroke = BasicStroke(2f)
        g2d.color = Color.BLACK
        g2d.draw(arrowhead)
    }

    private fun pause() {
        if (isRunning) {
            resumeButton.isVisible = true
            exitButton.isVisible = true
            pauseLabel.isVisible = true
            isPaused = true
            isRunning = false
            gameManager?.onGameEvent(GameEvent.PAUSE_GAME)
        }
    }

    private fun resume() {
        isPaused = false
        pauseLabel.isVisible = false
        exitButton.isVisible = false
        resumeButton.isVisible = false
        countLabel.isVisible = true
        gameManager?.onGameEvent(GameEvent.RESUME_GAME)
    }

    private fun smallerAbsoluteValueWithSign(a: Double, b: Double) = if (abs(a) < abs(b)) a else b

    fun setRunning(isRunning: Boolean) { this.isRunning = isRunning; countLabel.isVisible = !isRunning }
    fun setPlayers(playerNum: Int) { this.playerNum = playerNum }
    fun setGameListener(listener: GameListener) { this.gameManager = listener }
    fun setPowerUpList(list: CopyOnWriteArrayList<PowerUp>) { this.powerUpList = list }
    fun setSplitGame(sg: Boolean) { this.isSplitGame = sg }
    fun setDoubleBall(db: Boolean) { this.isDoubleBall = db }
    fun setColors(colors: Array<Color>) { this.colors = colors }
    fun getCountLabel() = this.countLabel
    fun setPaused(p: Boolean) { isPaused = p }
    fun setScale(sx: Double, sy: Double) { scaleX = sx; scaleY = sy; repaint() }
}