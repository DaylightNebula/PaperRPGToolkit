package daylightnebula.paperrpgtoolkit.npc

import daylightnebula.paperrpgtoolkit.quests.QuestChain
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpawnNPCCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        // make sure the sender is a player
        if (sender !is Player) return false
        if (!sender.isOp) return false

        // if arguments are null or none are given, send command arguments to the user and cancel
        if (args == null || (args.size != 1 && args.size != 4 && args.size != 6)) {
            sender.sendMessage("/spawnnpc <npc id> [<x> <y> <z>] [<pitch> <yaw>]")
            return true
        }

        // process location
        val location = sender.location.clone()
        if (args.size >= 4) {
            location.x = args[1].toDoubleOrNull() ?: return false
            location.y = args[2].toDoubleOrNull() ?: return false
            location.z = args[3].toDoubleOrNull() ?: return false
        }
        if (args.size >= 6) {
            location.pitch = args[4].toFloatOrNull() ?: return false
            location.yaw = args[5].toFloatOrNull() ?: return false
        }

        // try to get the NPC
        val npc = NPC.npcs[args.first()]

        // if no quest chain was found, error and cancel
        if (npc == null) {
            sender.sendMessage("Unknown NPC ${args.first()}, options are ${ NPC.npcs.keys.map { it } }")
            return false
        }

        // spawn the NPC
        npc.spawnAtLocation(location, true)

        // success
        return true
    }
}

class RemoveNearbyNPCCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        // make sure the sender is a player
        if (sender !is Player) return false
        if (!sender.isOp) return false

        // get kill radius
        val killRadius = if (args != null && args.isNotEmpty()) args.first().toFloatOrNull() else 1f
        if (killRadius == null) {
            sender.sendMessage("Invalid kill radius, must be a valid float")
            return true
        }

        // remove the entities
        NPC.removeNearLocation(sender.location, killRadius)

        // success
        return true
    }
}