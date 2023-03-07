package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.dialogue.DialogueChain
import daylightnebula.paperrpgtoolkit.goals.impl.ClickNPCWithItemGoal
import daylightnebula.paperrpgtoolkit.items.CustomItem
import daylightnebula.paperrpgtoolkit.npc.NPC
import daylightnebula.paperrpgtoolkit.quests.QuestChain
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType

class EventListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        QuestChain.playerJoin(event.player)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        handleCustomItemInteractEvent(event)
    }

    private fun handleCustomItemInteractEvent(event: PlayerInteractEvent) {
        // get item used
        val item = event.item

        // try to get the items custom item key if it has one by asking the items persistent data container for it, if no custom item key was found, cancel
        val itemKey = item?.itemMeta?.persistentDataContainer?.get(PaperRPGToolkit.customItemReferenceIDKey, PersistentDataType.STRING) ?: return

        // try to get a custom item from the item key, if nothing is found, cancel
        val customItem = CustomItem.items[itemKey] ?: return

        // call custom items click function
        customItem.click(event)
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        // get player
        val player = event.player

        // get target entity
        val target = event.rightClicked

        // check if target entity is an NPC
        val npc = NPC.npcs.values.firstOrNull { it.entities.contains(target) } ?: return

        // cancel the event
        event.isCancelled = true

        // check if this player has an active ClickNPCWithItemGoal
        val goal = ClickNPCWithItemGoal.activePlayers[player]

        // if we found a goal, and it passed the goals interact test, stop here
        if (goal != null && goal.interact(player, npc, player.inventory.itemInMainHand))
            return

        // call click
        npc.onRightClick(event)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        // cancel the event first
        event.isCancelled = true

        // get the message
        val message = "<${event.player.name}> ${event.message}"

        // send it to all players who are not in a dialogue
        Bukkit.getOnlinePlayers().forEach {
            if (!DialogueChain.occupiedList.contains(it))
                it.sendMessage(message)
        }
    }
}