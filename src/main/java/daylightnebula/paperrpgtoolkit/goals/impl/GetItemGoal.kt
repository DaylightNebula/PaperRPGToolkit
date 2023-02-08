package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.addItemWithEvent
import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class GetItemGoal(private val targetItem: ItemStack, private val minCount: Int): Goal(), Listener {
    private val isCustom = targetItem.itemMeta.persistentDataContainer.has(PaperRPGToolkit.customItemReferenceIDKey)
    private val itemName = if (isCustom) targetItem.itemMeta.persistentDataContainer.get(PaperRPGToolkit.customItemReferenceIDKey, PersistentDataType.STRING) else ""

    private val countMap = hashMapOf<UUID, Int>()

    init {
        Bukkit.getPluginManager().registerEvents(this, PaperRPGToolkit.plugin)
    }

    @EventHandler
    fun onInventoryPickupItem(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        val item = event.item.itemStack
        val numAdded = event.item.itemStack.amount

        // make sure player has quest
        if (!countMap.containsKey(player.uniqueId)) return

        val valid =
            // if item is custom, check id to check if valid
            if (isCustom)
                item.itemMeta.persistentDataContainer.has(PaperRPGToolkit.customItemReferenceIDKey) && item.itemMeta.persistentDataContainer.get(PaperRPGToolkit.customItemReferenceIDKey, PersistentDataType.STRING) == itemName
            // otherwise, check if material and name are equal
            else
                item.type == targetItem.type

        println("Received item pickup $valid")

        // if item is valid, update count and check if complete
        if (valid) {
            // get new count for this player
            val newCount = (countMap[player.uniqueId] ?: 0) + numAdded

            // if new count exceeds the count, remove the player from the count map and finish the quest
            if (newCount >= minCount)
                finishGoal(player)
            // otherwise, update tracker
            else {
                // update tracker
                countMap[player.uniqueId] = newCount
                descriptionChanged(player)
            }
        }
    }

    override fun startForPlayer(player: Player) { countMap[player.uniqueId] = 0 }
    override fun stopForPlayer(player: Player) { countMap.remove(player.uniqueId) }

    override fun forceComplete(player: Player) {
        player.inventory.addItemWithEvent(targetItem.clone().apply { amount = minCount })
    }

    override fun getDescriptionText(player: Player): String {
        val curCount = countMap[player.uniqueId] ?: return ""
        return "$curCount/$minCount"
    }
}