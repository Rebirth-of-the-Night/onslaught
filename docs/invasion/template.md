# Invasion Template

Invasion templates are `.json` files located in `config/onslaught/templates/invasion`. Subfolders are not detected and all template files must be placed in the root of this folder.

Each file can contain as many invasion definitions as you like so long as the id of each definition is unique across all files.


## Definitions

key | type | description
:-|:-|:-
selector | Selector | defines the selector logic to use when selecting an invasion
messages | Messages | defines the messages sent to an invaded player
commands | Commands | defines the commands executed by the invasion
waves    | Waves    | defines the waves spawned by the invasion

```js
{
    "unique_invasion_id": {
        "selector": {
            ...
        },
        "messages": {
            ...
        },
        "commands": {
            ...
        },
        "waves": {
            ...
        }
    }
}
```

The `unique_invasion_id` key denotes a unique name for the invasion definition and can be anything you like so long as it is unique across all invasion template files. This name is used to reference the invasion definition in Onslaught's `ostart` command. 

## Selector

The `selector` object defines the logic used to select an invasion for a player.

key | type | description
:-|:-|:-
dimension  | Dimension  | defines the allowed or disallowed dimension ids
gamestages | GameStages | defines the gamestage matching logic
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

// TODO

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

// TODO

## Commands

// TODO

## Waves

// TODO