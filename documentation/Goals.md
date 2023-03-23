# Goals
Goals are used by quests and dialogues to trigger when they should advance the respective quests or dialogues.

For example, if a quest wants a player to collect 10 apples, the developer can simply create a goal to require the player to get 10 apples.  This would work like this:
```json
{
  "type": "get_item",
  "item": "apple",
  "amount": 10
}
```

## Goal Types
#### Blank
A simple blank goal, this could be useful at some point.  HOWEVER, this goal NEVER completes.

Type: blank

Mandatory Arguments: None

Optional Arguments: None

```json
{
  "type": "blank"
}
```


#### Click NPC
A goal that completes when a user clicks a specified NPC.  Optionally, this goal can be given an item and an amount that the NPC must be clicked with.

Type: click_npc

Mandatory Arguments: 
- npc: The ID of the NPC to be clicked.

Optional Arguments:
- item: The material type or custom model ID of the item that the NPC must be clicked with.  If nothing is given, the NPC can be clicked with any item.
- amount: The amount of the specified item the NPC must be clicked with.  Default is 1.
- removeItems: If not specified or true, the above item and amount will be removed from the players inventory when the click occurs.

```json
{
  "type": "click_npc",
  "item": "apple",
  "amount": 10,
  "removeItems": true
}
```


#### Complete Dialogues
A goal that completes when a user completes a dialogue with the given id and subid.

Type: complete_dialogue

Mandatory Arguments:
- id: The ID of the dialogue chain (usually the name of the file the dialogue chain is in).
- subid: The sub-ID of the dialogue chain (the id specified with the dialogue chain).

Optional Arguments: None

```json
{
  "type": "complete_dialogue",
  "id": "bobsApples",
  "subid": "bobAskForApples"
}
```


#### Get Item
A goal that completes when a user collects the specified item and amount.

Type: get_item

Mandatory Arguments:
- item: A material type or custom item ID for the item that the user needs to collect.

Optional Arguments
- amount: The amount of the item to collect.  Default is 1.

```json
{
  "item": "apple",
  "amount": 10
}
```


#### Goto location
A goal that completes when the user goes to the specified location.

Type: goto_location

Mandatory Arguments:
- location: An array of 3 decimal values that represents the location that the user needs to go to complete this goal.

Optional Arguments:
- minDistance: The distance from the given point at which the goal is considered done.  Default is 1 block.

```json
{
  "type": "goto_location",
  "location": [ 100.0, 100.0, 200.0 ],
  "minDistance": 2.0
}
```


#### Kill Entity
A goal that completes when a user kills the specified entity.

Type: kill_entity

Mandatory Arguments:
- entity: The entity type or custom mob ID that the user needs to kill to complete the goal.

Optional Arguments:
- kills: The number of entities the user needs to kill.  Default is 1.
- location: A array of 3 decimal values that represents where the user needs to kill the entity at.  If this is not specified, the user can kill the entity anywhere in the world to complete the goal.
- radius: If a location is given, this decimal value represents the distance from the location in which the user is considered to be "at" the location.

```json
{
  "type": "kill_entity",
  "entity": "skeleton",
  "kills": 10,
  "location": [ 100.0, 100.0, 200.0 ],
  "radius": 100
}
```


#### Press Shift
A goal that completes when a user presses shift.  This is useful for dialogues so that players can press shift to press shift to continue.

Type: press_shift

Mandatory Arguments: None

Optional Arguments: None

```json
{
  "type": "press_shift"
}
```


#### Select Number
A goal that completes when the user selects a number between the given range using their hotbar.  For example, if the user presses 1 to go to the first slot of their hot-bar, the goal will complete if 0 is in the given range.

Type: select_number

Mandatory Arguments: None

Optional Arguments:
- min: The minimum slot that the user can select.  0 being the first slot, and 7 being the 8th slot.  The 9th slot should NOT be used as it is needed by PaperRPGToolkit for slot change detection.  Default is 0.
- max: The maximum slot that the user can select.  0 being the fist slot, and 7 being the 8th slot.  The 9th slot, again, should NOT be used.  Default is 7.

```json
{
  "type": "select_number",
  "min": 0,
  "max": 1
}
```