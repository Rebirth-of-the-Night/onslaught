# Onslaught

*Onslaught* is a Minecraft Mod that gives you an interface to create Invasions of violent mobs with custom abilities, loot, and spawning conditions.

## Using the Mod

1. Add the appropriate jar to your modpack in the `./mods` folder. 
2. [read the docs](https://onslaught.readthedocs.io/en/latest/) on how to configure your hordes.

## Development Quick Start

1. Checkout Github Repo to your local
2. Initialize the gradle files:
    * run `gradlew.bat init` for Windows
    * run `./gradlew init` for Mac / Linux / Bash Shell
3. Decompress the workspace:
    * run `./gradlew setupDecompWorkspace`
    
You should be good to build or run the client.
    
## Code Guidance

### Branch Management

1. Check out a new branch `git checkout -b <scope>/<context>`
    * `fix/some-bug`
    * `feat/some-new-feature`
    * `chore/some-toil`
    * `doc/add-some-new-documentation`
2. Commit often, and small
3. Use Pull Requests to deliberately introduce changes into master

## Releasing a new version of the mod

**Prerequisites**

1. Github write access to the repo
2. CurseForge access to the [Onslaught project](https://www.curseforge.com/minecraft/mc-mods/onslaught)

### Release to Github

1. switch to the desired branch (presumably the latest master)
2. run `./version set <minecraft-version>-<mod-version>`
    * Minecraft version is generally `1.12.2`
    * Use [Semantic Name Versioning](https://semver.org/) for the mod
3. Enter your github credentials
4. Amend the [Github Release](https://github.com/Rebirth-of-the-Night/onslaught/releases) with the main and sources jar

### Release to Curse Forge

1. go to [Onslaught project's files](https://www.curseforge.com/minecraft/mc-mods/onslaught/files)
2. click the file upload button in the top right
3. follow the instructions
