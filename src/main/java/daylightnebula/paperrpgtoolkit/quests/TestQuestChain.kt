package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.TestStuff
import daylightnebula.paperrpgtoolkit.item
import daylightnebula.paperrpgtoolkit.goals.impl.GetItemGoal
import org.bukkit.Material
import org.bukkit.entity.Player

class TestQuestChain: QuestChain(
    "testchain",
    "Test Quest Chain",
    "Bestest test quest chain"
) {
    override fun setupLinks(): Array<QuestLink> {
        return arrayOf(
            QuestLink(
                this,
                "Test Quest Get Custom Item",
                "Get the test sword.",
                GetItemGoal(TestStuff.testSword.itemStack, 1)
            ) { it.sendMessage("Finished the test quest get custom item") },
            QuestLink(
                this,
                "Test Quest Get MC Item",
                "Get 10 apples.",
                GetItemGoal(item(Material.APPLE), 10)
            ) { it.sendMessage("Finished the test quest get mc item") }
        )
    }

    override fun onQuestChainComplete(player: Player) {
        player.sendMessage("Finished test quest chain")
    }
}