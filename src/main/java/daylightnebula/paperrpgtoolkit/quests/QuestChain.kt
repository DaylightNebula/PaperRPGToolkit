package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.actions.Action
import daylightnebula.paperrpgtoolkit.buildScoreboard
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.ChatPaginator
import org.json.JSONObject
import java.io.File

class QuestChain(
    val id: String,
    val name: String,
    val description: String,
    val links: Array<QuestLink>,

    // callbacks
    val onComplete: Action?,
) {
    companion object {
        val questChains = hashMapOf<String, QuestChain>()
        val curQuest = hashMapOf<Player, String>()
        val waitingJson = hashMapOf<String, JSONObject>()

        fun loadJSONFromFolder(folder: File) {
            folder.listFiles()?.forEach { file ->
                if (file.extension == "json")
                    waitingJson[file.nameWithoutExtension] = JSONObject(file.readText())
                else if (file.isDirectory)
                    loadJSONFromFolder(file)
            }
        }

        fun loadWaitingJson() {
            waitingJson.forEach { (id, json) ->
                QuestChain(id, json)
            }
        }

        fun startForPlayer(id: String, player: Player) {
            questChains[id]?.startForPlayer(player)
        }
    }

    constructor(id: String, json: JSONObject): this(
        id,
        json.optString("name", ""),
        json.optString("description", ""),
        if (json.has("links"))
            json.getJSONArray("links")
                .map { QuestLink(it as JSONObject) }
                .toTypedArray()
        else emptyArray<QuestLink>(),
        Action.decode(json.optJSONObject("complete_action"))
    )

    // tracks which
    val linkTracker = hashMapOf<Player, Int>()

    init {
        questChains[id] = this
        links.forEach { it.init(this) }
    }

    fun updateSidebarForPlayer(player: Player) {
        // if the player is no longer on the quest chain, set players scoreboard to a blank scoreboard and cancel
        if (!linkTracker.containsKey(player)) {
            player.scoreboard = Bukkit.getScoreboardManager().newScoreboard
            return
        }

        // get current quest
        val quest = links[linkTracker[player]!!]

        // paginate the quest description so that it fits in the sidebar
        val descriptionLines = ChatPaginator.wordWrap("§f${quest.description}", if (quest.name.length > 16) quest.name.length else 16)

        // render the sidebar
        val lines = listOf(
            "§6§l${quest.name}",
            *descriptionLines,
            quest.goal.getDescriptionText(player)
        )

        // send to player
        player.scoreboard = buildScoreboard("§a§l$name", lines)
    }

    fun startForPlayer(player: Player) {
        // add the given player to the quest state tracking map
        links.first().startForPlayer(player)
        linkTracker[player] = 0
        curQuest[player] = id
        updateSidebarForPlayer(player)

        // notify player that the quest started
        player.sendTitle("" , "§6Started quest: §a§l$name", 10, 50, 10)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }

    fun proceedToNextQuest(player: Player) {
        // make sure the given player has started this quest
        if (!linkTracker.containsKey(player)) return //throw NullPointerException("Attempting to proceed in quest for a player that has not started quest chain $name")

        // stop current quest
        links[linkTracker[player]!!].stopForPlayer(player)

        // get and then set the new quest number for the given player
        val newCount = linkTracker[player]!! + 1
        linkTracker[player] = newCount

        // if new count is in range of the links array, start next quest
        if (newCount < links.size)
            links[linkTracker[player]!!].startForPlayer(player)
        // otherwise, the player has complete this quest chain so call the end function
        else
            endForPlayer(player, true)
        updateSidebarForPlayer(player)
    }

    fun endForPlayer(player: Player, reward: Boolean) {
        // remove the given player from the quest state tracking map
        linkTracker.remove(player)

        // remove from quest chain tracker
        if (curQuest[player] == id)
            curQuest.remove(player)

        // if the player should be rewarded, call on complete
        if (reward)
            onComplete?.run(player)
        updateSidebarForPlayer(player)

        // notify player that the quest started
        player.sendTitle("" , "§6Completed quest: §a§l$name", 10, 50, 10)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
}