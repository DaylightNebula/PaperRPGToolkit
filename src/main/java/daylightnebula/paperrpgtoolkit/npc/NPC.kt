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
import org.bukkit.util.Vector
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID
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
        val saveFile = File(PaperRPGToolkit.plugin.dataFolder, "save/npcs.json")
        val saveJson = if (saveFile.exists()) { JSONObject(saveFile.readText()) } else { saveFile.parentFile.mkdirs(); JSONObject() }

        fun saveJsonToFile() {
            val json = JSONObject()
            npcs.forEach { (id, npc) ->
                val arr = JSONArray()
                npc.entities.forEach { (entity, pair) ->
                    arr.put(
                        JSONObject()
                            .put("uuid", pair.first.toString())
                            .put("position", arrayOf(entity.location.x, entity.location.y, entity.location.z))
                            .put("direction", arrayOf(entity.location.direction.x, entity.location.direction.y, entity.location.direction.z))
                    )
                }
                json.put(id, arr)
            }
            saveFile.writeText(json.toString(1))
        }

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
            // load waiting json
            waitingJson.forEach { (id, json) ->
                NPC(id, json)
            }

            // spawn saved NPCs
            Bukkit.getScheduler().runTaskLater(PaperRPGToolkit.plugin, Runnable {
                saveJson.keys().forEach { key ->
                    saveJson.getJSONArray(key).forEach { j ->
                        val json = j as JSONObject
                        val uuid = UUID.fromString(json.getString("uuid"))
                        val locationArr = json.getJSONArray("position")
                        val directionArr = json.getJSONArray("direction")
                        val location = Location(Bukkit.getWorlds().first(), locationArr.getDouble(0), locationArr.getDouble(1), locationArr.getDouble(2))
                        location.apply {
                            this.direction = Vector(
                                directionArr.getDouble(0),
                                directionArr.getDouble(1),
                                directionArr.getDouble(2),
                            )
                        }
                        npcs[key]?.spawnAtLocation(location, false, uuid)
                    }
                }
            }, 10L)
        }

        fun removeNearLocation(location: Location, radius: Float, save: Boolean) {
            // get nearby entities and loop through them
            val radiusSq = radius.pow(2f)
            npcs.values.forEach { npc ->
                npc.removeEntities(
                    npc.entities.keys.filter { it.location.distanceSquared(location) < radiusSq },
                    save
                )
            }
        }

        fun removeAllNPCs(save: Boolean) {
            // remove all the npcs entities
            npcs.values.forEach { it.removeEntities(it.entities.keys.toList(), save) }
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

    val entities = hashMapOf<Entity, Pair<UUID, Boolean>>()

    init {
        npcs[id] = this
    }

    fun spawnAtLocation(location: Location, save: Boolean, uuid: UUID = UUID.randomUUID()): Entity {
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
        entities[entity] = Pair(uuid, save)

        if (save) saveJsonToFile()

        // return the entity
        return entity
    }

    fun onRightClick(event: PlayerInteractEntityEvent) {
        onClickAction?.run(event.player)
    }

    fun removeEntities(toRemove: List<Entity>, save: Boolean) {
        // kill all the entities and remove them from the entities list
        toRemove.forEach { entity ->
            entity.remove()
            entities.remove(entity)
        }
        if (save) saveJsonToFile()
    }
}
