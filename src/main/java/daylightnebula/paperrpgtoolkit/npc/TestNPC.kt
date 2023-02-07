package daylightnebula.paperrpgtoolkit.npc

import daylightnebula.paperrpgtoolkit.TestStuff
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerInteractEntityEvent

class TestNPC: NPC(
    "testnpc",
    "§dBob the builder",
    EntityType.VILLAGER
) {
    override fun onCreateNewEntity(entity: Entity) {}
    override fun onRightClick0(event: PlayerInteractEntityEvent) { TestStuff.testDialogue.startForPlayer(event.player) }
}