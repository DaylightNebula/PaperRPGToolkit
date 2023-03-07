package daylightnebula.paperrpgtoolkit.spawner

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.entities.CustomMob
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.random.Random

class MobSpawner(
    val rootLocation: Location,
    private val targetType: String,
    private val spawnRadius: Float = 2f,
    private val minTicksBetweenSpawn: Int = 20,
    private val maxTicksBetweenSpawn: Int = 60,
    private val minChildrenEntities: Int = 0,
    private val maxChildrenEntities: Int = 10,
    private val uuid: UUID = UUID.randomUUID()
) {
    companion object {
        val activeSpawners = mutableListOf<MobSpawner>()
        private val saveFile = File(PaperRPGToolkit.plugin.dataFolder, "save/spawner.json")
        private val saveJson = if (saveFile.exists()) JSONObject(saveFile.readText()) else {
            saveFile.parentFile.mkdirs()
            JSONObject()
        }

        internal fun addOrUpdateSave(spawner: MobSpawner) {
            if (saveJson.has(spawner.uuid.toString()))
                saveJson.remove(spawner.uuid.toString())
            saveJson.put(spawner.uuid.toString(), convertSpawnerToJson(spawner))
            saveFile.writeText(saveJson.toString(1))
        }

        private fun convertSpawnerToJson(spawner: MobSpawner): JSONObject {
            return JSONObject()
                .put("uuid", spawner.uuid.toString())
                .put("type", spawner.targetType)
                .put("position", arrayOf(spawner.rootLocation.x, spawner.rootLocation.y, spawner.rootLocation.z))
                .put("spawnRadius", spawner.spawnRadius)
                .put("minTicksBetweenSpawn", spawner.minTicksBetweenSpawn)
                .put("maxTicksBetweenSpawn", spawner.maxTicksBetweenSpawn)
                .put("minChildrenEntities", spawner.minChildrenEntities)
                .put("maxChildrenEntities", spawner.maxChildrenEntities)
        }

        internal fun removeSave(spawner: MobSpawner) {
            saveJson.remove(spawner.uuid.toString())
            saveFile.writeText(saveJson.toString(1))
        }

        fun startUpdateLoop() {
            // update spawners loop
            Bukkit.getScheduler().runTaskTimer(PaperRPGToolkit.plugin, Runnable {
                activeSpawners.forEach { it.update() }
            }, 1L, 1L)
        }

        fun finalizeInitialization() {
            saveJson.keys().forEach { key ->
                val json = saveJson.getJSONObject(key)
                val uuid = UUID.fromString(key)
                val positionArr = json.getJSONArray("position")
                MobSpawner(
                    Location(
                        Bukkit.getWorlds().first(),
                        positionArr.getDouble(0),
                        positionArr.getDouble(1),
                        positionArr.getDouble(2)
                    ),
                    json.getString("type"),
                    json.getFloat("spawnRadius"),
                    json.getInt("minTicksBetweenSpawn"),
                    json.getInt("maxTicksBetweenSpawn"),
                    json.getInt("minChildrenEntities"),
                    json.getInt("maxChildrenEntities"),
                    uuid
                )
            }
        }
    }

    private val activeEntities = mutableListOf<Entity>()
    private val customMob: CustomMob? = CustomMob.mobs[targetType]
    private val entityType: EntityType? = EntityType.values().firstOrNull { it.name.equals(targetType, true) }

    init {
        activeSpawners.add(this)
        println("Initialized spawner with type $targetType")
    }

    fun save() {
        addOrUpdateSave(this)
    }

    private var spawnTick = 0

    fun update() {
        // remove any dead entities
        activeEntities.removeIf { it.isDead }

        // if we have the max amount of children entities, stop here
        if (activeEntities.size >= maxChildrenEntities) return

        // if we have less than the minimum, spawn until we meet the minimum
        if (activeEntities.size < minChildrenEntities)
            repeat(minChildrenEntities - activeEntities.size) { addEntity() }

        // if we are spawning this tick, do so and then get a new random duration, otherwise, decrement duration tracker
        if (spawnTick <= 0) {
            addEntity()
            spawnTick = getRandomDurationTicks()
        } else
            spawnTick--
    }

    private fun addEntity() {
        val spawnLocation = getRandomLocationInRange(rootLocation.world) ?: rootLocation
        if (customMob != null) {
            activeEntities.add(
                customMob.spawnEntityAtLocation(spawnLocation, false)
            )
        } else if (entityType != null) {
            rootLocation.world.spawnEntity(spawnLocation, entityType)
        }
    }

    private val heightSearchThreshold = 10.0
    private fun getRandomLocationInRange(world: World): Location? {
        // get xz vector first
        val vec = Vector(Random.nextFloat() - 0.5f, 0f, Random.nextFloat() - 0.5f)
        if (vec.lengthSquared() > 1.0)
            vec.normalize()
        val baseLocation = vec.multiply(spawnRadius).toLocation(world).add(rootLocation)

        // if this block is valid just return this location
        if (isBlockValid(baseLocation.block)) return baseLocation

        // search along the y-axis for the first valid block
        var i = 1.0
        while (i < heightSearchThreshold) {
            // check if block above is valid
            val above = baseLocation.clone().add(0.0, i, 0.0)
            if (isBlockValid(above.block)) return above

            // check if block below is valid
            val below = baseLocation.clone().add(0.0, -i, 0.0)
            if (isBlockValid(below.block)) return below

            i++
        }

        // if nothing found, return nothing
        return null
    }

    private fun isBlockValid(block: Block): Boolean {
        return block.getRelative(BlockFace.DOWN).isSolid && !block.isSolid && !block.getRelative(BlockFace.UP).isSolid
    }

    private fun getRandomDurationTicks(): Int {
        return Random.nextInt(minTicksBetweenSpawn, maxTicksBetweenSpawn)
    }

    fun removeActiveEntities(removeFromJson: Boolean) {
        activeEntities.forEach { it.remove() }
        if (removeFromJson) removeSave(this)
    }
}