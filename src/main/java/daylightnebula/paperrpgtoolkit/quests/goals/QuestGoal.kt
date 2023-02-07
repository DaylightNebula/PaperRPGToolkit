package daylightnebula.paperrpgtoolkit.quests.goals

import daylightnebula.paperrpgtoolkit.quests.Quest
import org.bukkit.entity.Player

abstract class QuestGoal(
) {
    var quest: Quest? = null

    fun finishQuest(player: Player) {
        quest?.goalComplete(player)
    }
}