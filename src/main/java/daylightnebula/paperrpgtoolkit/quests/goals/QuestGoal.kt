package daylightnebula.paperrpgtoolkit.quests.goals

import daylightnebula.paperrpgtoolkit.quests.Quest
import daylightnebula.paperrpgtoolkit.quests.QuestChain
import org.bukkit.entity.Player

abstract class QuestGoal(
) {
    var quest: Quest? = null

    fun finishQuest(player: Player) {
        quest?.goalComplete(player)
    }

    fun playerHasQuest(player: Player): Boolean {
        val quest = quest ?: return false
        return QuestChain.curQuest[player] == quest.chain.id && quest.chain.quests[quest.chain.questState[player] ?: return false] == quest
    }

    abstract fun forceComplete(player: Player)
}