# Dialogues
Dialogues are conversations that NPCs can have with the players.  Each dialogue is a "chain" of dialogue "links".  Each link has custom text, NPC to say from, and a goal to proceed to the next link (please reference the argument section below for official information).

A dialogue chain has both an ID and a sub-ID.  The ID is the name of the file from which it originates, and the sub-ID is the ID of the dialogue chain in the file.


## File format
The file should contain a array of dialogue chain objects.  These files should be put in the dialogue folder in PaperRPGToolkit's data folder.

Example
```json
[
  ... list of dialogue chain objects seperated by commas
]
```


## Dialogue Chain Object
The dialogue chain object is an object that contains all segments of the dialogue.

Mandatory Arguments:
- id: The sub-ID of the dialogue
- links: An array of dialogue link objects

Optional Arguments:
- complete_action: An action that is called when the dialogue is completed

```json
{
  "id": "bobsApples",
  "links": [
    ... list of dialogue link objects sperated by commas
  ],
  "complete_action": ... action to run on complete
}
```


## Dialogue Link Object
The dialogue link object is an object that contains the content of each element of a dialogue.

Mandatory Arguments:
- goal: A goal that when complete causes the dialogue to proceed to the next link.

Optional Arguments:
- npc: The NPC that is talking during the link.
- text: The text that will be shown as what is being said during the dialogue link.
- options: A list of options that will be displayed along with the text.  Numbered starting at 1.
- lock: If not set or true, the player will be forced to look at the nearest instance of the given NPC or just simply locked in place.

```json
{
  "npc": "bob",
  "text": "Will you get me some apples?",
  "lock": true,
  "options": [ "Yes", "No" ],
  "goal": ... goal to complete this dialogue link
}
```