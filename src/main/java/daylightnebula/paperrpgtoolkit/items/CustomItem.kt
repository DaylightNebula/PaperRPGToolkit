package daylightnebula.paperrpgtoolkit.items

import daylightnebula.paperrpgtoolkit.addItemWithEvent
import daylightnebula.paperrpgtoolkit.item
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class CustomItem(
    id: String,
    srcMaterial: Material,
    name: String,
    description: String,
    attackDamage: Double = -1.0,
    attackSpeed: Double = -1.0,
    knockback: Double = -1.0,

    // callbacks
    val leftClick: (player: Player, target: Entity?) -> Unit = { _, _ -> },
    val rightClick: (player: Player, target: Entity?) -> Unit = { _, _ -> }
) {

    companion object {
        val items = hashMapOf<String, CustomItem>() // every item will be stored here, each custom item instance is to be treated as a SINGLETON
    }

    val itemStack = item(srcMaterial, name = name, description = description, customItemReferenceID = id, attackDamage = attackDamage, attackSpeed = attackSpeed, knockback = knockback)

    init {
        // save item
        items[id] = this
    }

    private var lastClickTime = 0
    private val clickMinTimeMS = 500
    fun click(interactEvent: PlayerInteractEvent) {
        // if minimum spacing between item clicks has not been met, cancel
        if (System.currentTimeMillis() - lastClickTime < clickMinTimeMS) return

        // call click based on left or right click interact event TODO fix
//        if (interactEvent.action == Action.LEFT_CLICK_AIR || interactEvent.action == Action.LEFT_CLICK_BLOCK)
//            leftClick()
//        else if (interactEvent.action == Action.RIGHT_CLICK_AIR || interactEvent.action == Action.RIGHT_CLICK_BLOCK)
//            rightClick()
    }

    fun giveToPlayer(player: Player, amount: Int) {
        // TODO make better
        player.inventory.addItemWithEvent(itemStack)
    }
}