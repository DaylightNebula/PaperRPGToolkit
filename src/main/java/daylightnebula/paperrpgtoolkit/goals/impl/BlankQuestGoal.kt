package daylightnebula.paperrpgtoolkit.goals.impl

import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.entity.Player

class BlankQuestGoal: Goal() {
    override fun forceComplete(player: Player) {
        finishQuest(player)
    }

    override fun getDescriptionText(player: Player): String {
        return ""
    }
}