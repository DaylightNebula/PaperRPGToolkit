package daylightnebula.paperrpgtoolkit

import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.entity.Player

interface GoalInterface {
    fun goalComplete(player: Player, goal: Goal)
    fun descriptionChanged(player: Player, goal: Goal)
    fun doesPlayerHasGoal(player: Player, goal: Goal): Boolean
}