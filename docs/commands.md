# Commands

---

## oreload

Reloads mob templates and invasion templates.

Does not reload custom loot tables. To reload loot tables, use the vanilla `/reload` command.

**Syntax**

```
/oreload
```

---

## ostart

Starts a specific invasion for a specific player or N random players.

**Syntax**

```
/ostart <invasion_id> [<player>|<number>]
```

* `invasion_id`: invasion template id
* `player`: player name
* `number`: how many invasions to start for random players

!!! note
    `player` and `number` are mutually exclusive and optional. If omitted, the command will choose the player that ran the command. This command will ignore any selector conditions the invasion may have.

**Examples**

```
/ostart invasion001
/ostart invasion001 Steve
/ostart invasion001 4
```

---

## ostartrandom

Starts a random invasion for a specific player or N random players.

**Syntax**

```
/ostartrandom [<player>|<number>]
```

* `player`: player name
* `number`: how many invasions to start for random players

!!! note
    `player` and `number` are mutually exclusive and optional. If omitted, the command will choose the player that ran the command. This command will respect invasion selectors.

**Examples**

```
/ostartrandom
/ostartrandom Steve
/ostartrandom 4
```

---

## ostop

Stops a specific player's active invasion.

**Syntax**

```
/ostop [<player>]
```

* `player`: player name

!!! note
    `player` is optional. If omitted, the command will choose the player that ran the command.

**Examples**

```
/ostop
/ostop Steve
```

---

## ostopall

Stops all active invasions.

**Syntax**

```
/ostopall
```

**Examples**

```
/ostopall
```

---

## osummon

Summons an entity using the given mob definition.

**Syntax**

```
/osummon <mob_definition> [<pos>]
```

**Examples**

```
/osummon zombie_basic ~ ~ ~
```
