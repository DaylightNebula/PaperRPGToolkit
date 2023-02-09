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
import kotlin.random.Random

class MobSpawner(
    val rootLocation: Location,
    private val spawnRadius: Float = 2f,
    private val entityType: EntityType? = null,
    private val customMobID: String = "",
    private val minTicksBetweenSpawn: Int = 20,
    private val maxTicksBetweenSpawn: Int = 60,
    private val minChildrenEntities: Int = 0,
    private val maxChildrenEntities: Int = 10
) {
    companion object {
        val activeSpawners = mutableListOf<MobSpawner>()

        fun startUpdateLoop() {
            Bukkit.getScheduler().runTaskTimer(PaperRPGToolkit.plugin, Runnable {
                activeSpawners.forEach { it.update() }
            }, 1L, 1L)
        }
    }

    private val activeEntities = mutableListOf<Entity>()

    init {
        activeSpawners.add(this)
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
        if (spawnTick == 0) {
            addEntity()
            spawnTick = getRandomDurationTicks()
        } else
            spawnTick--
    }

    private fun addEntity() {
        val spawnLocation = getRandomLocationInRange(rootLocation.world) ?: rootLocation
        val mobHandler = CustomMob.mobs.firstOrNull { it.id == customMobID }
        if (mobHandler != null) {
            activeEntities.add(
                mobHandler.spawnEntityAtLocation(spawnLocation)
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

    fun removeActiveEntities() {
        activeEntities.forEach { it.remove() }
    }
}