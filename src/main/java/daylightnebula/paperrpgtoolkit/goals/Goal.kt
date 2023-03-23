package daylightnebula.paperrpgtoolkit.goals

import daylightnebula.paperrpgtoolkit.goals.impl.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.json.JSONArray
import org.json.JSONObject

abstract class Goal(
) {

    companion object {
        fun convertJSONArrayToGoalsArray(arr: JSONArray): Array<Goal> {
            return Array(arr.length()) { idx -> convertJSONToGoal(arr.getJSONObject(idx)) }
        }

        fun convertJSONToGoal(json: JSONObject): Goal {
            return when (json.getString("type")) {
                "blank" -> BlankQuestGoal()
                "click_npc" -> ClickNPCWithItemGoal(
                    json.optString("npc", ""),
                    json.optString("item", ""),
                    json.optInt("amount", 1),
                    json.optBoolean("removeItems", true)
                )
                "complete_dialogue" -> CompleteDialogueGoal(
                    json.optString("id", ""),
                    json.optString("subid", "")
                )
                "get_item" -> GetItemGoal(
                    json.optString("item", ""),
                    json.optInt("amount", 1)
                )
                "goto_location" -> GotoLocationGoal(
                    Vector(
                        json.optJSONArray("location")?.getDouble(0) ?: 0.0,
                        json.optJSONArray("location")?.getDouble(1) ?: 0.0,
                        json.optJSONArray("location")?.getDouble(2) ?: 0.0,
                    ),
                    json.optFloat("minDistance", 1f)
                )
                "kill_entity" -> KillEntityGoal(
                    json.optString("entity", ""),
                    json.optInt("kills", 1),
                    if (json.has("location"))
                        Vector(
                            json.getJSONArray("location").getDouble(0),
                            json.getJSONArray("location").getDouble(1),
                            json.getJSONArray("location").getDouble(2)
                        )
                    else null,
                    json.optFloat("radius", 1f)
                )
                "press_shift" -> PressShiftGoal()
                "select_number" -> SelectNumberedOptionGoal(
                    json.optInt("min", 0),
                    json.optInt("max", 7),
                )
                else -> BlankQuestGoal()
            }
        }
    }

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