package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.TestStuff
import daylightnebula.paperrpgtoolkit.item
import daylightnebula.paperrpgtoolkit.quests.goals.BlankQuestGoal
import daylightnebula.paperrpgtoolkit.quests.goals.GetItemGoal
import daylightnebula.paperrpgtoolkit.quests.goals.GotoLocationGoal
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class TestQuestChain: QuestChain(
    "testchain",
    "Test Quest Chain",
    "Bestest test quest chain"
) {
    override fun setupQuests(): Array<Quest> {
        return arrayOf(
            Quest(
                this,
                "Test Quest Location",
                "Go to 0/122/52",
                GotoLocationGoal(Vector(0.5, 122.0, 52.5), 1f)
            ) { it.sendMessage("Finished the test quest location") },
            Quest(
                this,
                "Test Quest Get Custom Item",
                "Get the test sword.",
                GetItemGoal(TestStuff.testSword.itemStack, 1)
            ) { it.sendMessage("Finished the test quest get custom item") },
            Quest(
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