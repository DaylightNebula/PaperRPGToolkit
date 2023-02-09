package daylightnebula.paperrpgtoolkit.entities

import org.bukkit.Location
import org.bukkit.entity.Mob

abstract class EntityTask {
    abstract fun getPriority(handler: CustomMob, entity: Mob): Float
    abstract fun startForEntity(handler: CustomMob, entity: Mob)
    abstract fun updateForEntity(handler: CustomMob, entity: Mob)
    abstract fun stopForEntity(handler: CustomMob, entity: Mob)
}
class StayAtPointTask(
    val location: Location? = null
): EntityTask() {
    private fun getCenterLocation(handler: CustomMob, mob: Mob): Location {
        return location ?: handler.entities[mob]!!.first
    }
    override fun getPriority(handler: CustomMob, entity: Mob): Float { return 0.01f }
    override fun startForEntity(handler: CustomMob, entity: Mob) {}
    override fun stopForEntity(handler: CustomMob, entity: Mob) {}
    override fun updateForEntity(handler: CustomMob, entity: Mob) {
        if (entity.location.distanceSquared(getCenterLocation(handler, entity)) > 0.1f)
            entity.pathfinder.moveTo(getCenterLocation(handler, entity))
    }
}