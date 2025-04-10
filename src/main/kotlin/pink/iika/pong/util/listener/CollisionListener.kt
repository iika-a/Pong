package pink.iika.pong.util.listener

import pink.iika.pong.logic.gameobject.GameObject
import pink.iika.pong.logic.gameobject.Paddle
import pink.iika.pong.logic.gameobject.PowerUp
import pink.iika.pong.util.gameenum.CollisionEvent
import java.util.concurrent.CopyOnWriteArrayList

interface CollisionListener {
    fun onCollision(event: CollisionEvent, obj1: GameObject, obj2: GameObject = GameObject(), intersect: Double, gameObjectList: CopyOnWriteArrayList<GameObject> = CopyOnWriteArrayList(), scale: Double = 1.0)
    fun applyPowerUp(paddle: Paddle, powerUp: PowerUp, gameObjectList: CopyOnWriteArrayList<GameObject> = CopyOnWriteArrayList(), scale: Double)
}