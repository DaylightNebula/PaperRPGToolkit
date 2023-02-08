package daylightnebula.paperrpgtoolkit.dialogue

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import io.papermc.paper.entity.LookAnchor
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.ChatPaginator
import org.bukkit.util.StringUtil
import java.awt.Component

class DialogueChain(
    val links: Array<DialogueLink>,
    val onComplete: (player: Player) -> Unit
) {

    companion object {
        val chains = mutableListOf<DialogueChain>()
        val occupiedList = mutableListOf<Player>()

        fun startUpdateLoop() {
            // update each dialogue chain every tick
            Bukkit.getScheduler().runTaskTimer(PaperRPGToolkit.plugin, Runnable {
                chains.forEach { it.update() }
            }, 1L, 1L)
        }
    }

    init {
        chains.add(this)
        links.forEach { it.init(this) }
    }

    private val linkCounter = hashMapOf<Player, Int>()

    fun update() {
        // every tick, if the player is in a locked dialogue, update slow and look
        linkCounter.forEach { (player, linkIdx) ->
            if (links[linkIdx].lockPlayer) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 2, 255))
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

    fun endForPlayer(player: Player) {
        // remove the given player from the quest state tracking map
        linkCounter.remove(player)

        // if the player should be rewarded, call on complete
        onComplete(player)

        occupiedList.remove(player)
    }

    val maxLines = 10
    val limitPerLine = 70
    fun draw(player: Player, link: DialogueLink) {
        // get the lines for the message
        val linesForMessage = maxLines - (link.options?.size ?: 1)

        // get message lines
        val messageLines = listOf(
            link.npc.name ?: "????",
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