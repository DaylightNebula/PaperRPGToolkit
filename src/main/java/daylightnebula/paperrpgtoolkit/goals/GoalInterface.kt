package daylightnebula.paperrpgtoolkit.goals

import org.bukkit.entity.Player

interface GoalInterface {
    fun goalComplete(player: Player, goal: Goal)
    fun descriptionChanged(player: Player, goal: Goal)
}