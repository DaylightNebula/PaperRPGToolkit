package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.buildScoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.util.ChatPaginator
import java.lang.NullPointerException

abstract class QuestChain(
    val name: String,
    val description: String
) {
    companion object {
        val questChains = hashMapOf<String, QuestChain>()
    }

    val quests = setupQuests()

    // create an id by remove spacing from the given name
    val id = name.replace(" ", "")

    // tracks which
    val questState = hashMapOf<Player, Int>()

    init {
        questChains[id] = this
    }

    fun updateSidebarForPlayer(player: Player) {
        // if the player is no longer on the quest chain, set players scoreboard to a blank scoreboard and cancel
        if (!questState.containsKey(player)) {
            player.scoreboard = Bukkit.getScoreboardManager().newScoreboard
            return
        }

        // get current quest
        val quest = quests[questState[player]!!]

        // paginate the quest description so that it fits in the sidebar
        val descriptionLines = ChatPaginator.wordWrap("§7${quest.description}", 16)

        // render the sidebar
        val lines = listOf(
            "§6§l${quest.name}",
            *descriptionLines
        )

        // send to player
        player.scoreboard = buildScoreboard("§a§l$name", lines)
    }

    fun startForPlayer(player: Player) {
        // add the given player to the quest state tracking map
        questState[player] = 0
        updateSidebarForPlayer(player)
    }

    fun proceedToNextQuest(player: Player) {
        // make sure the given player has started this quest
        if (!questState.containsKey(player)) return //throw NullPointerException("Attempting to proceed in quest for a player that has not started quest chain $name")

        // get and then set the new quest number for the given player
        val newCount = questState[player]!! + 1
        questState[player] = newCount

        // if the new count has exceeded the quests size, the player has complete this quest chain so call the end function
        if (newCount >= quests.size)
            endForPlayer(player, true)
        updateSidebarForPlayer(player)
    }

    fun endForPlayer(player: Player, reward: Boolean) {
        // remove the given player from the quest state tracking map
        questState.remove(player)

        // if the player should be rewarded, call on complete
        if (reward)
            onQuestChainComplete(player)
        updateSidebarForPlayer(player)
    }

    abstract fun setupQuests(): Array<Quest>
    abstract fun onQuestChainComplete(player: Player)
}