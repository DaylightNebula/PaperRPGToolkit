package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.dialogue.DialogueChain
import daylightnebula.paperrpgtoolkit.dialogue.DialogueFinishEvent
import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CompleteDialogueGoal(private val chainID: String): Listener, Goal() {

    val activePlayers = mutableListOf<Player>()

    init {
        Bukkit.getPluginManager().registerEvents(this, PaperRPGToolkit.plugin)
    }

    @EventHandler
    fun onDialogueFinish(event: DialogueFinishEvent) {
        if (activePlayers.contains(event.player) && event.dialogueChain.id == chainID)
            finishGoal(event.player)
    }

    override fun startForPlayer(player: Player) {
        activePlayers.add(player)
    }

    override fun stopForPlayer(player: Player) {
        activePlayers.remove(player)
    }

    override fun getDescriptionText(player: Player): String {
        return ""
    }

    override fun forceComplete(player: Player) {
        finishGoal(player)
    }
}