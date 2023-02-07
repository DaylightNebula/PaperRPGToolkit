package daylightnebula.paperrpgtoolkit.goals

import daylightnebula.paperrpgtoolkit.GoalInterface
import org.bukkit.entity.Player
import java.lang.NullPointerException

abstract class Goal(
) {
    private var goalInterface: GoalInterface? = null

    fun init(inter: GoalInterface) {
        goalInterface = inter
    }

    fun getInterface(): GoalInterface {
        return goalInterface ?: throw UninitializedPropertyAccessException("Goal $this was never initialized")
    }

    fun finishQuest(player: Player) {
        goalInterface?.goalComplete(player, this) ?: throw UninitializedPropertyAccessException("Goal $this was never initialized")
    }

    fun playerHasGoal(player: Player): Boolean {
        return goalInterface?.doesPlayerHasGoal(player, this) ?: throw UninitializedPropertyAccessException("Goal $this was never initialized")
    }

    fun descriptionChanged(player: Player) {
        goalInterface?.descriptionChanged(player, this) ?: throw UninitializedPropertyAccessException("Goal $this was never initialized")
    }

    abstract fun forceComplete(player: Player)
    abstract fun getDescriptionText(player: Player): String
}