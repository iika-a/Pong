package net.iika.pong.logic.gameobject

import net.iika.pong.util.gameenum.PowerUpType

data class PowerUp (
    var xPos: Double,
    val side: Int = (1..2).random(),
    val type: PowerUpType = enumValues<PowerUpType>().random()
): GameObject(xPos, if (side == 1) 1070.0 else 0.0, 0.0, 0.0, 50.0, 10.0)