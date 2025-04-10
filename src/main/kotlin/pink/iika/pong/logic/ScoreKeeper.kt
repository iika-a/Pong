package pink.iika.pong.logic

data class ScoreKeeper (
    var score1: Double,
    var score2: Double
) {
    fun resetScore() {
        score1 = 0.0
        score2 = 0.0
    }
}