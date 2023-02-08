package daylightnebula.paperrpgtoolkit.dialogue

import daylightnebula.paperrpgtoolkit.PaperRPGToolkit
import daylightnebula.paperrpgtoolkit.goals.Goal
import daylightnebula.paperrpgtoolkit.goals.GoalInterface
import daylightnebula.paperrpgtoolkit.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class DialogueLink(
    val npc: NPC,
    val text: String,
    private val goal: Goal,
    val lockPlayer: Boolean,
    val options: Array<String>? = null,
    private val callback: ((player: Player, heldSlot: Int) -> Unit)? = null
): GoalInterface {

    lateinit var chain: DialogueChain

    fun init(chain: DialogueChain) {
        this.chain = chain
        goal.init(this)
    }

    private val playerToEntity = hashMapOf<Player, Entity>()

    fun getTargetEntityForPlayer(player: Player): Entity? {
        return playerToEntity[player]
    }

    fun startForPlayer(player: Player) {
        playerToEntity[player] = npc.entities.minBy { it.location.distanceSquared(player.location) }
        goal.startForPlayer(player)
        chain.draw(player, this)
    }

    fun stopForPlayer(player: Player) {
        playerToEntity.remove(player)
        goal.stopForPlayer(player)
    }

    override fun goalComplete(player: Player, goal: Goal) {
        chain.proceedToNextLink(player)
        callback?.let { it(player, player.inventory.heldItemSlot) }
    }

    override fun descriptionChanged(player: Player, goal: Goal) {}
}