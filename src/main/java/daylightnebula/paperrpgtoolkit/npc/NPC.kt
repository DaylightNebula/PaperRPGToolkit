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
    val name: String?,
    private val entityType: EntityType
) {

    companion object {
        val npcs = hashMapOf<String, NPC>()

        fun removeNearLocation(location: Location, radius: Float) {
            // get nearby entities and loop through them
            val radiusSq = radius.pow(2f)
            npcs.values.forEach { npc ->
                npc.removeEntities(
                    npc.entities.filter { it.location.distanceSquared(location) < radiusSq }
                )
            }
        }

        fun removeAllNPCs() {
            // remove all the npcs entities
            npcs.values.forEach { it.removeEntities(it.entities) }
        }
    }

    val entities = mutableListOf<Entity>()

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
            entity.customName(Component.text(name))
            entity.isCustomNameVisible = true
        }

        // save to active npc list
        entities.add(entity)

        // return the entity
        return entity
    }

    fun onRightClick(event: PlayerInteractEntityEvent) {
        onRightClick0(event)
    }

    fun removeEntities(toRemove: List<Entity>) {
        // kill all the entities and remove them from the entities list
        toRemove.forEach { it.remove() }
        entities.removeAll(toRemove.toSet())
    }

    abstract fun onCreateNewEntity(entity: Entity)
    abstract fun onRightClick0(event: PlayerInteractEntityEvent)
}