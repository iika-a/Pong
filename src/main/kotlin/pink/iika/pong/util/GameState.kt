package pink.iika.pong.util

data class GameState(
    val paddle1X: Double,
    val paddle1Width: Double,
    val paddle2X: Double,
    val paddle2Width: Double,
    val ballX: Double,
    val ballY: Double,
    var score1: Int,
    var score2: Int
)