import kotlin.random.Random

class GameLoop(private val gamePanel: GamePanel, private val powerUpList: ArrayList<PowerUp>) : Runnable {

    @Volatile
    private var isRunning = false
    private val targetFPS = 240
    private val nsPerFrame = 1_000_000_000.0 / targetFPS
    private var count = 0
    private var powerUpCount = 0
    private val everyPowerUp = arrayOf(
        PowerUpType.INCREASE_PADDLE_SIZE to 0.245,
        PowerUpType.INCREASE_PADDLE_SPEED to 0.245,
        PowerUpType.RANDOMIZE_BALL_SPEED to 0.245,
        PowerUpType.RANDOMIZE_BALL_ANGLE to 0.245,
        PowerUpType.SPAWN_BALL to 0.02
    )
    private val excludeList = java.util.ArrayList<PowerUpType>()
    private var playerNum = 1

    fun start() {
        if (!isRunning) {
            isRunning = true
            Thread(this).start()
        }
    }

    fun stop() {
        isRunning = false
    }

    fun createPowerUp() {
        when (playerNum) {
            1 -> powerUpList.add(PowerUp((0..gamePanel.width).random(), 1, getRandomWithExclusions(everyPowerUp, excludeList)))
            2 -> powerUpList.add(PowerUp(xPos = (0..gamePanel.width).random(), type = getRandomWithExclusions(everyPowerUp, excludeList)))
        }
    }

    private fun getRandomWithExclusions(items: Array<Pair<PowerUpType, Double>>, toExclude: java.util.ArrayList<PowerUpType> = java.util.ArrayList()): PowerUpType {
        val filteredItems = items.filter { it.first !in toExclude }
        val totalWeight = filteredItems.sumOf { it.second }
        val cumulativeWeights = filteredItems.map { it.second / totalWeight }.runningFold(0.0) { acc, weight -> acc + weight }
        val randomValue = Random.nextDouble(1.0)

        for (i in cumulativeWeights.indices) {
            if (randomValue < cumulativeWeights[i]) {
                return filteredItems[i - 1].first
            }
        }

        throw IllegalStateException("Should never reach here")
    }

    override fun run() {
        var lastTime = System.nanoTime()
        var lag = 0.0

        while (isRunning) {
            val currentTime = System.nanoTime()
            val elapsedTime = (currentTime - lastTime) / nsPerFrame
            lastTime = currentTime
            lag += elapsedTime

            while (lag >= 1.0) {
                gamePanel.advanceGame(1.0 / targetFPS)
                lag -= 1.0

                if (count++ % 240 == 0) gamePanel.speedUpBall()
                if (count % powerUpCount == 0) createPowerUp()
            }

            Thread.sleep(1)
        }
    }

    fun setPowerUpCount(count: Int) {
        powerUpCount = count
    }

    fun getExcludeList(): ArrayList<PowerUpType> {
        return excludeList
    }

    fun setPlayerNum(playerNum: Int) {
        this.playerNum = playerNum
    }
}
