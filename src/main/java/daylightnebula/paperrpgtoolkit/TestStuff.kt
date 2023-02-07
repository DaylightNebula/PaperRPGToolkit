package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.dialogue.DialogueChain
import daylightnebula.paperrpgtoolkit.dialogue.DialogueLink
import daylightnebula.paperrpgtoolkit.goals.impl.PressShiftGoal
import daylightnebula.paperrpgtoolkit.goals.impl.SelectNumberedOptionGoal
import daylightnebula.paperrpgtoolkit.items.TestSwordItem
import daylightnebula.paperrpgtoolkit.npc.TestNPC
import daylightnebula.paperrpgtoolkit.quests.TestQuestChain

object TestStuff {

    lateinit var testSword: TestSwordItem
    lateinit var testNPC: TestNPC
    lateinit var testQuestChain: TestQuestChain
    lateinit var testDialogue: DialogueChain

    fun init() {
        // create test custom item
        testSword = TestSwordItem()
        testNPC = TestNPC()
        testQuestChain = TestQuestChain()

        // create dialogue
        testDialogue = DialogueChain(
            arrayOf(
                DialogueLink(
                    testNPC,
                    "Hi.  My Name is Bob.",
                    PressShiftGoal(),
                    true
                ),
                DialogueLink(
                    testNPC,
                    "Can you help me find 10 apples?",
                    SelectNumberedOptionGoal(),
                    true,
                    arrayOf(
                        "Yes",
                        "No"
                    )
                ) { player, option ->
                    player.sendMessage("You selected option $option")
                }
            )
        ) { it.sendMessage("Finished test dialogue chain") }
    }
}