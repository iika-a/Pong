class GameLoop(private val gamePanel: GamePanel) : Runnable {

    @Volatile
    private var isRunning = false
    private val targetFPS = 240
    private val nsPerFrame = 1_000_000_000.0 / targetFPS
    private var count = 0

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
                gamePanel.advanceGame(1.0 / targetFPS)
                lag -= 1.0

                if (count++ % 240 == 0) gamePanel.speedUpBall()
            }

            Thread.sleep(1)
        }
    }
}
