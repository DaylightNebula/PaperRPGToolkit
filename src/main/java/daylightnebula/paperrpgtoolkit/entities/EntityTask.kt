package daylightnebula.paperrpgtoolkit.entities

import org.bukkit.Location
import org.bukkit.entity.Mob

abstract class EntityTask {
    abstract fun getPriority(entity: Mob): Float
    abstract fun startForEntity(entity: Mob)
    abstract fun updateForEntity(entity: Mob)
    abstract fun stopForEntity(entity: Mob)
}
class StayAtPointTask(
    val location: Location
): EntityTask() {
    override fun getPriority(entity: Mob): Float { return 0.01f }
    override fun startForEntity(entity: Mob) {}
    override fun stopForEntity(entity: Mob) {}
    override fun updateForEntity(entity: Mob) {
        if (entity.location.distanceSquared(location) > 0.1f)
            entity.pathfinder.moveTo(location)
    }
}
//class WanderNearPoint(
//    val location: Location,
//    val maxDistance: Float,
//    val minPauseTicks: Int,
//    val maxPauseTicks: Int
//): EntityTask() {
//    override fun getPriority(): Float {
//        return 0.02f
//    }
//}