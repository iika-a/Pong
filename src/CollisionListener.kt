interface CollisionListener {
    fun onCollision(event: CollisionEvent, obj1: GameObject, obj2: GameObject, gameObjectList: ArrayList<GameObject> = ArrayList())
    fun applyPowerUp(paddle: Paddle, powerUp: PowerUp, gameObjectList: ArrayList<GameObject> = ArrayList())
}