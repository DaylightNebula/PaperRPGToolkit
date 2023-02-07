package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.TestStuff
import daylightnebula.paperrpgtoolkit.item
import daylightnebula.paperrpgtoolkit.quests.goals.BlankQuestGoal
import daylightnebula.paperrpgtoolkit.quests.goals.GetItemGoal
import org.bukkit.Material
import org.bukkit.entity.Player

class TestQuestChain: QuestChain(
    "testchain",
    "Test Quest Chain",
    "Bestest test quest chain"
) {
    override fun setupQuests(): Array<Quest> {
        return arrayOf(
            Quest(this, "Test Quest A", "First Test Quest.  Get the test sword.", GetItemGoal(TestStuff.testSword.itemStack, 1)) { it.sendMessage("Finished the first test quest") },
            Quest(this, "Test Quest B", "Second Test Quest. Get 10 apples.", GetItemGoal(item(Material.APPLE), 10)) { it.sendMessage("Finished the second test quest") }
        )
    }

    override fun onQuestChainComplete(player: Player) {
        player.sendMessage("Finished test quest chain")
    }
}