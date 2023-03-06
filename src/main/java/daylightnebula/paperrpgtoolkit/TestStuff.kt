package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.dialogue.DialogueChain
import daylightnebula.paperrpgtoolkit.dialogue.DialogueLink
import daylightnebula.paperrpgtoolkit.entities.CustomMob
import daylightnebula.paperrpgtoolkit.entities.tasks.AttackNearbyPlayersTask
import daylightnebula.paperrpgtoolkit.entities.tasks.WanderNearPointTask
import daylightnebula.paperrpgtoolkit.goals.impl.*
import daylightnebula.paperrpgtoolkit.items.CustomItem
import daylightnebula.paperrpgtoolkit.npc.NPC
import daylightnebula.paperrpgtoolkit.quests.QuestChain
import daylightnebula.paperrpgtoolkit.quests.QuestLink
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.json.JSONObject
import java.io.File

object TestStuff {

    fun init() {
        // setup quest chain
        QuestChain(
            "bobsApples",
            "Bobs Apples",
            "Bob needs your help finding some apples.",
            arrayOf(
                QuestLink(
                    "Find Bobs Apples",
                    "Pickup 10 apples.",
                    GetItemGoal("apple", 10)
                ),
                QuestLink(
                    "Talk to Bob",
                    "Give Bob the 10 apples you just found.",
                    ClickNPCWithItemGoal("bob", "apple", 10, true),
                    onGoalComplete = { DialogueChain.startChainForPlayer("bobsApples", "bobFoundApples", it) }
                ),
                QuestLink(
                    "Talk to Bob",
                    "Talk to Bob",
                    CompleteDialogueGoal("bobsApples", "bobFoundApples")
                )
            )
        )
    }
}