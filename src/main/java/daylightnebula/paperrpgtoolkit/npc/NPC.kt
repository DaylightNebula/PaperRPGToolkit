package daylightnebula.paperrpgtoolkit.npc

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerInteractEntityEvent
import kotlin.math.pow

abstract class NPC(
    id: String,
    val name: Component?,
    val entityType: EntityType
) {

    companion object {
        val npcs = hashMapOf<String, NPC>()
        val activeNPCs = hashMapOf<Entity, NPC>()

        fun removeNearLocation(location: Location, radius: Float) {
            // get nearby entities and loop through them
            val radiusSq = radius.pow(2f)
            activeNPCs.keys.filter { it.location.distanceSquared(location) < radiusSq }.forEach {
                // kill the entity
                it.remove()

                // remove them from active list
                activeNPCs.remove(it)
            }

        }

        fun removeAllNPCs() {
            activeNPCs.keys.forEach {
                it.remove()
            }
            activeNPCs.clear()
        }
    }

    init {
        npcs[id] = this
    }

    fun spawnAtLocation(location: Location): Entity {
        val entity = location.world.spawnEntity(location, entityType)

        // cannot be saved but cannot be killed (we will do our own spawning on start)
        entity.isPersistent = false
        entity.isInvulnerable = true

        // shh...  We will handle our own sound effects
        entity.isSilent = true

        // if the entity is a living entity, disable its AI so that it doesn't wander off
        if (entity is LivingEntity)
            entity.setAI(false)

        // set the entities name
        if (name != null) {
            entity.customName(name)
            entity.isCustomNameVisible = true
        }

        // save to active npc list
        activeNPCs[entity] = this

        // return the entity
        return entity
    }

    fun onRightClick(event: PlayerInteractEntityEvent) {
        onRightClick0(event)
    }

    abstract fun onCreateNewEntity(entity: Entity)
    abstract fun onRightClick0(event: PlayerInteractEntityEvent)
}