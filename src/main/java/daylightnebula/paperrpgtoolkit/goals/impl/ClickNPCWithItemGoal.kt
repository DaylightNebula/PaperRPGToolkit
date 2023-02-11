package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.goals.Goal
import daylightnebula.paperrpgtoolkit.items.CustomItem
import daylightnebula.paperrpgtoolkit.npc.NPC
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ClickNPCWithItemGoal(
    private val targetNPCID: String,
    private val targetItem: String,
    private val amount: Int,
    private val shouldRemove: Boolean
): Listener, Goal() {
    //private val isCustom = targetItem.itemMeta.persistentDataContainer.has(PaperRPGToolkit.customItemReferenceIDKey)
    //private val itemName = if (isCustom) targetItem.itemMeta.persistentDataContainer.get(PaperRPGToolkit.customItemReferenceIDKey, PersistentDataType.STRING) else ""
    private val customItem = CustomItem.items.values.firstOrNull { it.id.equals(targetItem, true) }
    private val material = Material.values().firstOrNull { it.name.equals(targetItem, true) }

    companion object {
        val activePlayers = hashMapOf<Player, ClickNPCWithItemGoal>()
    }

    fun interact(player: Player, npc: NPC, item: ItemStack): Boolean { // return true if that is our item and npc
        // make sure npc is the target npc
        if (npc.id != targetNPCID) return false

        // make sure player has quest
        if (!activePlayers.containsKey(player)) return false

        // check if the item is valid
        val isItemValid =
            // if item is custom, check id to check if valid
            if (customItem != null)
                item.itemMeta.persistentDataContainer.has(PaperRPGToolkit.customItemReferenceIDKey) &&
                        item.itemMeta.persistentDataContainer.get(PaperRPGToolkit.customItemReferenceIDKey, PersistentDataType.STRING) == customItem.name
            // otherwise, check if material and name are equal
            else if (material != null)
                item.type == material
            else
                false

        val isCountValid = item.amount >= amount

        // if the could and item is valid, stop the goal and return true, otherwise, return false
        return if (isItemValid && isCountValid) {
            // remove items if necessary
            if (shouldRemove) {
                if (item.amount > amount) item.amount -= amount
                else player.inventory.remove(item)
            }

            // finish the goal and return true
            finishGoal(player)
            true
        } else
            false
    }

    override fun startForPlayer(player: Player) {
        activePlayers[player] = this
    }

    override fun stopForPlayer(player: Player) {
        if (activePlayers[player] == this)
            activePlayers.remove(player)
    }

    override fun forceComplete(player: Player) {
        finishGoal(player)
    }

    override fun getDescriptionText(player: Player): String {
        return ""
    }
}