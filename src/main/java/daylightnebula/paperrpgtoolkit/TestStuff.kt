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

    private lateinit var funSword: CustomItem
    private lateinit var bob: NPC
    private lateinit var bobsApples: QuestChain
    private lateinit var bobAskForApples: DialogueChain
    private lateinit var bobNoApples: DialogueChain
    private lateinit var bobFoundApples: DialogueChain
    private lateinit var darkSkeleton: CustomMob

    fun init() {
        val world = Bukkit.getWorlds().first()

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
                DialogueChain.startChainForPlayer("bobAskForApples", player)
            }
        )

        // create custom mob
        //darkSkeleton = CustomMob("darkskeleton", JSONObject(File(PaperRPGToolkit.plugin.dataFolder, "mobs/darkskeleton.json").readText()))
//        darkSkeleton = CustomMob(
//            id ="darkskeleton",
//            displayName = "§0Dark Skeleton",
//            supertype = EntityType.WITHER_SKELETON,
//            maxHealth = 40.0,
//            tasks = arrayOf(
//                WanderNearPointTask(
//                    //Location(world, -3009.5, 70.26, 1282.5),
//                    wanderRange = 10f,
//                    minTicksBetweenMove = 30,
//                    maxTicksBetweenMove = 100
//                ),
//                AttackNearbyPlayersTask(
//                    detectRange = 5f
//                )
//            )
//        )

        // bob yes/no apples dialogues
        bobNoApples = DialogueChain(
            "bobNoApples",
            arrayOf(
                DialogueLink(
                    "bob",
                    "SMH",
                    PressShiftGoal(),
                    true
                )
            )
        )
        bobFoundApples = DialogueChain(
            "bobFoundApples",
            arrayOf(
                DialogueLink(
                    "bob",
                    "Thank you for the apples!",
                    PressShiftGoal(),
                    true
                )
            )
        )

        // setup quest chain
        bobsApples = QuestChain(
            "bobsApples",
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
                    ClickNPCWithItemGoal("bob", item(Material.APPLE), 10, true),
                    onGoalComplete = { DialogueChain.startChainForPlayer("bobFoundApples", it) }
                ),
                QuestLink(
                    "Talk to Bob",
                    "Talk to Bob",
                    CompleteDialogueGoal("bobFoundApples")
                )
            )
        )

        // create dialogue
        bobAskForApples = DialogueChain(
            "bobAskForApples",
            arrayOf(
                DialogueLink(
                    "bob",
                    "Hi.  My Name is Bob.",
                    PressShiftGoal(),
                    true
                ),
                DialogueLink(
                    "bob",
                    "Can you help me find 10 apples?",
                    SelectNumberedOptionGoal(0, 1),
                    true,
                    arrayOf(
                        "Yes",
                        "No"
                    )
                )
            ),
            onComplete =  { player ->
                if (player.inventory.heldItemSlot == 0)
                    QuestChain.startForPlayer("bobsApples", player)
                else
                    DialogueChain.startChainForPlayer("bobNoApples", player)
            }
        )
    }
}