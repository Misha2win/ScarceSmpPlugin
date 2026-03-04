# ScarceLife

ScarceLife is a hardcore-style SMP plugin where players have limited lives and become ghosts when they die, creating a survival experience focused on risk, conflict, and recovery.

## Features

- **Limited Lives** – Players have a configurable number of lives. Player nametags are colored based on remaining lives:
  - Dark Green = 4+
  - Green = 3
  - Yellow = 2
  - Red = 1
  - Gray = 0
- **Keep Inventory on Death** – Players keep their inventories unless they lose their last life.
- **Ghost Mode** – Players with 0 lives become ghosts instead of being kicked.
- **Player Head Drops** – Players drop heads with their death message.
- **Configurable Commands & Items** – Enable or disable commands like `/givelife` and `/tpa`, and toggle features such as ghost mode or custom items.
- **Eden Apple** – A rare item that grants extra lives. Crafted with player heads, netherite, and **Unstable Mix** in a smithing table.
- **Unstable Mix** - A crafting ingredient for the **Eden Apple**. Created with wither skeleton skulls, amethyst, and dragon's breath.
- **Cursed Enchanting Table** – Replaces the normal enchanting table with a one-time craftable version. The player who crafts or holds it becomes cursed, revealing their location and turning the table into a server-wide objective.

## Installation

1. Download the latest `.jar` from the Releases page.
2. Place it in your server's `plugins/` folder.
3. Start the server.
4. Edit `config.yml` if desired.
5. Restart or run `/scarce reload`.

## Commands

| Command | Description |
|-------|-------------|
| `/lives` | Admin command to set, give, or remove lives |
| `/givelife <player> <amount>` | Give one of your lives to another player |
| `/givescarce <item>` | Admin command to give a special ScarceLife item |
| `/tpa` | Teleport request command |
| `/scarceconfig` | Read or modify config values in game |

## Configuration

Example `config.yml`:

```yaml
lives:
  max: 4

ghost:
  enabled: true

items:
  eden-apple:
    enabled: true
```

## Gameplay

When a player dies, they lose a life.  
If they reach 0 lives, they become a ghost.

Ghosts can still move around the world but have limited interaction with it.  
They can fly and are given special items that allow abilities like blinking in and out of existence.

Players can regain lives by consuming an Eden Apple or receiving lives from other players using `/givelife`.

Eden Apples are crafted using player heads, a netherite ingot, and the **Unstable Mix** item in a smithing table.

## Compatibility

Tested on:
- Spigot 1.21.11
