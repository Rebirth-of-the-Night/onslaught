#!/bin/bash

INTERACTIVE=0

should_continue() {
    if [[ INTERACTIVE == 1 ]]; then
        read -p "   [??] Continue [y/N]? " -n 1 -r
        echo ""
        case $REPLY in
            y)
                ;;
            *)
                echo "   [!!] Process halted by user"
                exit 1
        esac
    fi
}

YEAR=$(date +'%Y')
USE_LIB=0
USE_LICENSE=0
USE_README=0

read -p '   [??] Mod id: ' MOD_ID
read -p '   [??] Mod name: ' MOD_NAME
read -p '   [??] Mod description: ' MOD_DESCRIPTION
read -p '   [??] Mod classname: ' MOD_CLASSNAME
read -p '   [??] Public repo: ' PUBLIC_REPO
read -p '   [??] Private repo: ' PRIVATE_REPO
read -p "   [??] Use Athenaeum [y/N]? " -n 1 -r
echo ""
case $REPLY in
    y)
        USE_LIB=1
        ;;
    *)
        ;;
esac
read -p "   [??] Use LICENSE [y/N]? " -n 1 -r
echo ""
case $REPLY in
    y)
        USE_LICENSE=1
        ;;
    *)
        ;;
esac
read -p "   [??] Use README.md [y/N]? " -n 1 -r
echo ""
case $REPLY in
    y)
        USE_README=1
        ;;
    *)
        ;;
esac
echo ""
echo "   [--] Create the asset folders"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/advancements"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/blockstates"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/lang"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/models/block"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/models/item"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/recipes"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/sounds"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/textures/blocks"
echo "     mkdir -p -v src/main/resources/assets/${MOD_ID}/textures/items"
echo "   [--] Rename the mod package"
echo "     mv -v src/main/java/com/codetaylor/mc/@@MOD_ID@@ src/main/java/com/codetaylor/mc/${MOD_ID}"

if [[ $USE_LIB == 0 ]]; then
    echo "   [--] Clear the settings.gradle file"
    echo "     rm -v settings.gradle && touch settings.gradle"
    echo "   [--] Clear the lib dependency from the project gradle"
    echo "     sed -i 's/@@LIB@@//g' project.gradle"
    echo "   [--] Replace the mod template file with the stand-alone version"
    echo "     rm -v src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java"
    echo "     mv -v src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate_StandAlone.java src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java"
else
    echo "   [--] Add the lib dependency to the project gradle"
    echo "     sed -i 's/@@LIB@@/compile project(\":athenaeum\")/g' project.gradle"
    echo "   [--] Remove the mod template file stand-alone version"
    echo "     rm -v src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate_StandAlone.java"
fi

echo "   [--] Rename the mod file"
echo "     mv -v src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java src/main/java/com/codetaylor/mc/${MOD_ID}/Mod${MOD_CLASSNAME}.java"
echo "   [--] Replace the mod classname in the mod file"
echo "     sed -i 's/ModTemplate/Mod${MOD_CLASSNAME}/g' src/main/java/com/codetaylor/mc/${MOD_ID}/Mod${MOD_CLASSNAME}.java"
if [[ $USE_LICENSE == 0 ]]; then
    echo "   [--] Remove the license"
    echo "     rm -v LICENSE"
else
    echo "   [--] Replace the year in the license"
    echo "     sed -i 's/@@YEAR@@/${YEAR}/g' LICENSE"
fi
if [[ $USE_README == 0 ]]; then
    echo "   [--] Remove the readme"
    echo "     rm -v README.md"
fi
echo "   [--] Replace the mod id"
echo "     sed -i 's/@@MOD_ID@@/${MOD_ID}/g' project.gradle"
echo "     sed -i 's/@@MOD_ID@@/${MOD_ID}/g' src/main/resources/mcmod.info"
echo "     sed -i 's/@@MOD_ID@@/${MOD_ID}/g' src/main/resources/pack.mcmeta"
echo "     sed -i 's/@@MOD_ID@@/${MOD_ID}/g' src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java"
echo "   [--] Replace the mod description"
echo "     sed -i 's/@@MOD_DESCRIPTION@@/${MOD_DESCRIPTION}/g' src/main/resources/mcmod.info"
echo "   [--] Replace the mod name"
echo "     sed -i 's/@@MOD_NAME@@/${MOD_NAME}/g' src/main/resources/mcmod.info"
echo "     sed -i 's/@@MOD_NAME@@/${MOD_NAME}/g' src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java"
echo "   [--] Initialize git"
echo "     rm -rf .git"
echo "     git init"
if [[ -n $PUBLIC_REPO ]]; then
    echo "     git remote add public ${PUBLIC_REPO}"
    echo "     git push -u public master"
fi
echo "     git checkout -b develop"
echo "     git add -A"
echo "     git commit -m \"Initial commit\""
echo "     git tag -a 1.12.2-0.0.0 -m \"version 1.12.2-0.0.0\""
if [[ -n $PRIVATE_REPO ]]; then
    echo "     git remote add private ${PRIVATE_REPO}"
    echo "     git push -u private develop"
fi
echo "   [--] Initialize workspace"
echo "     ./gradlew :setupDecompWorkspace :idea :genIntellijRuns"
echo ""
read -p "   [??] Does this look right to you [y/N]? " -n 1 -r
echo ""
case $REPLY in
    y)
        echo "   [OK] Process validated by user"
        ;;
    *)
        echo "   [!!] Process halted by user"
        exit 1
esac

# Make the asset folders
echo "[>>] Creating the asset folders"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/advancements"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/blockstates"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/lang"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/models/block"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/models/item"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/recipes"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/sounds"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/textures/blocks"
mkdir -p -v "src/main/resources/assets/${MOD_ID}/textures/items"
should_continue

# Rename the mod package
echo "[>>] Renaming the mod package"
mv -v src/main/java/com/codetaylor/mc/@@MOD_ID@@ "src/main/java/com/codetaylor/mc/${MOD_ID}"
should_continue

# Handle lib dependency
echo "[>>] Configure lib dependency"
if [[ $USE_LIB == 0 ]]; then
    # Clear the settings.gradle file
    rm -v settings.gradle && touch settings.gradle
    # Clear the lib dependency from the project gradle
    sed -i 's/@@LIB@@//g' project.gradle
    # Replace the mod template file with the stand-alone version
    rm -v "src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java"
    mv -v "src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate_StandAlone.java" "src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java"
else
    # Add the lib dependency to the project gradle
    sed -i 's/@@LIB@@/compile project(":athenaeum")/g' project.gradle
    # Remove the mod template file stand-alone version
    rm -v "src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate_StandAlone.java"
fi
should_continue

# Rename the mod file
echo "[>>] Renaming the mod file"
mv -v "src/main/java/com/codetaylor/mc/${MOD_ID}/ModTemplate.java" "src/main/java/com/codetaylor/mc/${MOD_ID}/Mod${MOD_CLASSNAME}.java"
should_continue

# Replace the mod classname in the mod file
echo "[>>] Replacing the mod classname in the mod file"
sed -i "s/ModTemplate/Mod${MOD_CLASSNAME}/g" "src/main/java/com/codetaylor/mc/${MOD_ID}/Mod${MOD_CLASSNAME}.java"
should_continue

if [[ $USE_LICENSE == 0 ]]; then
    # Remove license file
    echo "[>>] Remove the license"
    rm -v LICENSE
else
    # Replace the year
    echo "[>>] Replacing the year"
    sed -i "s/@@YEAR@@/${YEAR}/g" LICENSE
    should_continue
fi

if [[ $USE_README == 0 ]]; then
    # Remove readme file
    echo "[>>] Remove the readme"
    rm -v README.md
fi

# Replace the mod id
echo "[>>] Replacing the mod id"
sed -i "s/@@MOD_ID@@/${MOD_ID}/g" project.gradle
sed -i "s/@@MOD_ID@@/${MOD_ID}/g" src/main/resources/mcmod.info
sed -i "s/@@MOD_ID@@/${MOD_ID}/g" src/main/resources/pack.mcmeta
sed -i "s/@@MOD_ID@@/${MOD_ID}/g" "src/main/java/com/codetaylor/mc/${MOD_ID}/Mod${MOD_CLASSNAME}.java"
should_continue

# Replace the mod description
echo "[>>] Replacing the mod description"
sed -i "s/@@MOD_DESCRIPTION@@/${MOD_DESCRIPTION}/g" src/main/resources/mcmod.info
should_continue

# Replace the mod name
echo "[>>] Replacing the mod name"
sed -i "s/@@MOD_NAME@@/${MOD_NAME}/g" src/main/resources/mcmod.info
sed -i "s/@@MOD_NAME@@/${MOD_NAME}/g" "src/main/java/com/codetaylor/mc/${MOD_ID}/Mod${MOD_CLASSNAME}.java"
should_continue

# Initialize git
echo "[>>] Initializing git"
rm -rf .git
git init
if [[ -n $PUBLIC_REPO ]]; then
    git remote add public "${PUBLIC_REPO}"
    git push -u public master
fi
git checkout -b develop
git add -A
git commit -m "Initial commit"
git tag -a 1.12.2-0.0.0 -m "version 1.12.2-0.0.0"
if [[ -n $PRIVATE_REPO ]]; then
    git remote add private "${PRIVATE_REPO}"
    git push -u private develop
fi
should_continue

# Initialize workspace
echo "[>>] Initializing workspace"
./gradlew :setupDecompWorkspace :idea :genIntellijRuns