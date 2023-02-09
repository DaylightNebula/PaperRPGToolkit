package daylightnebula.paperrpgtoolkit.entities

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import kotlin.math.pow

class CustomMob(
    val id: String,
    private val displayName: String,
    private val type: EntityType,

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

        fun removeAllActiveEntities() {
            mobs.forEach { mob -> mob.removeAll() }
        }
    }

    private val entities = mutableListOf<Mob>()

    init {
        mobs.add(this)
    }

    fun spawnEntityAtLocation(location: Location) {
        // spawn entity and make sure it is a mob
        val entity = location.world.spawnEntity(location, type) as? Mob ?: return

        // set flags
        entity.isPersistent = false
        entity.isSilent = true
        entity.removeWhenFarAway = false
        entity.canPickupItems = false
        entity.equipment.clear()

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
        if (displayName != "") {
            entity.customName(Component.text(displayName))
            entity.isCustomNameVisible = true
        }

        // call on mob create
        onMobCreate(entity)

        // track the entity
        entities.add(entity)
    }

    fun removeInRange(location: Location, range: Float) {
        val rangeSq = range.pow(2f)
        val toRemove = entities.filter { it.location.distanceSquared(location) < rangeSq }
        toRemove.forEach { it.remove() }
        entities.removeAll(toRemove)
    }

    fun removeAll() {
        entities.forEach { it.remove() }
        entities.clear()
    }
}