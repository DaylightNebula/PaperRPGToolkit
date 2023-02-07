package daylightnebula.paperrpgtoolkit.npc

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerInteractEntityEvent

class TestNPC: NPC(
    "testnpc",
    Component.text("Bob the builder").color(TextColor.color(150, 0, 255)),
    EntityType.VILLAGER
) {
    override fun onCreateNewEntity(entity: Entity) {}
    override fun onRightClick0(event: PlayerInteractEntityEvent) { println("Click detected") }
}