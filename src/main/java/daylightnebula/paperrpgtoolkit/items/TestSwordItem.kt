package daylightnebula.paperrpgtoolkit.items

import org.bukkit.Material

class TestSwordItem: CustomItem(
    "testsword",
    Material.IRON_SWORD,
    "Test Sword",
    "Bestest Test Sword",
    2,
    3.0,
    1.5
) {

    override fun leftClick() { println("L click") }
    override fun rightClick() { println("R click") }
}