package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.dialogue.DialogueChain
import daylightnebula.paperrpgtoolkit.dialogue.DialogueLink
import daylightnebula.paperrpgtoolkit.goals.impl.*
import daylightnebula.paperrpgtoolkit.items.CustomItem
import daylightnebula.paperrpgtoolkit.npc.NPC
import daylightnebula.paperrpgtoolkit.quests.QuestChain
import daylightnebula.paperrpgtoolkit.quests.QuestLink
import org.bukkit.Material
import org.bukkit.entity.EntityType

object TestStuff {

    private lateinit var funSword: CustomItem
    private lateinit var bob: NPC
    private lateinit var bobsApples: QuestChain
    private lateinit var bobAskForApples: DialogueChain
    private lateinit var bobNoApples: DialogueChain
    private lateinit var bobFoundApples: DialogueChain

    fun init() {
        // create test custom item
        funSword = CustomItem(
            "testsword",
            Material.IRON_SWORD,
            "Test Sword",
            "Bestest test sword",
            attackDamage = 3.0,
            attackSpeed = 1.5
        )

        // create NPC
        bob = NPC(
            "bob",
            "§dBob the builder",
            EntityType.VILLAGER,
            onPlayerInteract = { player ->
                bobAskForApples.startForPlayer(player)
            }
        )

        // bob yes/no apples dialogues
        bobNoApples = DialogueChain(
            arrayOf(
                DialogueLink(
                    bob,
                    "SMH",
                    PressShiftGoal(),
                    true
                )
            )
        )
        bobFoundApples = DialogueChain(
            arrayOf(
                DialogueLink(
                    bob,
                    "Thank you for the apples!",
                    PressShiftGoal(),
                    true
                )
            )
        )

        // setup quest chain
        bobsApples = QuestChain(
            "bobsapples",
            "Bobs Apples",
            "Bob needs your help finding some apples.",
            arrayOf(
                QuestLink(
                    "Find Bobs Apples",
                    "Pickup 10 apples.",
                    GetItemGoal(item(Material.APPLE), 10)
                ),
                QuestLink(
                    "Talk to Bob",
                    "Give Bob the 10 apples you just found.",
                    ClickNPCWithItemGoal(bob, item(Material.APPLE), 10, true),
                    onGoalComplete = { bobFoundApples.startForPlayer(it) }
                ),
                QuestLink(
                    "Talk to Bob",
                    "Talk to Bob",
                    CompleteDialogueGoal(bobFoundApples)
                )
            )
        )

        // create dialogue
        bobAskForApples = DialogueChain(
            arrayOf(
                DialogueLink(
                    bob,
                    "Hi.  My Name is Bob.",
                    PressShiftGoal(),
                    true
                ),
                DialogueLink(
                    bob,
                    "Can you help me find 10 apples?",
                    SelectNumberedOptionGoal(0, 1),
                    true,
                    arrayOf(
                        "Yes",
                        "No"
                    )
                ) { player, option ->
                    if (option == 0)
                        bobsApples.startForPlayer(player)
                    else
                        bobNoApples.startForPlayer(player)
                }
            )
        )
    }
}