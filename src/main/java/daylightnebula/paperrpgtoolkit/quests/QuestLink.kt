package daylightnebula.paperrpgtoolkit.quests

import daylightnebula.paperrpgtoolkit.actions.Action
import daylightnebula.paperrpgtoolkit.goals.GoalInterface
import daylightnebula.paperrpgtoolkit.goals.Goal
import org.bukkit.entity.Player
import org.json.JSONObject
import java.lang.IllegalArgumentException

class QuestLink(
    val name: String,
    val description: String,
    val goal: Goal,
    val onGoalComplete: Action?
): GoalInterface {

    constructor(json: JSONObject): this(
        json.optString("name", ""),
        json.optString("description", ""),
        Goal.convertJSONToGoal(
            json.optJSONObject("goal")
                ?: throw IllegalArgumentException("Quest links must be given a goal")
        ),
        Action.decode(json.optJSONObject("complete_action"))
    )

    lateinit var chain: QuestChain
    fun init(chain: QuestChain) {
        this.chain = chain
        goal.init(this)
    }

    fun startForPlayer(player: Player) { goal.startForPlayer(player) }
    fun stopForPlayer(player: Player) { goal.stopForPlayer(player) }

    override fun goalComplete(player: Player, goal: Goal) {
        onGoalComplete?.run(player)
        chain.proceedToNextQuest(player)
    }

    override fun descriptionChanged(player: Player, goal: Goal) {
        chain.updateSidebarForPlayer(player)
    }
}