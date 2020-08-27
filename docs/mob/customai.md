# Custom AI

Onslaught contains several custom AI tasks that can be assigned to entities using NBT.

All custom AI NBT needs to be placed inside the `ForgeData/Onslaught/CustomAI` tag.

Each task exposes some optionally configurable properties. If you omit any of the properties, defaults from the mod's config file will be used.

!!! warning "Task Priority"
    Each task does expose a configurable priority, however, we recommended that you omit the `Priority` tag and let the mod use the defaults. The default priorities should work for most mobs and changing them may result in undesirable behavior and performance hits.

## AntiAir

The `AntiAir` task will pull a player down from the air when the mob sees them.

key | type | range | default | description
:-|:-|:-|:-|:-
Priority      | int     | N/A   | -4   | task priority
Range         | int     | [1,-) | 128  | range within which a mob will pull the player down
SightRequired | boolean | N/A   | true | does the mob need to have sight of the player to pull them down
MotionY       | double  | (-,0] | -0.4 | the Y axis force applied to the player

```js
{
  "zombie_antiair": {
    "id": "minecraft:zombie",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "AntiAir": {
              "Priority": -4,
              "Range": 128,
              "SightRequired": true,
              "MotionY": -0.4
            }
          }
        }
      }
    }
  }
}
```

The mod's config file contains additional options to:

* set the number of ticks that the player is allowed to be in the air, and
* toggle summing the downward force of all affecting mobs or using only the single largest force. 

## AttackMelee

The `AttackMelee` task allows a passive mob to attack.

!!! note
    This task will only work with mobs that can be given an attack target. For example, attaching this task to a Pig will do nothing because the Pig does not naturally target entities to attack. This task will work with mobs spawned by the invasion system because they are given the ability to persistently target the invaded player.
    
key | type | range | default | description
:-|:-|:-|:-|:-
Priority      | int     | N/A   | -3 | task priority
Speed         | double  | [0,-) | 1  | movement speed when attacking
AttackDamage  | int     | [0,-) | 1  | attack damage in half-hearts

```js
{
  "pig_attackmelee": {
    "id": "minecraft:pig",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "AttackMelee": {
              "Priority": -3,
              "Speed": 1,
              "AttackDamage": 1.0
            }
          }
        }
      }
    }
  }
}
```

## ChaseLongDistance

The `ChaseLongDistance` task operates in tandem with the TargetPlayer task to allow mobs to target the invaded player over a long distance. This task will do nothing without the TargetPlayer task because the mob will never have a long-distance target. 

!!! warning
    This task is automatically applied to mobs spawned by an invasion and doesn't need to be manually applied unless you want to change the task's default properties. This task can be applied manually along with the `TargetPlayer` task for testing purposes.
    
 key | type | range | default | description
 :-|:-|:-|:-|:-
 Priority      | int     | N/A   | -10 | task priority
 Speed         | double  | [0,-) | 1   | movement speed when out of range

```js
{
  "zombie_chaselongdistance": {
    "id": "minecraft:zombie",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "ChaseLongDistance": {
              "Priority": -10,
              "Speed": 1
            }
          }
        }
      }
    }
  }
}
```

## CounterAttack

The `CounterAttack` task will cause a mob to leap toward their target when hit with an attack.

key | type | range | default | description
:-|:-|:-|:-|:-
Priority      | int     | N/A     | -2   | task priority
LeapMotionXZ  | double  | [0,-)   | 0.4  | motion on the XZ plane when leaping
LeapMotionY   | double  | [0,-)   | 0.4  | vertical motion when leaping
Chance        | double  | [0,1]   | 1    | the chance of counterattacking per tick after being attacked
RangeMin      | double  | [0,max) | 2    | minimum range required to counterattack
RangeMax      | double  | (min,-) | 4    | maximum range required to counterattack

!!! note
    The `Chance` parameter will not prevent the task from executing, but it may delay or vary the timing of its execution.

```js
{
  "zombie_counterattack": {
    "id": "minecraft:zombie",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "CounterAttack": {
              "Priority": -2,
              "LeapMotionXZ": 0.4,
              "LeapMotionY": 0.4,
              "Chance": 1,
              "RangeMin": 2,
              "RangeMax": 4
            }
          }
        }
      }
    }
  }
}
```

## ExplodeWhenStuck

The `ExplodeWhenStuck` task will cause a mob to explode when it no longer has a path to its target.

key | type | range | default | description
:-|:-|:-|:-|:-
Priority            | int     | N/A     | -5    | task priority
SightRequired       | boolean | N/A     | false | does the mob need to see its target to explode
RangeRequired       | boolean | N/A     | true  | does the mob need to be within the given range parameters to explode
RangeMin            | double  | [0,max) | 2     | minimum range required to explode
RangeMax            | double  | (min,-) | 16    | maximum range required to explode
ExplosionDelayTicks | int     | [0,-)   | 60    | explosion delay in ticks
ExplosionStrength   | double  | [1,-)   | 3     | explosion strength
ExplosionCausesFire | boolean | N/A     | false | does the explosion cause fires
ExplosionDamaging   | boolean | N/A     | true  | does the explosion break blocks

```js
{
  "zombie_explodewhenstuck": {
    "id": "minecraft:zombie",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "ExplodeWhenStuck": {
              "Priority": -5,
              "SightRequired": false,
              "RangeRequired": true,
              "RangeMin": 1,
              "RangeMax": 16,
              "ExplosionDelayTicks": 60,
              "ExplosionStrength": 3,
              "ExplosionCausesFire": false,
              "ExplosionDamaging": true
            }
          }
        }
      }
    }
  }
}
```

## Lunge

The `Lunge` task will increase a mob's speed when it gets within range of its target.

key | type | range | default | description
:-|:-|:-|:-|:-
Priority      | int    | N/A   | -15 | task priority
Range         | int    | [1,-) | 6   | the range within which a mob will increase its speed
SpeedModifier | double | [0,-) | 0.3 | multiplicative speed modifier

```js
{
  "zombie_lunge": {
    "id": "minecraft:zombie",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "Lunge": {
              "Priority": -15,
              "Range": 6,
              "SpeedModifier": 0.3
            }
          }
        }
      }
    }
  }
}
```

## Mining

The `Mining` task allows a mob to break blocks to reach its target.

key | type | range | default | description
:-|:-|:-|:-|:-
Priority      | int    | N/A   | -9 | task priority
Range         | int    | [1,-) | 4  | the range within which a mob will break blocks
SpeedModifier | double | [0,-) | 1  | multiplicative mining speed modifier

```js
{
  "zombie_mining": {
    "id": "minecraft:zombie",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "Mining": {
              "Priority": -9,
              "Range": 4,
              "SpeedModifier": 1
            }
          }
        }
      }
    }
  }
}
```

## TargetPlayer

The `TargetPlayer` task will cause a mob to persistently target the given player by UUID.

!!! warning
    This task is automatically applied to mobs spawned by an invasion and should not be manually applied. This task can be applied manually along with the `ChaseLongDistance` task for testing purposes.

key | type | range | default | description
:-|:-|:-|:-|:-
Priority | int    | N/A | 10  | task priority
UUID     | string | N/A | N/A | the UUID of the player to persistently target

```js
  "zombie_targetplayer": {
    "id": "minecraft:zombie",
    "nbt": {
      "ForgeData": {
        "Onslaught": {
          "CustomAI": {
            "TargetPlayer": {
              "Priority": 10,
              "UUID": "46562dd7-ada9-4af8-b88c-3a0f2d3e8860"
            }
          }
        }
      }
    }
  }
}
```