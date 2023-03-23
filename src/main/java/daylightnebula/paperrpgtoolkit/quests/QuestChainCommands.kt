package daylightnebula.paperrpgtoolkit.quests

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StartQuestChainCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        // make sure the sender is a player
        if (sender !is Player) return false
        if (!sender.hasPermission("rpgtoolkit.startquestchain")) return false

        // if arguments are null or none are given, send command arguments to the user and cancel
        if (args == null || args.isEmpty()) {
            sender.sendMessage("/startquestchain <quest chain id>")
            return true
        }
        // if no quest chain was found, error and cancel
        if (!QuestChain.startQuestForPlayer(sender, args.first())) {
            sender.sendMessage("Unknown quest chain id ${args.first()}")
            return false
        }

        return true
    }
}

class EndQuestChainCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!sender.hasPermission("rpgtoolkit.endquestchain")) return false

        // if no quest chain was found, error and cancel
        if (!QuestChain.stopQuestForPlayer(sender, args != null && args.size > 1 && args.first() == "yes")) {
            sender.sendMessage("Could not find your active quest chain")
            return true
        }

        return true
    }
}

class AdvanceQuestChainCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!sender.hasPermission("rpgtoolkit.advancequestchain")) return false

        // if no quest chain was found, error and cancel
        if (!QuestChain.advanceQuestForPlayer(sender)) {
            sender.sendMessage("Could not find your active quest chain")
            return true
        }

        return true
    }
}