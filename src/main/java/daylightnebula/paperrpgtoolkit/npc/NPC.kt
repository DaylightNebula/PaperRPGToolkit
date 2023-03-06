package daylightnebula.paperrpgtoolkit.npc

import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.actions.Action
import daylightnebula.paperrpgtoolkit.goals.Goal
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.json.JSONObject
import java.io.File
import kotlin.IllegalArgumentException
import kotlin.math.pow

class NPC(
    val id: String,
    val name: String?,
    private val entityType: EntityType,
    private val onClickAction: Action?
) {

    companion object {
        val npcs = hashMapOf<String, NPC>()
        private val waitingJson = hashMapOf<String, JSONObject>()

        fun loadJsonFromFolder(root: File) {
            root.listFiles()?.forEach { file ->
                if (file.extension == "json") {
                    val json = JSONObject(file.readText())
                    val id = json.getString("id")
                    waitingJson[id] = json
                } else if (file.isDirectory)
                    loadJsonFromFolder(file)
            }
        }

        fun loadWaitingJson() {
            waitingJson.forEach { (id, json) ->
                NPC(id, json)
            }
        }

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

    constructor(id: String): this(id, waitingJson[id] ?: throw IllegalArgumentException("Could not find json for id $id"))

    constructor(id: String, json: JSONObject): this(
        id,
        json.getString("name"),
        EntityType.values().firstOrNull { it.name.equals(json.getString("type"), ignoreCase = true) }
            ?: throw IllegalArgumentException("No entity type matches ${json.getString("type")}"),
        Action.decode(json.optJSONObject("on_click_action"))
    )

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

        // if living entity, we have some more options to change
        if (entity is LivingEntity) {
            entity.removeWhenFarAway = false
            entity.canPickupItems = false
            entity.equipment?.clear()
            entity.noDamageTicks = Int.MAX_VALUE
            entity.setAI(false)
        }

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
        onClickAction?.run(event.player)
    }

    fun removeEntities(toRemove: List<Entity>) {
        // kill all the entities and remove them from the entities list
        toRemove.forEach { it.remove() }
        entities.removeAll(toRemove)
    }
}
