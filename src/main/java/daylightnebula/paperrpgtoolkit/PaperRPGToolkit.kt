package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.items.CustomItem
import daylightnebula.paperrpgtoolkit.items.CustomItemCommand
import daylightnebula.paperrpgtoolkit.items.TestSwordItem
import daylightnebula.paperrpgtoolkit.npc.NPC
import daylightnebula.paperrpgtoolkit.npc.RemoveNearbyNPCCommand
import daylightnebula.paperrpgtoolkit.npc.SpawnNPCCommand
import daylightnebula.paperrpgtoolkit.quests.AdvanceQuestChainCommand
import daylightnebula.paperrpgtoolkit.quests.EndQuestChainCommand
import daylightnebula.paperrpgtoolkit.quests.StartQuestChainCommand
import daylightnebula.paperrpgtoolkit.quests.TestQuestChain
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

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

        // if tests are enabled, init them
        if (testsEnabled) TestStuff.init()
    }

    fun registerCommands() {
        this.getCommand("getcustomitem")?.setExecutor(CustomItemCommand())
        this.getCommand("startquestchain")?.setExecutor(StartQuestChainCommand())
        this.getCommand("endquestchain")?.setExecutor(EndQuestChainCommand())
        this.getCommand("advancequestchain")?.setExecutor(AdvanceQuestChainCommand())
        this.getCommand("spawnnpc")?.setExecutor(SpawnNPCCommand())
        this.getCommand("removenearbynpcs")?.setExecutor(RemoveNearbyNPCCommand())
    }

    override fun onDisable() {
        NPC.removeAllNPCs()
    }
}