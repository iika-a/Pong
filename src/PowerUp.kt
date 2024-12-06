data class PowerUp (
    val xPos: Double = 0.0,
    val side: Int = (1..2).random(),
    val type: PowerUpType = enumValues<PowerUpType>().random()
): GameObject(xPos, if (side == 1) 710.0 else 0.0, 0.0, 0.0, 50.0, 10.0)