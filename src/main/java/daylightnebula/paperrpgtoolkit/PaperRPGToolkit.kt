package daylightnebula.paperrpgtoolkit

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PaperRPGToolkit : JavaPlugin() {
    override fun onEnable() {
        Bukkit.broadcastMessage("Start")
    }
    override fun onDisable() {
        Bukkit.broadcastMessage("Stop")
    }
}