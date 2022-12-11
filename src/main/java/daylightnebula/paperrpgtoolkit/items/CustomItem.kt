package daylightnebula.paperrpgtoolkit.items

import daylightnebula.paperrpgtoolkit.item
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

abstract class CustomItem(
    val srcMaterial: Material,
    val name: String,
    val description: String,
    val maxStackSize: Int,
    val attackDamage: Double = -1.0,
    val attackSpeed: Double = -1.0,
    val knockback: Double = -1.0,
) {

    companion object {
        val items = hashMapOf<String, CustomItem>() // every item will be stored here, each custom item instance is to be treated as a SINGLETON
    }

    val id = name.replace(" ", "")
    val itemStack = item(srcMaterial, name = name, description = description, customItemReferenceID = id, attackDamage = attackDamage, attackSpeed = attackSpeed, knockback = knockback)

    init {
        // save item
        items[id] = this
    }

    var lastClickTime = 0
    val clickMinTimeMS = 500
    fun click(interactEvent: PlayerInteractEvent) {
        // if minimum spacing between item clicks has not been met, cancel
        if (System.currentTimeMillis() - lastClickTime < clickMinTimeMS) return

        // call click based on left or right click interact event
        if (interactEvent.action == Action.LEFT_CLICK_AIR || interactEvent.action == Action.LEFT_CLICK_BLOCK)
            leftClick()
        else if (interactEvent.action == Action.RIGHT_CLICK_AIR || interactEvent.action == Action.RIGHT_CLICK_BLOCK)
            rightClick()
    }

    abstract fun rightClick()
    abstract fun leftClick()

    fun giveToPlayer(player: Player, amount: Int) {
        // TODO make better
        player.inventory.addItem(itemStack)
    }
}