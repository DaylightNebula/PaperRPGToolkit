package daylightnebula.paperrpgtoolkit.goals

import org.bukkit.entity.Player

abstract class Goal(
) {
    private var goalInterface: GoalInterface? = null

    fun init(inter: GoalInterface) {
        goalInterface = inter
    }

    fun getInterface(): GoalInterface {
        return goalInterface ?: throw UninitializedPropertyAccessException("Goal $this was never initialized")
    }

    fun finishGoal(player: Player) {
        goalInterface?.goalComplete(player, this) ?: throw UninitializedPropertyAccessException("Goal $this was never initialized")
    }

    fun descriptionChanged(player: Player) {
        goalInterface?.descriptionChanged(player, this) ?: throw UninitializedPropertyAccessException("Goal $this was never initialized")
    }

    abstract fun startForPlayer(player: Player)
    abstract fun stopForPlayer(player: Player)
    abstract fun forceComplete(player: Player)
    abstract fun getDescriptionText(player: Player): String
}