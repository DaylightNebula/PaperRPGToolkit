package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.*

class PressShiftGoal: Listener, Goal() {

    private val activePlayers = mutableListOf<UUID>()

    init {
        Bukkit.getPluginManager().registerEvents(this, PaperRPGToolkit.plugin)
    }

    @EventHandler
    fun onPlayerSneak(event: PlayerToggleSneakEvent) {
        if (event.isSneaking && activePlayers.contains(event.player.uniqueId))
            finishGoal(event.player)
    }

    override fun startForPlayer(player: Player) {
        activePlayers.add(player.uniqueId)
    }

    override fun stopForPlayer(player: Player) {
        activePlayers.remove(player.uniqueId)
    }

    override fun forceComplete(player: Player) {
        finishGoal(player)
    }

    override fun getDescriptionText(player: Player): String {
        return "Press shift to continue."
    }
}