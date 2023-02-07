package daylightnebula.paperrpgtoolkit.quests

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StartQuestChainCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        // make sure the sender is a player
        if (sender !is Player) return false
        if (!sender.isOp) return false

        // if arguments are null or none are given, send command arguments to the user and cancel
        if (args == null || args.size < 1) {
            sender.sendMessage("/startquestchain <quest chain id>")
            return true
        }

        // try to get a quest chain
        val questChain = QuestChain.questChains[args.first()]

        // if no quest chain was found, error and cancel
        if (questChain == null) {
            sender.sendMessage("Unknown quest chain id ${args.first()}, options are ${ QuestChain.questChains.keys.map { it } }")
            return false
        }

        // start quest chain
        questChain.startForPlayer(sender)

        return true
    }
}

class EndQuestChainCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!sender.isOp) return false

        // try to get a quest chain of the sender
        val questChain = QuestChain.questChains.values.firstOrNull { it.linkTracker.containsKey(sender) }

        // if no quest chain was found, error and cancel
        if (questChain == null) {
            sender.sendMessage("Could not find your active quest chain, options are ${ QuestChain.questChains.keys.map { it } }")
            return true
        }

        // stop quest chain
        questChain.endForPlayer(sender, args != null && args.size > 1 && args.first() == "yes")
        return true
    }
}

class AdvanceQuestChainCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!sender.isOp) return false

        // try to get a quest chain of the sender
        val questChain = QuestChain.questChains.values.firstOrNull { it.linkTracker.containsKey(sender) }

        // if no quest chain was found, error and cancel
        if (questChain == null) {
            sender.sendMessage("Could not find your active quest chain, options are ${ QuestChain.questChains.keys.map { it } }")
            return true
        }

        // advance quest chain
        questChain.proceedToNextQuest(sender)
        return true
    }
}