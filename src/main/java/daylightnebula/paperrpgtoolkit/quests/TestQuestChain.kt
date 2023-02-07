package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.TestStuff
import daylightnebula.paperrpgtoolkit.goals.impl.ClickNPCWithItemGoal
import daylightnebula.paperrpgtoolkit.item
import daylightnebula.paperrpgtoolkit.goals.impl.GetItemGoal
import daylightnebula.paperrpgtoolkit.goals.impl.PressShiftGoal
import daylightnebula.paperrpgtoolkit.goals.impl.SelectNumberedOptionGoal
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
                "Test Give Item To Bob",
                "Give bob 5 apples",
                ClickNPCWithItemGoal(TestStuff.testNPC, item(Material.APPLE), 5, true)
            ) { it.sendMessage("Finished the test quest give item to bob") },
            QuestLink(
                this,
                "Test Quest Press Number",
                "Press a number 1-8",
                SelectNumberedOptionGoal()
            ) { it.sendMessage("Finished the test quest press number with number ${it.inventory.heldItemSlot + 1}") },
            QuestLink(
                this,
                "Test Quest Press Shift",
                "Press shift",
                PressShiftGoal()
            ){ it.sendMessage("Finished the test quest press shift") },
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