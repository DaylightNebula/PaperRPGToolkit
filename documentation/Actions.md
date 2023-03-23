# Actions
Actions are a simple json object that tells PaperRPGToolkit what to do when the action is run.  Actions may be given to other objects (like quests or dialogues) to be run when that object chooses.  For example, when a quest ends it can run a action that says to give the player 10 apples.

Action example:
```json
{
  "type": "give_item",
  "id": "apple",
  "amount": 10
}
```

## Action Format
An action object must always be given a type argument (a list of which can be found below).  As well as, any other arguments specified by the type.

## Action Types

### Give Item
This action gives an item of the specified ID to the player.  An amount may optionally be given.  The ID may reference any material type in minecraft by default or a custom item ID.

Type: give_item

Mandatory Arguments:
- id: The material type or custom item ID of any item.

Optional Arguments:
- amount: The amount to give the player.  Default is 1.

Example:
```json
{
  "type": "give_item",
  "id": "apple",
  "amount": 10
}
```


### Spawn Mob
This action spawns a mob of the specified ID.

Type: spawn_mob

Mandatory Arguments:
- id: The entity type or custom mob ID of any mob.

Optional Arguments:
- location: The location at which to spawn the mob.  Default is the players location.
- spawnSpread: If set, the location will be randomly offset by random values between the positives and negatives of the numbers given.
- world: The numeric ID of the world in which the mob should spawn (0 = overworld, 1 = nether, 2 = end).
- amount: The amount of mobs to spawn.  Default is 1.

```json
{
  "type": "spawn_mob",
  "id": "wither_skeleton",
  "amount": 4,
  "spawnSpread": [5.0, 0.0, 5.0]
}
```


### Start Dialogue
This action starts a dialogue for the player.

Type: start_dialogue

Mandatory Arguments:
- id: A dialogue ID (this is usually the name of the file containing the dialogue chain).
- subid: The ID of the dialogue chain itself.

Optional Arguments: None

```json
{
  "type": "start_dialogue",
  "id": "bobsApples",
  "subid": "bobAskForApples"
}
```


### Start Quest
This action starts a quest for the player.

Type: start_quest

Mandatory Arguments:
- quest: The ID of the quest to start

Optional Arguments: None

```json
{
  "type": "start_quest",
  "quest": "bobsApples"
}
```


### Split Hot-bar Slot
This action will run any of the actions given to it based on which slot is given.  For example, if the first hot bar slot is chosen, the first action will be run, and the same for the second slot and so on.

Type: split_hotbar_slot

Mandatory Arguments:
- actions: An array of actions.  An action is run if its corresponding hot-bar slot is chosen.

Optional Arguments: None

```json
{
  "type": "split_hotbar_slot",
  "actions": [
    ... a list of action objects seperated by commas
  ]
}
```


### Run Quest on Completed State
This action runs another action given too it if a quest has been completed before.  For example, this action can be used to only trigger the other action when a quest has not been completed before ie the quest has been completed for the first time.

Type: split_hotbar_slot

Mandatory Arguments:
- quest: The ID for the quest in question.
- action: The action to be run.

Optional Arguments:
- completed: If false, the action will only be run if the quest has not been completed or (if run when a quest is complete, then if the quest has been completed for the first time).  If true, the action will run under the opposite conditions.

```json
{
  "type": "split_hotbar_slot",
  "quest": "bobsApples",
  "completed": false,
  "action": ... action object to run
}
```