package daylightnebula.paperrpgtoolkit.quests.goals

import org.bukkit.entity.Player

class BlankQuestGoal: QuestGoal() {
    override fun forceComplete(player: Player) {
        finishQuest(player)
    }

    override fun getDescriptionText(player: Player): String {
        return ""
    }
}