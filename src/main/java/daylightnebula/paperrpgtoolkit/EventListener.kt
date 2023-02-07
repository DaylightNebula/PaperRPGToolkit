package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.items.CustomItem
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

class EventListener : Listener {
    @EventHandler
    public fun onPlayerInteract(event: PlayerInteractEvent) {
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
    fun onInventoryInteract(event: EntityPickupItemEvent) {
    }
}