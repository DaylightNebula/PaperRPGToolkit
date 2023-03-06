package daylightnebula.paperrpgtoolkit.actions

import org.bukkit.entity.Player
import org.json.JSONObject

class ActionHotbarSplit(
    val actions: Array<Action>
): Action() {
    constructor(json: JSONObject): this(
        json.getJSONArray("actions")
            .map {
                decode(it as JSONObject)
                    ?: throw IllegalArgumentException("Could not load action from ${it.toString(1)}")
            }.toTypedArray()
    )

    override fun run(player: Player) {
        val slot = player.inventory.heldItemSlot
        if (actions.indices.contains(slot))
            actions[slot].run(player)
    }
}