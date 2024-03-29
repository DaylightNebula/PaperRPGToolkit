package daylightnebula.paperrpgtoolkit.dialogue

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.actions.Action
import daylightnebula.paperrpgtoolkit.items.CustomItem
import io.papermc.paper.entity.LookAnchor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.ChatPaginator
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.IllegalArgumentException

class DialogueChain(
    val id: String,
    val subid: String,
    private val links: Array<DialogueLink>,
    val onComplete: Action?
) {

    companion object {
        private val chains = mutableListOf<DialogueChain>()
        val occupiedList = mutableListOf<Player>()
        private val waitingJson = mutableListOf<Triple<String, String, JSONObject>>()

        fun loadJSONFromFolder(folder: File) {
            folder.listFiles()?.forEach {
                if (it.extension == "json")
                    JSONArray(it.readText()).forEach { j ->
                        val json = j as? JSONObject ?: return@forEach
                        val subid = json.getString("id") ?: return@forEach
                        waitingJson.add(
                            Triple(
                                it.nameWithoutExtension,
                                subid,
                                json
                            )
                        )
                    }
                else if (it.isDirectory)
                    loadJSONFromFolder(it)
            }
        }

        fun loadWaitingJson() {
            waitingJson.forEach { triple ->
                val id = triple.first
                val subid = triple.second
                val json = triple.third
                DialogueChain(id, subid, json)
            }
        }

        fun startUpdateLoop() {
            // update each dialogue chain every tick
            Bukkit.getScheduler().runTaskTimer(PaperRPGToolkit.plugin, Runnable {
                chains.forEach { it.update() }
            }, 1L, 1L)
        }

        fun startChainForPlayer(id: String, subid: String, player: Player) {
            getChain(id, subid)?.startForPlayer(player)
        }

        private fun getChain(id: String, subid: String): DialogueChain? {
            return chains.firstOrNull { it.id.equals(id, ignoreCase = true) && it.subid.equals(subid, true) }
        }
    }

    constructor(id: String, subid: String, onComplete: Action? = null): this(
        id, subid,
        waitingJson.firstOrNull { it.first.equals(id, true) && it.second.equals(subid, true) }?.third
            ?: throw IllegalArgumentException("Could not find waiting json with id $id"),
        onComplete
    ) {
        waitingJson.removeIf { it.first.equals(id, true) && it.second.equals(subid, true) }
    }
    constructor(id: String, subid: String, json: JSONObject, onComplete: Action? = null): this(
        id,
        subid,
        json.optJSONArray("links").map { if (it is JSONObject) DialogueLink(it) else DialogueLink(JSONObject()) }.toTypedArray(),
        onComplete ?: Action.decode(json.optJSONObject("complete_action"))
    )

    init {
        chains.add(this)
        links.forEach { it.init(this) }
    }

    private val linkCounter = hashMapOf<Player, Int>()

    fun update() {
        // every tick, if the player is in a locked dialogue, update slow and look
        linkCounter.forEach { (player, linkIdx) ->
            if (links[linkIdx].lockPlayer) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 2, 255, true))
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 2, 255, true))
                val entity = links[linkIdx].getTargetEntityForPlayer(player) ?: return@forEach
                player.lookAt(entity, LookAnchor.EYES, LookAnchor.EYES)
            }
        }
    }

    fun startForPlayer(player: Player) {
        // start counter
        linkCounter[player] = 0

        // start the first link
        links[0].startForPlayer(player)

        occupiedList.add(player)
    }

    fun proceedToNextLink(player: Player) {
        // make sure the given player has started this dialogue
        if (!linkCounter.containsKey(player)) return

        // stop current link
        links[linkCounter[player]!!].stopForPlayer(player)

        // get and then set the new link number for the given player
        val newCount = linkCounter[player]!! + 1
        linkCounter[player] = newCount

        // if new count is in range of the links array, start next link
        if (newCount < links.size)
            links[linkCounter[player]!!].startForPlayer(player)
        // otherwise, the player has complete this chain so call the end function
        else
            endForPlayer(player)
    }

    private fun endForPlayer(player: Player) {
        // clear chat
        for (i in 0 until maxLines) {
            player.sendMessage("")
        }

        // call events
        Bukkit.getPluginManager().callEvent(DialogueFinishEvent(player, this))
        onComplete?.let { it.run(player) }

        // remove the given player from the quest state tracking map
        linkCounter.remove(player)

        // remove from occupied list
        occupiedList.remove(player)
    }

    private val maxLines = 10
    private val limitPerLine = 70
    fun draw(player: Player, link: DialogueLink) {
        // get the lines for the message
        val linesForMessage = maxLines - (link.options?.size ?: 1)

        // get message lines
        val messageLines = listOf(
            link.npc?.name ?: "????",
            *ChatPaginator.wordWrap(link.text, limitPerLine)
        )

        // get spacing on top and bottom of the message
        val spaceToFind = linesForMessage - messageLines.size
        val topSpace = (spaceToFind / 2f).toInt()
        val botSpace = if (spaceToFind % 2 != 0) topSpace + 1 else topSpace

        // send everything to the player
        repeat(topSpace) { player.sendMessage(" ") }
        for (line in messageLines) { player.sendMessage(centerText(line, limitPerLine)) } // synchronous to avoid any potential weirdness
        repeat(botSpace) { player.sendMessage(" ") }

        // only send options if we have them, otherwise, send press shift to continue message
        if (link.options.isNullOrEmpty())
            player.sendMessage(centerText("Press Shift to Continue", limitPerLine))
        else
            link.options.forEachIndexed { index, s ->
                player.sendMessage("§b[${index + 1}] §f$s")
            }
    }

    private fun centerText(line: String, limit: Int): String {
        val builder = StringBuilder()
        val spaceToAdd = (limit - line.length) / 2
        repeat(spaceToAdd) { builder.append(" ") }
        builder.append(line)
        return builder.toString()
    }
}