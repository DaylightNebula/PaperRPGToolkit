package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.items.TestSwordItem
import daylightnebula.paperrpgtoolkit.npc.TestNPC
import daylightnebula.paperrpgtoolkit.quests.TestQuestChain

object TestStuff {

    lateinit var testSword: TestSwordItem

    fun init() {
        // create test custom item
        testSword = TestSwordItem()
        TestQuestChain()
        TestNPC()
    }
}