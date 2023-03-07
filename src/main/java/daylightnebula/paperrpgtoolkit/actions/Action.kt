package daylightnebula.paperrpgtoolkit.actions

import org.bukkit.entity.Player
import org.json.JSONObject
import java.lang.IllegalArgumentException

abstract class Action {

    companion object {
        private val registeredActions = hashMapOf<String, (json: JSONObject) -> Action>()

        init {
            registerAction("give_item") { ActionGiveItem(it) }
            registerAction("spawn_mob") { ActionSpawnMob(it) }
            registerAction("start_dialogue") { ActionStartDialogue(it) }
            registerAction("start_quest") { ActionStartQuest(it) }
            registerAction("split_hotbar_slot") { ActionHotbarSplit(it) }
            registerAction("run_quest_completed_state") { ActionRunOnQuestCompletedState(it) }
        }

        fun decode(json: JSONObject?): Action? {
            if (json == null) return null
            return registeredActions[json.getString("type")]?.let { it(json) }
                ?: throw IllegalArgumentException("No action registered with type ${json.getString("type")}")
        }

        fun registerAction(type: String, create: (json: JSONObject) -> Action) {
            registeredActions[type] = create
        }
    }

    abstract fun run(player: Player)
}