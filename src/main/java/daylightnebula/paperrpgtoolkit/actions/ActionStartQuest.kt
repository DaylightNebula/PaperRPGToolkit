package daylightnebula.paperrpgtoolkit.actions

import daylightnebula.paperrpgtoolkit.quests.QuestChain
import org.bukkit.entity.Player
import org.json.JSONObject

class ActionStartQuest(private val chainID: String): Action() {
    constructor(json: JSONObject): this(json.getString("quest"))
    override fun run(player: Player) { QuestChain.startForPlayer(chainID, player) }
}