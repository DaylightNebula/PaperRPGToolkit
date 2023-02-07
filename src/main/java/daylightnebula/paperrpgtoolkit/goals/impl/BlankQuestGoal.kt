package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.entity.Player

class BlankQuestGoal: Goal() {
    override fun forceComplete(player: Player) {
        finishGoal(player)
    }

    override fun getDescriptionText(player: Player): String {
        return ""
    }

    override fun startForPlayer(player: Player) {}
    override fun stopForPlayer(player: Player) {}
}