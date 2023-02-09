package daylightnebula.paperrpgtoolkit.entities

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import kotlin.math.pow

class CustomMob(
    val id: String,
    displayName: String,
    val supertype: EntityType,

    // stats
    private val maxHealth: Double? = null,
    private val armor: Double? = null,
    private val armorToughness: Double? = null,
    private val movementSpeed: Double? = null,
    private val attackDamage: Double? = null,
    private val attackKnockBack: Double? = null,
    private val attackSpeed: Double? = null,
    private val followRange: Double? = null,
    private val flyingSpeed: Double? = null,
    private val knockBackResistance: Double? = null,
    private val luck: Double? = null,

    // other stuffs
    private val tasks: Array<EntityTask> = arrayOf(),
    private val onMobCreate: (mob: Mob) -> Unit = {}
) {
    companion object {
        val mobs = mutableListOf<CustomMob>()

        fun startUpdateLoop() {
            Bukkit.getScheduler().runTaskTimer(PaperRPGToolkit.plugin, Runnable {
                mobs.forEach { it.updateAllEntities() }
            }, 1L, 1L)
        }

        fun removeAllActiveEntities() {
            mobs.forEach { mob -> mob.removeAll() }
        }
    }

    val dnComponents = Component.text(displayName)
    val entities = hashMapOf<Mob, Pair<Location, Int>>() // Format: bukkit entity, current task index

    init {
        mobs.add(this)
    }

    fun spawnEntityAtLocation(location: Location) {
        // spawn entity and make sure it is a mob
        val entity = location.world.spawnEntity(location, supertype) as? Mob ?: return

        // set flags
        entity.isPersistent = false
        entity.isSilent = true
        entity.removeWhenFarAway = false
        entity.canPickupItems = false
        entity.equipment.clear()

        Bukkit.getMobGoals().removeAllGoals(entity)

        // set stats
        if (maxHealth != null) entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = maxHealth
        if (armor != null) entity.getAttribute(Attribute.GENERIC_ARMOR)?.baseValue = armor
        if (armorToughness != null) entity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS)?.baseValue = armorToughness
        if (movementSpeed != null) entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = movementSpeed
        if (attackSpeed != null) entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = attackSpeed
        if (attackDamage != null) entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = attackDamage
        if (attackKnockBack != null) entity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK)?.baseValue = attackKnockBack
        if (followRange != null) entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE)?.baseValue = followRange
        if (knockBackResistance != null) entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)?.baseValue = knockBackResistance
        if (flyingSpeed != null) entity.getAttribute(Attribute.GENERIC_FLYING_SPEED)?.baseValue = flyingSpeed
        if (luck != null) entity.getAttribute(Attribute.GENERIC_LUCK)?.baseValue = luck

        // set display name
        if (dnComponents.content().isNotBlank()) {
            entity.customName(dnComponents)
            entity.isCustomNameVisible = true
        }

        // call on mob create
        onMobCreate(entity)

        // track the entity
        entities[entity] = Pair(location, getTaskForEntity(entity))
        startCurrentTaskForEntity(entity)
    }

    private fun updateAllEntities() {
        // for each active entity
        entities.forEach { (mob, pair) ->
            val curTaskIdx = pair.second

            // check if its task has changed
            val taskID = getTaskForEntity(mob)
            if (taskID == curTaskIdx) {
                // if task has not changed, update and cancel
                tasks[taskID].updateForEntity(this, mob)
                return@forEach
            }

            // stop the current task and swap to and then star the next task
            stopCurrentTaskForEntity(mob)
            entities[mob] = Pair(entities[mob]?.first ?: mob.location, taskID)
            startCurrentTaskForEntity(mob)
        }
    }

    private fun getTaskForEntity(entity: Mob): Int {
        val task = tasks.maxBy { it.getPriority(this, entity) }
        return tasks.indexOf(task)
    }

    private fun startCurrentTaskForEntity(entity: Mob) {
        tasks[entities[entity]?.second ?: return].startForEntity(this, entity)
    }

    private fun stopCurrentTaskForEntity(entity: Mob) {
        tasks[entities[entity]?.second ?: return].stopForEntity(this, entity)
    }

    fun removeInRange(location: Location, range: Float) {
        val rangeSq = range.pow(2f)
        val toRemove = entities.filter { it.key.location.distanceSquared(location) < rangeSq }
        toRemove.forEach {
            stopCurrentTaskForEntity(it.key)
            it.key.remove()
            entities.remove(it.key)
        }
    }

    fun removeAll() {
        entities.forEach {
            stopCurrentTaskForEntity(it.key)
            it.key.remove()
        }
        entities.clear()
    }
}