# Custom Items
These custom items are meant as an extension of the item system already in Minecraft.  Therefore it is very limited.  No custom textures.  No custom recipes (yet).  These items can still be used in crafting as well (a custom item based on black dye will behave like black dye in a crafting table).

If you are looking for something more powerful, use a plugin like ItemsAdder.  This is meant as a simple system for getting some basic custom items.

When a custom item is created, it's ID is taken from the name of the file from which it is created.

## File Format
A item is generated via a json file placed in the items folder inside the PaperRPGToolkit plugin folder.  The file should contain a single json object describing the item, and the items ID will be the files name without the extension.

## Mandatory Arguments
#### Display Name (json: "displayName")
This is the name of the item as it is displayed to users.

#### Description (json: "description")
This is the description or lore of the item as it should be displayed to users.

#### Source Material (json: "sourceMaterial")
This is the material from which the item will inherit most of its behaviours, as well as, texture.  Any material type in Minecraft can be given here.

## Optional Arguments
#### Damage (json: "damage")
This is the damage the item will deal if an entity is hit with this item.  If not set, the damage will just be the default for the source material.

#### Speed (json: "speed")
This is the attack speed of the item.  If not set, the damage will just be the default for the source material.

#### Knockback (json: "knockback")
This is the knockback of the item.  If not set, the knockback will just the default for the source material.

## Example
```json
{
  "displayName": "Great Sword",
  "description": "The best great sword!",
  "sourceMaterial": "IRON_SWORD",
  "damage": 9.0,
  "speed": 4.0,
  "knockback": 4.0
}
```