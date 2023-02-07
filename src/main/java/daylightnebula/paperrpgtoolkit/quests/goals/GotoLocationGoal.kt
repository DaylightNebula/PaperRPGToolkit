package daylightnebula.paperrpgtoolkit.quests.goals

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

class GotoLocationGoal(private val location: Vector, private val maxActivateDistance: Float): Listener, QuestGoal() {

    init {
        Bukkit.getPluginManager().registerEvents(this, PaperRPGToolkit.plugin)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        // make sure player has this quest
        if (!playerHasQuest(event.player)) return

        // if player is within the activation distance of the location, end the quest
        if (event.player.location.toVector().distance(location) < maxActivateDistance)
            finishQuest(event.player)
        // otherwise, update the scoreboard
        else
            quest?.chain?.updateSidebarForPlayer(event.player)
    }

    override fun forceComplete(player: Player) {
        player.teleport(location.toLocation(player.world))
    }

    override fun getDescriptionText(player: Player): String {
        return "${String.format("%.0f", player.location.toVector().distance(location))}m Away"
    }
}