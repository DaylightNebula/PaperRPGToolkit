package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.dialogue.DialogueChain
import daylightnebula.paperrpgtoolkit.entities.CustomMob
import daylightnebula.paperrpgtoolkit.entities.RemoveNearbyMobsCommand
import daylightnebula.paperrpgtoolkit.entities.SpawnMobCommand
import daylightnebula.paperrpgtoolkit.items.CustomItem
import daylightnebula.paperrpgtoolkit.items.CustomItemCommand
import daylightnebula.paperrpgtoolkit.npc.NPC
import daylightnebula.paperrpgtoolkit.npc.RemoveNearbyNPCCommand
import daylightnebula.paperrpgtoolkit.npc.SpawnNPCCommand
import daylightnebula.paperrpgtoolkit.quests.AdvanceQuestChainCommand
import daylightnebula.paperrpgtoolkit.quests.EndQuestChainCommand
import daylightnebula.paperrpgtoolkit.quests.QuestChain
import daylightnebula.paperrpgtoolkit.quests.StartQuestChainCommand
import daylightnebula.paperrpgtoolkit.spawner.CreateMobSpawner
import daylightnebula.paperrpgtoolkit.spawner.MobSpawner
import daylightnebula.paperrpgtoolkit.spawner.RemoveNearbySpawners
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PaperRPGToolkit : JavaPlugin() {

    companion object {
        const val testsEnabled = true
        lateinit var plugin: JavaPlugin
        lateinit var customItemReferenceIDKey: NamespacedKey
    }

    override fun onEnable() {
        // save plugin
        plugin = this

        // set up the name spaced keys that we need for storing information in minecraft objects
        customItemReferenceIDKey = NamespacedKey(this, "customItemReferenceID")

        // register events
        Bukkit.getPluginManager().registerEvents(EventListener(), this)

        // commands
        registerCommands()

        // load waiting json
        CustomMob.loadJSONFromFolder(File(dataFolder, "mobs"))
        CustomItem.loadJSONFromFolder(File(dataFolder, "items"))
        DialogueChain.loadJSONFromFolder(File(dataFolder, "dialogue"))
        NPC.loadJsonFromFolder(File(dataFolder, "npcs"))
        QuestChain.loadJSONFromFolder(File(dataFolder, "quests"))

        // start the update loops
        DialogueChain.startUpdateLoop()
        CustomMob.startUpdateLoop()
        MobSpawner.startUpdateLoop()

        // if tests are enabled, init them
        if (testsEnabled) TestStuff.init()

        // make sure each world is ready for all of our stuff
        Bukkit.getWorlds().forEach {
            // make sure game mode is at least easy
            if (it.difficulty == Difficulty.PEACEFUL)
                it.difficulty = Difficulty.EASY

            // disable natural mob spawning
            it.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        }

        // after 1 tick, finalize json loading
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            CustomMob.loadRemainingJSON()
            CustomItem.loadRemainingJSON()
            DialogueChain.loadWaitingJson()
            NPC.loadWaitingJson()
            QuestChain.loadWaitingJson()
            MobSpawner.finalizeInitialization()
        }, 1L)
    }

    private fun registerCommands() {
        this.getCommand("getcustomitem")?.setExecutor(CustomItemCommand())
        this.getCommand("startquestchain")?.setExecutor(StartQuestChainCommand())
        this.getCommand("endquestchain")?.setExecutor(EndQuestChainCommand())
        this.getCommand("advancequestchain")?.setExecutor(AdvanceQuestChainCommand())
        this.getCommand("spawnnpc")?.setExecutor(SpawnNPCCommand())
        this.getCommand("removenearbynpcs")?.setExecutor(RemoveNearbyNPCCommand())
        this.getCommand("spawnmob")?.setExecutor(SpawnMobCommand())
        this.getCommand("removenearbymobs")?.setExecutor(RemoveNearbyMobsCommand())
        this.getCommand("createmobspawner")?.setExecutor(CreateMobSpawner())
        this.getCommand("removenearbyspawners")?.setExecutor(RemoveNearbySpawners())
    }

    override fun onDisable() {
        // remove any active custom entities
        NPC.removeAllNPCs(false)
        CustomMob.disable()
        QuestChain.disable()
    }
}