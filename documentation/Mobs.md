# Custom Mobs
These mobs are meant as an extension to the Minecraft mobs.  Therefore, this system is limited.  If you are looking for something more in-depth, use something like Mythic Mobs.

When a custom mob is created, its ID is taken from the file name that it is loaded from.

Reference [Tasks](Tasks.md) for more information on the task system that is referenced in the optional arguments section.

## File Format
A custom mob is created by placing a json file in the mobs folder inside the PaperRPGToolkit plugin data folder.  The file should contain a single json object that contains all mandatory arguments listed below.

## Mandatory Arguments
#### Display Name (json: "displayName")
This will be the display name of the mob.  However, it may be a blank string (ex: "").  The custom name will be visible as a name tag if it is not blank.

#### Super Type (json: "supertype")
This is the entity type that a mobs stats and model are taken from.  As well as, their attack effects.  For example, if a super type of WITHER_SKELETON is given, the mob will look and have the stats of a wither skeleton and will even apply the wither effect on attack.

#### Tasks (json: "tasks")
A json array of task objects.  Reference [Tasks](Tasks.md) for more information

## Mob Stats Optional Arguments
The following arguments will default to the given supertypes stats if no value is given.  They are all decimal values that should be greater than 0.  Reference the Minecraft wiki for more information on each stat.  Each mob will spawn with its given max health.

The options are:
- Armor (json: "armor")
- Armor Toughness (json: "armorToughness")
- Attack Damage (json: "attackDamage")
- Attack Knock Back (json: "attackKnockback")
- Attack Speed (json: "attackSpeed")
- Follow Range (json: "followRange")
- Flying Speed (json: "flyingSpeed")
- Knock Back Resistance (json: "knockBackResistance")
- luck (json: "luck")
- maxHealth (json: "maxHealth")

## Example
```json
{
  "displayName": "§0Dark Skeleton",
  "supertype": "WITHER_SKELETON",
  "maxHealth": 40.0,
  "tasks": [
    {
      "type": "wander",
      "range": 20.0,
      "minWaitTicks": 30,
      "maxWaitTicks": 100
    },
    {
      "type": "attack_players",
      "detectRange": 5.0
    }
  ]
}
```