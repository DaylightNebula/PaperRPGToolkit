package daylightnebula.paperrpgtoolkit.actions

import daylightnebula.paperrpgtoolkit.dialogue.DialogueChain
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.json.JSONObject

class ActionStartDialogue(val id: String, val subid: String): Action() {
    constructor(json: JSONObject): this(json.getString("id"), json.getString("subid"))
    override fun run(player: Player) {
        DialogueChain.startChainForPlayer(id, subid, player)
    }
}