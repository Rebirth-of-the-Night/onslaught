# Changelog

## 1.0.0

* **Added:**
    * Invasion templates
        * Selectors
            * Dimension
            * Gamestages
            * Weight
        * Messages
            * Start
            * End
            * Early warning
        * Command execution
            * Start
            * End
            * Staged
        * Waves
            * Groups
            * Mobs
            * Spawn type
            * Secondary Mob
    * Spawn system
    * Invasion system
    * Commands:
        * `ostart` command to start an invasion
        * `ostartrandom` command to start a random invasion
        * `ostop` command to stop an invasion
        * `ostopall` command to stop all invasions
    * Documentation:
        * Commands
        * Invasion templates

## 0.1.0

* **Added:**
    * Mob templates, including:
        * Effects to be applied on spawn
        * Additional loot to be dropped
        * NBT support
    * Custom loot tables and loot table overrides
    * Commands:
        * `osummon` command to summon mobs from the mob templates
        * `oreload` command to reload mob templates
    * Custom AI:
        * AntiAir
        * AttackMelee
        * ChaseLongDistance
        * CounterAttack
        * ExplodeWhenStuck
        * Lunge
        * Mining
        * TargetPlayer
    * Documentation:
        * Changelog
        * Commands
        * Mob templates
        * Custom AI