package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.goals.GoalInterface
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

    fun startForPlayer(player: Player) { goal.startForPlayer(player) }
    fun stopForPlayer(player: Player) { goal.stopForPlayer(player) }

    override fun goalComplete(player: Player, goal: Goal) {
        onQuestComplete(player)
        chain.proceedToNextQuest(player)
    }

    override fun descriptionChanged(player: Player, goal: Goal) {
        chain.updateSidebarForPlayer(player)
    }
}