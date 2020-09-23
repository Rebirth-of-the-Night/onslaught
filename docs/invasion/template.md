# Invasion Template

Invasion templates are `.json` files located in `config/onslaught/templates/invasion`. Sub-folders are not detected and all template files must be placed in the root of this folder.

Each file can contain as many invasion definitions as you like so long as the id of each definition is unique across all files.


## Definitions

key | type | description
:-|:-|:-
name     | string   | defines the name of the invasion to show on progress bars
selector | Selector | defines the selector logic to use when selecting an invasion
messages | Messages | defines the messages sent to an invaded player
commands | Commands | defines the commands executed by the invasion
waves    | Waves[]  | defines the waves spawned by the invasion

```js
{
    "unique_invasion_id": {
        "name": "Invasion Name",
        "selector": {
            ...
        },
        "messages": {
            ...
        },
        "commands": {
            ...
        },
        "waves": [
            ...
        ]
    }
}
```

The `unique_invasion_id` key denotes a unique name for the invasion definition and can be anything you like so long as it is unique across all invasion template files. This name is used to reference the invasion definition in Onslaught's `ostart` command. 

The `name` key is optional and if omitted, defaults to `""` which will prevent the name from being displayed on the progress bar.

## Selector

The `selector` object defines the logic used to select an invasion for a player.

key | type | description
:-|:-|:-
dimension  | Dimension  | defines the allowed or disallowed dimension ids
gamestages | Stages | defines the gamestage matching logic
weight     | int | defines the invasion selector's weight

```js
{
    "unique_invasion_id": {
        "selector": {
            "dimension": {
                ...
            },
            "gamestages": {
                ...
            },
            "weight": 100
        }
    }
}
```

### Dimension

Invasions can be selectively allowed or disallowed in dimensions using the `dimension` selector.

key | type | range | description
:-|:-|:-|:-
type            | string  | "include", "exclude" | whether to allow or disallow the listed dimensions
dimensions      | int[]   | N/A | the dimension id list

```js
{
    "unique_invasion_id": {
        "selector": {
            "dimension": {
                "type": "include",
                "dimensions": [0]
            }
        }
    }
}
```

### GameStages

The `gamestages` object is defined using nested sets of `Stages` objects. `Stages` objects use the keys `and`, `or`, and `not` to define their type.

Each `Stages` object can have only one key.

```js
{
    "unique_invasion_id": {
        "selector": {
            "gamestages": {
                "and": [
                    "stage0",
                    {"or": ["stage1", "stage2"]},
                    {"not": "stage3"}
                ]
            }
        }
    }
}
```

This example will match a player that:

* has `stage0`, *and*
* has either `stage1` *or* `stage2`, *and*
* does *not* have `stage3`

#### and

The `Stages` object `and` consists of an array of elements that are either a `string` or `Stages` object.

`and` requires all of the elements to evaluate to true in order to evaluate to true.

```js
// "and": [<string|Stages>...]
"and": ["stage0", "stage1"]
```

In this example, the player must have both `stage0` *and* `stage1`.

#### or

The `Stages` object `or` consists of an array of elements that are either a `string` or `Stages` object.

`or` requires at least one of the elements to evaluate to true in order to evaluate to true.

```js
// "or": [<string|Stages>...]
"or": ["stage0", "stage1"]
```

In this example, the player must have either `stage0` *or* `stage1`.

#### not

The `Stages` object `not` is different from the others in that it does not use an array. It consists of either a single `string` or `Stages` object.

`not` requires its value to evaluate to false in order to evaluate to true.

```js
// "not": <string|Stages>
"not": "stage0"
```

In this example, the player must not have `stage0`.

#### Examples

**Example A:**

```js
{
    "unique_invasion_id": {
        "selector": {
            "gamestages": {
                "and": [
                    "stage0",
                    {"or": ["stage1", "stage2"]}
                ]
            }
        }
    }
}
```

This example will match a player that:

* has `stage0`, *and*
* has either `stage1` *or* `stage2`

**Example B:**

```js
{
    "unique_invasion_id": {
        "selector": {
            "gamestages": {
                "or": [
                    "stage0",
                    {"and": ["stage1", "stage2"]}
                ]
            }
        }
    }
}
```

This example will match a player that:

* has `stage0`, *or*
* has either `stage1` *and* `stage2`

**Example C:**

```js
{
    "unique_invasion_id": {
        "selector": {
            "gamestages": {
                "not": "stage0"
            }
        }
    }
}
```

This example will match a player that:

* does *not* have `stage0`

**Example D:**

```js
{
    "unique_invasion_id": {
        "selector": {
            "gamestages": {
                "and": [
                    "stage0",
                    {"or": ["stage1", {"not": "stage2"}]},
                    {"and": ["stage3", {"or": ["stage4", "not": "stage5"]}]}
                ]
            }
        }
    }
}
```

This example will match a player that:

* has `stage0`, *and*
* has either `stage1` *or* does *not* have `stage2`, *and*
* has `stage3`, *and*
    * has either `stage4` *or* does *not* have `stage5`

**Example E:**

```js
{
    "unique_invasion_id": {
        "selector": {
            "gamestages": {
                "not": {
                    "or": ["stage0", "stage1", "stage2"]
                }
            }
        }
    }
}
```

This example will match a player that:

* does not have *any* of the given stages

!!! note
    In this example the player must have at least one of the given stages to fail the evaluation.

**Example F:**

```js
{
    "unique_invasion_id": {
        "selector": {
            "gamestages": {
                "not": {
                    "and": ["stage0", "stage1", "stage2"]
                }
            }
        }
    }
}
```

This example will match a player that:

* does not have *all* of the given stages

!!! note
    In this example the player must have all of the given stages to fail the evaluation. If they have just one or two of the stages, the selector will evaluate to true.

### Weight

If an invasion's `dimension` and `gamestages` selectors allow the invasion to be selected, it is placed into a collection with all the other allowed invasions. One invasion is randomly selected from the collection and invasions with a larger weight will be selected more often, relative to the total weight of all invasions in the collection.

```js
{
    "unique_invasion_id": {
        "selector": {
            "weight": 100
        }
    }
}
```

## Messages

Messages that can be sent to an invaded player are defined in the `messages` object.

key | type | description
:-|:-|:-
start | string  | defines the message sent to a player when their invasion starts
end   | string  | defines the message sent to a player when their invasion ends
warn  | Warning | defines the early warning message sent to a player before their invasion starts

```js
{
    "unique_invasion_id": {
        "messages": {
            "start": "Zombies appear!",
            "end": "The threat has been neutralized!",
            "warn": {
                "message": "You can smell Zombies!",
                "ticks": 12000
            }
        }
    }
}
```

### Warning

An early warning message can be sent to a player long before their invasion starts.

!!! note
    Due to the way the invasion system works, early warning messages can only be sent up to 12000 ticks, or 10 minutes, early.

key | type | range | description
:-|:-|:-|:-
message    | string  | N/A | the early warning message sent to a player before their invasion starts
ticks      | int   | [0, 12000] | how many ticks before the invasion starts should the message be sent

&nbsp;

## Commands

!!! note
    All commands are executed as if they were executed by the invaded player with an elevated permission level. This allows the usage of things like `@p` to reference the player and relative coordinates like `~ ~10 ~`.

key | type | description
:-|:-|:-
start | string[] | defines an array of commands to be executed when a player's invasion starts
end   | string[] | defines an array of commands to be executed when a player's invasion ends
staged  | StagedMessage[] | defines an array of `StagedMessage` definitions

```js
{
    "unique_invasion_id": {
        "commands": {
            "start": [
                "/say My invasion is starting!"
            ],
            "end": [
                "/say My invasion has ended!"
            ],
            "staged": [
                ...
            ]
        }
    }
}
```

#### StagedMessage

!!! note
    Due to the way that the invasion completion percentage is evaluated and the command executor is triggered, commands with a `complete` value of `0` will not be executed until an invasion mob dies. To run commands at the beginning of an invasion, use the `start` definition instead.

!!! warning
    You can define up to a maximum of 64 different `StagedMessage` definitions in the `invasion/commands/staged` array.

key | type | range | description
:-|:-|:-|:-
complete    | float  | [0, 1] | at what percentage complete should these commands be run
commands    | string[]   | N/A | defines an array of commands to be executed

```js
{
    "complete": 0.25,
    "commands": [
        "/say My invasion is 25% complete!"
    ]
}
```

## Waves

Waves define the mobs that will be spawned in the invasion.

Each wave listed in the `waves` definition will be spawned sequentially.

```js
{
    "unique_invasion_id": {
        "waves": [
            ...
        ]
    }
}
```

#### Wave

The `Wave` object defines the mobs that will spawn in the wave and how the spawner will try to spawn them.

Each wave can be delayed from the start of the invasion. If the player hasn't defeated all previous waves by the time that the wave's delay expires, the wave will be spawned. The wave will also be spawned immediately after a player defeats all previous waves.

key | type | optional | default| description
:-|:-|:-|:-|:-
delayTicks   | int[min, max] *or* int[fixed] | yes | `[0]` | defines how long to wait to spawn this wave after the invasions starts
groups       | Group[]       | no  | - | defines an array of groups, one will be selected
secondaryMob | SecondaryMob  | yes | config | defines a secondary mob to spawn when the primary mob spawn fails

```js
{
    "delayTicks": [3600, 4000],
    "groups": [
        ...
    ],
    "secondaryMob": {
        ...
    }
}
```

#### Group

One group is randomly selected from the collection and groups with a larger weight will be selected more often, relative to the total weight of all groups in the collection.

Enabling `forceSpawn` will try to spawn the mob using the delayed spawn system if the primary spawn type fails. The delayed spawn system will ignore the light level when attempting spawns.

key | type | optional | default| description
:-|:-|:-|:-|:-
weight     | int     | yes | `1` | defines the group's weight used for selection
forceSpawn | boolean | yes | config | should the mob try to be force-spawned in the light if it fails to spawn in defined light levels
mobs       | Mob[]   | no | - | all mobs defined here will be spawned for the wave

```js
{
    "weight": 100,
    "forceSpawn": true,
    "mobs": [
        ...
    ]
}
```

#### Mob

This defines the mob template to use, the number of this mob to spawn, and how to try and spawn it.

key | type | optional | default| description
:-|:-|:-|:-|:-
id    | int                         | no  | - | defines the mob template id
count | int[min, max] *or* int[fixed] | yes | `[1]` | defines how many of this mob should spawn
spawn | SpawnType                   | yes | config | defines how to try and spawn the mob

```js
{
    "id": "invasion_zombie",
    "count": [8, 16],
    "spawn": {
        ...
    }
}
```

#### SpawnType

This defines how the spawner will try to spawn a mob.

key | type | optional | default| description
:-|:-|:-|:-|:-
type    | string             | yes | config | defines the spawn type, value values are `ground` and `air`
light   | int[min, max]      | yes | config | defines the light range to spawn the mob in
rangeXZ | int[min, max]      | yes | config | defines how far away from the player to try and spawn the mob
rangeY  | int                | yes | config | defines how far +/- Y to try and spawn the mob
stepRadius     | int         | yes | config | defines the step radius for the spawn sampler
sampleDistance | int         | yes | config | defines the sample distance for the spawn sampler

```js
"spawn": {
    "type": "ground",
    "light": [0, 7],
    "rangeXZ": [16, 32],
    "rangeY": 16,
    "stepRadius": 4,
    "sampleDistance": 2
}
```

#### SecondaryMob

This defines a mob template to try spawning if the primary spawn fails.

key | type | optional | default| description
:-|:-|:-|:-|:-
id    | int                         | yes | config | defines the mob template id
spawn | SpawnType                   | yes | config | defines how to try and spawn the mob

```js
"secondaryMob": {
    "id": "invasion_vex",
    "spawn": {
        ...
    }
}
```