package daylightnebula.paperrpgtoolkit.actions

import daylightnebula.paperrpgtoolkit.items.CustomItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.json.JSONObject
import java.lang.IllegalArgumentException

class ActionGiveItem(private val itemID: String, private val amount: Int = 1): Action() {
    constructor(json: JSONObject): this(json.getString("id"), json.optInt("amount", 1))
    override fun run(player: Player) {
        val cItem = CustomItem.items[itemID]
        if (cItem != null) cItem.giveToPlayer(player, amount)
        else ItemStack(
            Material.values().firstOrNull { it.name.equals(itemID, ignoreCase = true) }
                ?: throw IllegalArgumentException("$itemID is not a custom item or material")
        ).apply { this.amount = amount }
    }
}