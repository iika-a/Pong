package pink.iika.pong.logic

import pink.iika.pong.logic.gameobject.PowerUp
import pink.iika.pong.util.gameenum.PowerUpType
import kotlin.random.Random
import java.util.concurrent.CopyOnWriteArrayList

class GameLoop(private val gamePanel: GamePanel, private val powerUpList: CopyOnWriteArrayList<PowerUp>) : Runnable {

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
    private var loopThread = Thread(this)

    fun start() {
        if (!isRunning) {
            reset()
            isRunning = true
            loopThread.start()
        }
    }

    fun stop() {
        isRunning = false
        count = 0
    }

    fun pause() {
        if (count > targetFPS * 3) {
            isRunning = false
            gamePanel.advanceGame(0.000000000001)
        }
    }

    fun resume() {
        if (!isRunning) {
            reset()
            isRunning = true
            loopThread.start()
        }
    }


    fun reset() {
        count = 0
        loopThread = Thread(this)
        gamePanel.getCountLabel().text = "3..."
    }

    fun createPowerUp() {
        when (playerNum) {
            1 -> powerUpList.add(
                PowerUp(
                    (0..gamePanel.width).random().toDouble(),
                    1,
                    getRandomWithExclusions(everyPowerUp, excludeList)
                )
            )
            2 -> powerUpList.add(
                PowerUp(
                    xPos = (0..gamePanel.width).random().toDouble(),
                    type = getRandomWithExclusions(everyPowerUp, excludeList)
                )
            )
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
        while (isRunning) {
            val startTime = System.nanoTime()

            if (++count >= targetFPS * 3) gamePanel.advanceGame(1.0 / targetFPS)
            if (count == targetFPS) gamePanel.getCountLabel().text = "2.."
            if (count == targetFPS * 2) gamePanel.getCountLabel().text = "1."
            if (count == targetFPS * 3) {
                gamePanel.setRunning(true)
                gamePanel.setPaused(false)
            }

            if (count % powerUpCount == 0 && count >= targetFPS * 3) createPowerUp()

            val elapsedTime = System.nanoTime() - startTime

            val sleepTime = nsPerFrame - elapsedTime
            if (sleepTime > 0) {
                val endTime = System.nanoTime() + sleepTime
                while (System.nanoTime() < endTime) {
                    // waiting for accurate frame timing
                }
            }
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
