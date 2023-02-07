package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.goals.Goal
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import java.util.UUID

class SelectNumberedOptionGoal: Listener, Goal() {

    private val prevSlots = hashMapOf<UUID, Int>()

    init {
        Bukkit.getPluginManager().registerEvents(this, PaperRPGToolkit.plugin)
    }

    @EventHandler
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        // ignore this event if the player is not an active player or the slot chosen is the last one
        if (!prevSlots.containsKey(event.player.uniqueId) || event.newSlot == 8) return

        // after 1 tick, set back to previous slot
        val oldSlot = prevSlots[event.player.uniqueId]!!
        Bukkit.getScheduler().runTaskLater(PaperRPGToolkit.plugin, Runnable { event.player.inventory.heldItemSlot = oldSlot },1L)

        // the goal is complete, IDK why I need to run this in a scheduler but without it, it breaks other things
        Bukkit.getScheduler().runTaskLater(PaperRPGToolkit.plugin, Runnable { finishGoal(event.player) }, 0L)
    }

    override fun startForPlayer(player: Player) {
        prevSlots[player.uniqueId] = player.inventory.heldItemSlot
        player.inventory.heldItemSlot = 8
    }

    override fun stopForPlayer(player: Player) { prevSlots.remove(player.uniqueId) }

    override fun forceComplete(player: Player) {
        finishGoal(player)
    }

    override fun getDescriptionText(player: Player): String {
        return "Select an option 1-8"
    }
}