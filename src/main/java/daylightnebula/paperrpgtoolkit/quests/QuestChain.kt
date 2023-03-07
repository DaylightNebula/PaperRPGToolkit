package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.actions.Action
import daylightnebula.paperrpgtoolkit.buildScoreboard
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.ChatPaginator
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class QuestChain(
    val id: String,
    val name: String,
    val description: String,
    private val links: Array<QuestLink>,

    // callbacks
    private val onComplete: Action?,
) {
    companion object {
        private val questChains = hashMapOf<String, QuestChain>()
        private val curQuestState = hashMapOf<Player, Pair<String, Int>>()
        private val waitingJson = hashMapOf<String, JSONObject>()

        private val saveFile = File(PaperRPGToolkit.plugin.dataFolder, "save/players.json")
        private val saveJson = if (saveFile.exists()) JSONObject(saveFile.readText()) else { saveFile.parentFile.mkdirs(); JSONObject() }

        fun loadJSONFromFolder(folder: File) {
            folder.listFiles()?.forEach { file ->
                if (file.extension == "json")
                    waitingJson[file.nameWithoutExtension] = JSONObject(file.readText())
                else if (file.isDirectory)
                    loadJSONFromFolder(file)
            }
        }

        fun loadWaitingJson() {
            // load waiting json
            waitingJson.forEach { (id, json) ->
                QuestChain(id, json)
            }

            Bukkit.getOnlinePlayers().forEach { playerJoin(it) }
        }

        fun playerJoin(player: Player) {
            // load basic info
            val json = getJsonForPlayer(player)
            val questID = json.optString("quest") ?: return
            val link = json.optInt("link", 0)
            val chain = questChains[questID]
            if (chain == null) {
                json.remove("quest")
                json.remove("link")
                return
            }

            // save state
            curQuestState[player] = Pair(questID, link)

            // resume quest
            chain.links[link].startForPlayer(player)
            chain.updateSidebarForPlayer(player)
        }

        private fun getQuestByPlayer(player: Player): QuestChain? {
            return questChains[curQuestState[player]?.first ?: return null]
        }

        fun advanceQuestForPlayer(player: Player): Boolean {
            val chain = getQuestByPlayer(player)
            chain?.proceedToNextQuest(player)

            // save new link
            if (chain != null) {
                val json = getJsonForPlayer(player)
                json.remove("link")
                json.put("link", curQuestState[player]!!.second)
            }
            saveJsonToFile(false)

            return chain != null
        }

        fun stopQuestForPlayer(player: Player, reward: Boolean): Boolean {
            val chain = getQuestByPlayer(player)
            chain?.endForPlayer(player, reward)

            if (chain != null) {
                // get json for the player and remove quest and link
                val json = getJsonForPlayer(player)
                json.remove("quest")
                json.remove("link")

                // add this quest to completed list if reward
                if (reward) {
                    var arr = json.optJSONArray("completed")
                    if (arr == null) {
                        arr = JSONArray()
                        json.put("completed", arr)
                    }
                    if (!arr.contains(chain.id))
                        arr.put(chain.id)
                } else {
                    var arr = json.optJSONArray("incomplete")
                    if (arr == null) {
                        arr = JSONArray()
                        json.put("incomplete", arr)
                    }
                    arr.removeAll { (it as JSONObject).getString("quest") == chain.id }
                    arr.put(
                        JSONObject()
                            .put("quest", chain.id)
                            .put("link", chain.id)
                    )
                }
            }

            return chain != null
        }

        fun startQuestForPlayer(player: Player, id: String): Boolean {
            // stop old chain if necessary
            stopQuestForPlayer(player, false)

            // update json
            getJsonForPlayer(player).put("quest", id).put("link", 0)

            // start new chain
            val chain = questChains[id]
            chain?.startForPlayer(player)
            return chain != null
        }

        private var lastSave = 0L
        private fun saveJsonToFile(force: Boolean) {
            if (force || System.currentTimeMillis() - lastSave > 10000) {
                saveFile.writeText(saveJson.toString(1))
                lastSave = System.currentTimeMillis()
            }
        }

        fun getJsonForPlayer(player: Player): JSONObject {
            var json = saveJson.optJSONObject(player.uniqueId.toString())
            if (json == null) {
                json = JSONObject()
                saveJson.put(player.uniqueId.toString(), json)
            }
            return json
        }

        fun disable() {
            saveJsonToFile(true)
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

    init {
        questChains[id] = this
        links.forEach { it.init(this) }
    }

    fun updateSidebarForPlayer(player: Player) {
        // if the player is no longer on the quest chain, set players scoreboard to a blank scoreboard and cancel
        val state = curQuestState[player]
        if (state == null || state.first != id) {
            player.scoreboard = Bukkit.getScoreboardManager().newScoreboard
            return
        }

        // get current quest link
        val quest = links[state.second]

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
        curQuestState[player] = Pair(id, 0)
        updateSidebarForPlayer(player)

        // notify player that the quest started
        player.sendTitle("" , "§6Started quest: §a§l$name", 10, 50, 10)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }

    fun proceedToNextQuest(player: Player) {
        // make sure the given player has started this quest
        if (!curQuestState.containsKey(player)) return //throw NullPointerException("Attempting to proceed in quest for a player that has not started quest chain $name")
        val state = curQuestState[player]!!
        if (state.first != id) return

        // stop current quest
        links[state.second].stopForPlayer(player)

        // get and then set the new quest number for the given player
        val newCount = state.second + 1
        curQuestState[player] = Pair(id, newCount)

        // if new count is in range of the links array, start next quest
        if (newCount < links.size)
            links[newCount].startForPlayer(player)
        // otherwise, the player has complete this quest chain so call the end function
        else
            stopQuestForPlayer(player, true)
        updateSidebarForPlayer(player)
    }

    fun endForPlayer(player: Player, reward: Boolean) {
        if (curQuestState[player]?.first == id)
            curQuestState.remove(player)

        // if the player should be rewarded, call on complete
        if (reward)
            onComplete?.run(player)
        updateSidebarForPlayer(player)

        // notify player that the quest started
        player.sendTitle("" , "§6Completed quest: §a§l$name", 10, 50, 10)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
}