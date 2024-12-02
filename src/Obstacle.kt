data class Obstacle (
    val xPos: Int,
    val yPos: Int,
    val wid: Int = (100..200).random(),
    val heig: Int = (100..200).random(),
): GameObject(xPos, yPos, 0.0, 0.0, wid, heig)