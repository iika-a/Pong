data class PowerUp (
    val xPos: Int = 0,
    val side: Int = (1..2).random(),
    val type: PowerUpType = enumValues<PowerUpType>().random()
): GameObject(xPos, if (side == 1) 710 else 0, 0.0, 0.0, 50, 10)