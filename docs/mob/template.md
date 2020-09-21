# Mob Templates

Mob templates are `.json` files located in `config/onslaught/templates/mob`. Subfolders are not detected and all template files must be placed in the root of this folder.

Each file can contain as many mob definitions as you like so long as the id of each definition is unique across all files.

## Definitions

key | type | description
:-|:-|:-
id              | string   | resource location of the mob
effects         | Effect[] | an array of effects to apply to the mob when spawned
extraLootTables | string[] | additional loot to drop when the mob dies
nbt             | NBT      | NBT data used to generate the mob

```js
{
  "zombie_basic": {
    "id": "minecraft:zombie"
  }
}
```

The `zombie_basic` key denotes a unique name for the mob definition and can be anything you like so long as it is unique across all mob template files. This name is used to reference the mob definition in invasion templates as well as Onslaught's summon command. 

## Effects

key | type | range | description
:-|:-|:-|:-
id            | string  | N/A   | resource location of the effect
duration      | int     | [0,-) | effect duration in ticks
amplifier     | int     | [0,-) | effect amplifier
showParticles | boolean | N/A   | show effect particles

```js
{
  "zombie_effects": {
    "id": "minecraft:zombie",
    "effects": [
      {
        "id": "minecraft:invisibility",
        "duration": 1200,
        "amplifier": 3,
        "showParticles": true 
      }
    ]
  }
}
```

An array of effects can be defined inside of the `effects` array key.

Each of the defined effects will be applied to the mob when it is spawned.

## Loot Tables

Mob definitions can define additional loot tables to roll when the mob dies.

The extra tables can reference existing loot tables or new loot tables located in the `config/onslaught/loot` folder.

```js
{
  "zombie_loot": {
    "id": "minecraft:zombie",
    "extraLootTables": [
      "onslaught:wither_skeleton",
      "minecraft:blaze"
    ]
  }
}
```

Loot tables must be placed into a subfolder of the `config/onslaught/loot` folder.

For example, the template above will use a loot table in the location `config/onslaught/loot/onslaught/wither_skeleton.json` as well as the vanilla loot table for the `blaze`.

Due to the way Onslaught works, existing loot tables can be overridden. For example, if you place a new loot table in `config/onslaught/loot/minecraft/blaze.json`, it will override the vanilla loot table for the `blaze` mob.

For loot table syntax, see the [Official Minecraft Wiki](https://minecraft.gamepedia.com/Loot_table).

## NBT

NBT can be applied to the mob using the `nbt` key.

```js
{
  "zombie_nbt": {
    "id": "minecraft:zombie",
    "nbt": {
      "HandItems": [
        {"Count": 1, "id": "minecraft:diamond_sword"},
        {"Count": 1, "id": "minecraft:shield"}
      ],
      "ArmorItems": [
        {"Count": 1, "id": "minecraft:diamond_boots"},
        {"Count": 1, "id": "minecraft:diamond_leggings"},
        {"Count": 1, "id": "minecraft:diamond_chestplate"},
        {"Count": 1, "id": "minecraft:diamond_helmet"}
      ],
      "HandDropChances": [1.0, 1.0],
      "ArmorDropChances": [1.0, 1.0, 1.0, 1.0]
    }
  }
}
```

For entity NBT syntax, see the [Official Minecraft Wiki](https://minecraft.gamepedia.com/Tutorials/Command_NBT_tags#Entities)