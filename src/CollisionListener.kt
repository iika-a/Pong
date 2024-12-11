import java.util.concurrent.CopyOnWriteArrayList

interface CollisionListener {
    fun onCollision(event: CollisionEvent, obj1: GameObject, obj2: GameObject = GameObject(), intersect: Double, gameObjectList: CopyOnWriteArrayList<GameObject> = CopyOnWriteArrayList())
    fun applyPowerUp(paddle: Paddle, powerUp: PowerUp, gameObjectList: CopyOnWriteArrayList<GameObject> = CopyOnWriteArrayList())
}