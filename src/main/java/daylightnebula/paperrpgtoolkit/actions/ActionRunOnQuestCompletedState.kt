package daylightnebula.paperrpgtoolkit.actions

import daylightnebula.paperrpgtoolkit.quests.QuestChain
import org.bukkit.entity.Player
import org.json.JSONObject

class ActionRunOnQuestCompletedState(val questID: String, val prevCompleted: Boolean, val action: Action?): Action() {
    constructor(json: JSONObject): this(
        json.optString("quest", ""),
        json.optBoolean("completed", false),
        decode(json.optJSONObject("action"))
    )

    override fun run(player: Player) {
        val json = QuestChain.getJsonForPlayer(player)
        if (json.has("completed") && json.getJSONArray("completed").contains(questID) == prevCompleted)
            action?.run(player)
    }
}