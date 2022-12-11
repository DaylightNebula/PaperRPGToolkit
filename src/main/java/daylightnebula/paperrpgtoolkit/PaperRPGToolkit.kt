package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.items.CustomItem
import daylightnebula.paperrpgtoolkit.items.CustomItemCommand
import daylightnebula.paperrpgtoolkit.items.TestSwordItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class PaperRPGToolkit : JavaPlugin() {

    companion object {
        lateinit var customItemReferenceIDKey: NamespacedKey
    }

    override fun onEnable() {
        // set up the name spaced keys that we need for storing information in minecraft objects
        customItemReferenceIDKey = NamespacedKey(this, "customItemReferenceID")

        Bukkit.getPluginManager().registerEvents(EventListener(), this)

        registerCommands()

        // create test custom item
        TestSwordItem()
    }

    fun registerCommands() {
        this.getCommand("getcustomitem")?.setExecutor(CustomItemCommand())
    }

    override fun onDisable() {
    }
}