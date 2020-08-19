package com.codetaylor.mc.onslaught.modules.onslaught.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIPlayerTarget;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;

import java.util.UUID;

/**
 * Responsible for injecting the AI player target task into entities with the tag.
 */
public class EntityAIPlayerTargetInjector
    extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_TARGET_PLAYER)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_TARGET_PLAYER);

    if (!aiTag.hasKey(Tag.AI_PARAM_UUID)) {
      return;
    }

    String playerUUIDString = aiTag.getString(Tag.AI_PARAM_UUID);
    UUID playerUUID = UUID.fromString(playerUUIDString);

    int priority = this.getPriority(aiTag, EntityAIPlayerTarget.DEFAULT_PRIORITY);

    entity.targetTasks.addTask(priority, new EntityAIPlayerTarget(entity, () -> {
      WorldServer world = (WorldServer) entity.world;
      MinecraftServer minecraftServer = world.getMinecraftServer();

      if (minecraftServer == null) {
        return null;
      }

      PlayerList playerList = minecraftServer.getPlayerList();
      return playerList.getPlayerByUUID(playerUUID);
    }));
  }
}