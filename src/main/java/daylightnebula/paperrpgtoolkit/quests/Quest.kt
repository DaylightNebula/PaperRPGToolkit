package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.quests.goals.QuestGoal
import org.bukkit.entity.Player

class Quest(
    val chain: QuestChain,
    val name: String,
    val description: String,
    val goal: QuestGoal,
    val onQuestComplete: (player: Player) -> Unit
) {

    init {
        goal.quest = this
    }

    fun goalComplete(player: Player) {
        onQuestComplete(player)
    }
}