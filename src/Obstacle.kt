data class Obstacle (
    val xPos: Double,
    val yPos: Double,
    val wid: Double = (100..200).random().toDouble(),
    val heig: Double = (100..200).random().toDouble(),
): GameObject(xPos, yPos, 0.0, 0.0, wid, heig)