
 - [x] Custom Items (and weapons) (register at start up for resource pack generation)
   - [x] Custom item give command
   - [x] Fields and functions to set attack speed and damage
   - [x] Left and right click detection (with limits on both (1/2 second))
   - [x] Name and description fields that convert to components
 - [x] Quest Chains
   - [x] Start and force end quest command
   - [x] Current quest shows up in the sidebar
   - [x] Quests goals:
     - [x] Collect item (with number)
     - [x] Collect custom item
     - [x] Go to location (with acceptable radius)
     - [x] Kill number of entities (optional location and radius to get the kills)
 - [x] NPCs
   - [x] Spawn NPC command
   - [x] Remove NPC command
   - [x] Right click detection
   - [x] Goal: Give item to NPC (click NPC with item) (optional remove item)
   - [x] Goal: Finish dialogue (talk to NPC goal, idea is that it is better to finish the goal after the dialogue is done)
   - [x] Dialogue
     - [x] Dialogue chains
     - [x] Lock player into chat
       - [x] Disable standard chat for the player while they are in the dialogue
     - [x] Dialogue goals
       - [x] Convert quest goals to general purpose goals
       - [x] Press shift goal
       - [x] Press hot bar number and switch dialogue chain
 - [x] Custom Mobs
   - [x] Entity Tasks
     - [x] Wander near point (average time between wander and well as spread) (take over when out of range option)
     - [x] Stay at point 
     - [x] Attack nearby players
   - [x] Spawn command
   - [x] Update "kill number of entities" goal to account for custom mobs accordingly
 - [x] Custom Mob Spawner
   - [x] Maximum active
   - [x] Minimum active
   - [x] Range
 - [x] Allow for json configs so that code is not always necessary and vice versa
   - [x] Items
   - [x] NPCs
     - [x] Actions
       - [x] Start dialogue
       - [x] Start quest
       - [x] Give item
       - [x] Spawn mob (with amount) (both custom and not)
       - [x] Custom
   - [x] Goals
   - [x] Dialogues (convert to id and sub id pair)
   - [x] Quests
     - [x] Split action for hot bar slot selection
   - [x] Mobs
     - [x] Entity Tasks
 - [ ] Save system
   - [x] Spawners
   - [x] NPC spawns
   - [ ] Mob spawns (only those not from a spawner)
   - [ ] Player
     - [ ] Quest state
     - [ ] Quests completed
       - [ ] Update quest complete function to report if the player finished the quest for the first time
     - [ ] Dialogues completed
       - [ ] Update dialogue complete function to report if the player finished the dialogue for the first time
 - [ ] Custom Resource Pack
   - [ ] Generate item models from .bbmodel files or just textures
   - [ ] Add custom model support for items
   - [ ] Add custom model support for NPCs
   - [ ] Add custom model support for mobs
 - [ ] Custom Blocks
   - [ ] Get command
   - [ ] Custom models
   - [ ] Quest goal: Give item to block (optional remove item)
   - [ ] Highlight option
 - [ ] Area protection
   - [ ] Allow for complete or partial disabling of block breaking (except those with special permission)
   - [ ] Allow for complete or partial disabling of PvP in areas
 - [ ] Permissions
   - [ ] Custom Item Command
   - [ ] Start and force stop quest commands
   - [ ] Spawn NPC command
   - [ ] Custom blocks get command
   - [ ] Spawn custom mob command
   - [ ] Make spawner area for custom mobs visible
   - [ ] Area protection create and ignore
   - [ ] Area PvP protection create and ignore
 - [ ] Custom UIs
   - [ ] Open command w/ permission
   - [ ] Create builder
   - [ ] UI Elements:
      - [ ] Button
      - [ ] Checkbox
      - [ ] Text input
      - [ ] Scrolling list
 - [ ] Localization support
   - [ ] Language settings for different players
   - [ ] Command to change
   - [ ] Convert all names and descriptions to locale keys
   - [ ] Locale files for multiple languages
   - [ ] Auto translating? (prolly not possible but something to look into)