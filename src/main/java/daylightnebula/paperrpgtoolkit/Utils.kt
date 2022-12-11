package daylightnebula.paperrpgtoolkit

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.ChatPaginator


fun item(
    srcMaterial: Material,
    name: String = "",
    description: String = "",
    customModelData: Int = -1,
    customItemReferenceID: String? = null,
    attackSpeed: Double = -1.0,
    attackDamage: Double = -1.0,
    knockback: Double = -1.0
): ItemStack {
    // create base item from source material
    val item = ItemStack(srcMaterial)

    // get a copy of the new items metadata
    val meta = item.itemMeta

    // if this function was given a custom item reference id
    if (customItemReferenceID != null) {
        // add a custom tag to the metadatas persistent data container containing the custom item reference id
        meta.persistentDataContainer.set(PaperRPGToolkit.customItemReferenceIDKey, PersistentDataType.STRING, customItemReferenceID)
    }

    // if this function was given a name
    if (name.length > 0) {
        // set item metadata's display name to a component version of the given name
        meta.displayName(Component.text(name))
    }

    // if this function was given a description
    if (description.length > 0) {
        // paginate the given description so it is not too big
        val lorePages = ChatPaginator.paginate(description, 20).lines

        // set item metadata lore to the lore pages mapped to components
        meta.lore(lorePages.map { Component.text(it) })
    }

    // if this function was given some custom model data
    if (customModelData != -1) {
        // set the item metadata custom model data to the given custom model data
        meta.setCustomModelData(customModelData)
    }

    // if this function was given an attack speed value
    if (attackSpeed != -1.0) {
        // add an attribute modifier to the items metadata to set the items attack speed
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, AttributeModifier("customItem", attackSpeed, AttributeModifier.Operation.ADD_NUMBER))
    }

    // if this function is given an attack damage value
    if (attackDamage != -1.0) {
        // add an attribute modifier to the items metadata to set the items attack damage
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier("customItem", attackDamage, AttributeModifier.Operation.ADD_NUMBER))
    }

    // if this function was given a knock back value
    if (knockback != -1.0) {
        // add an attribute modifier to the items metadata to set the items knock back
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, AttributeModifier("customItem", knockback, AttributeModifier.Operation.ADD_NUMBER))
    }

    // set the items metadata to the modified copy we made
    item.itemMeta = meta

    // return the final item
    return item
}