import kotlin.math.floor

class GameLoop(private val gamePanel: GamePanel) : Runnable {

    @Volatile
    private var isRunning = false
    private val targetFPS = 60
    private val nsPerFrame = 1_000_000_000.0 / targetFPS

    fun start() {
        if (!isRunning) {
            isRunning = true
            Thread(this).start()
        }
    }

    fun stop() {
        isRunning = false
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
                val loops = floor(lag).toInt()
                for (i in 1..loops) gamePanel.advanceGame(1.0 / targetFPS)
                lag -= loops
            }

            Thread.sleep(1)
        }
    }
}
