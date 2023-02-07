package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.GoalInterface
import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.entity.Player

class QuestLink(
    private val chain: QuestChain,
    val name: String,
    val description: String,
    val goal: Goal,
    val onQuestComplete: (player: Player) -> Unit
): GoalInterface {

    init {
        goal.init(this)
    }

    override fun doesPlayerHasGoal(player: Player, goal: Goal): Boolean {
        return QuestChain.curQuest[player] == chain.id && chain.quests[chain.questState[player] ?: return false] == this
    }

    override fun goalComplete(player: Player, goal: Goal) {
        onQuestComplete(player)
        chain.proceedToNextQuest(player)
    }

    override fun descriptionChanged(player: Player, goal: Goal) {
        chain.updateSidebarForPlayer(player)
    }
}