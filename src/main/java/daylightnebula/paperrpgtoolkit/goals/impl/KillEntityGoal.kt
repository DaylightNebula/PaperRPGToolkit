package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.pow

class KillEntityGoal(
    private val entityType: EntityType,
    private val minKills: Int,
    private val location: Vector? = null,
    private val radius: Float = 1f
): Listener, Goal() {

    private val killsCounter = hashMapOf<UUID, Int>()

    init {
        Bukkit.getPluginManager().registerEvents(this, PaperRPGToolkit.plugin)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        // get target and make sure it has the correct type
        val target = event.entity
        if (target.type != entityType) return

        // get killed by, cancel if it is not a player
        val killedBy = (target.lastDamageCause as? EntityDamageByEntityEvent ?: return).damager as? Player ?: return

        // if killed by is not in the quest, cancel
        if (!killsCounter.containsKey(killedBy.uniqueId)) return

        // if we were given a location, make sure the player is within the given radius of that location
        if (location != null && target.location.toVector().distanceSquared(location) > radius.pow(2f)) return

        // get new kills count
        val newKills = (killsCounter[killedBy.uniqueId] ?: 0) + 1

        // if new kills exceeds min kill count, stop the quest and remove the player from the tracker
        if (newKills >= minKills) {
            finishQuest(killedBy)
        }
        // otherwise, update the kill tracker and update the scoreboard
        else {
            killsCounter[killedBy.uniqueId] = newKills
            descriptionChanged(killedBy)
        }
    }

    override fun startForPlayer(player: Player) { killsCounter[player.uniqueId] = 0 }
    override fun stopForPlayer(player: Player) { killsCounter.remove(player.uniqueId) }

    override fun forceComplete(player: Player) {
        finishQuest(player)
    }

    override fun getDescriptionText(player: Player): String {
        val kills = killsCounter[player.uniqueId] ?: return ""
        return "$kills/$minKills"
    }
}