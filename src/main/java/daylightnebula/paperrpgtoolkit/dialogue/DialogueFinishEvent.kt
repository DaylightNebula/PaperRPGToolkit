package daylightnebula.paperrpgtoolkit.dialogue

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class DialogueFinishEvent(
    val player: Player,
    val dialogueChain: DialogueChain
): Event() {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}