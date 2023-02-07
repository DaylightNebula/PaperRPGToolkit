package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import java.util.UUID

class GotoLocationGoal(private val location: Vector, private val maxActivateDistance: Float): Listener, Goal() {

    val activePlayers = mutableListOf<UUID>()

    init {
        Bukkit.getPluginManager().registerEvents(this, PaperRPGToolkit.plugin)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // make sure player has this quest
        if (!activePlayers.contains(event.player.uniqueId)) return

        // if player is within the activation distance of the location, end the quest
        if (event.player.location.toVector().distance(location) < maxActivateDistance)
            finishQuest(event.player)
        // otherwise, update the scoreboard
        else
            descriptionChanged(event.player)
    }

    override fun startForPlayer(player: Player) { activePlayers.add(player.uniqueId) }
    override fun stopForPlayer(player: Player) { activePlayers.remove(player.uniqueId) }

    override fun forceComplete(player: Player) {
        player.teleport(location.toLocation(player.world))
    }

    override fun getDescriptionText(player: Player): String {
        return "${String.format("%.0f", player.location.toVector().distance(location))}m Away"
    }
}