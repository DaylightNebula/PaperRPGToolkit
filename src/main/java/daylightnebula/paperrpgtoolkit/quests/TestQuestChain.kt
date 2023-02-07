package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.quests.goals.BlankQuestGoal
import org.bukkit.entity.Player

class TestQuestChain: QuestChain(
    "Test Quest Chain",
    "Bestest test quest chain"
) {
    override fun setupQuests(): Array<Quest> {
        return arrayOf(
            Quest(this, "Test Quest A", "First Test Quest.  Try to find a test sword to complete this quest.", BlankQuestGoal()) { it.sendMessage("Finished the first test quest") },
            Quest(this, "Test Quest B", "Second Test Quest", BlankQuestGoal()) { it.sendMessage("Finished the second test quest") }
        )
    }

    override fun onQuestChainComplete(player: Player) {
        player.sendMessage("Finished test quest chain")
    }
}