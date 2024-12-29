package net.iika.pong.util.listener

import net.iika.pong.logic.gameobject.GameObject
import net.iika.pong.logic.gameobject.Paddle
import net.iika.pong.logic.gameobject.PowerUp
import net.iika.pong.util.gameenum.CollisionEvent
import java.util.concurrent.CopyOnWriteArrayList

interface CollisionListener {
    fun onCollision(event: CollisionEvent, obj1: GameObject, obj2: GameObject = GameObject(), intersect: Double, gameObjectList: CopyOnWriteArrayList<GameObject> = CopyOnWriteArrayList())
    fun applyPowerUp(paddle: Paddle, powerUp: PowerUp, gameObjectList: CopyOnWriteArrayList<GameObject> = CopyOnWriteArrayList())
}