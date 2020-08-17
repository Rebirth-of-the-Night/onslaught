package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.MethodHandleHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;

import java.lang.invoke.MethodHandle;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Responsible for injecting the AI player target task into entities with the tag.
 */
public class EntityAIPlayerTargetInjector {

  private static final MethodHandle entityLiving$persistenceRequiredSetter;

  static {
    /*
    MC 1.12: net/minecraft/entity/EntityLiving.persistenceRequired
    Name: bA => field_82179_bU => persistenceRequired
    Comment: Whether this entity should NOT despawn.
    Side: BOTH
    AT: public net.minecraft.entity.EntityLiving field_82179_bU # persistenceRequired
     */
    entityLiving$persistenceRequiredSetter = MethodHandleHelper.unreflectSetter(EntityLiving.class, "field_82179_bU");
  }

  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!(entity instanceof EntityCreature)) {
      return;
    }

    if (!tag.hasKey(Tag.AI_PLAYER_TARGET)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_PLAYER_TARGET);

    if (!aiTag.hasKey(Tag.AI_PARAM_UUID)) {
      return;
    }

    String playerUUIDString = aiTag.getString(Tag.AI_PARAM_UUID);
    UUID playerUUID = UUID.fromString(playerUUIDString);
    EntityCreature entityCreature = (EntityCreature) entity;

    entity.targetTasks.addTask(10, new EntityAIPlayerTarget(entityCreature, () -> {
      WorldServer world = (WorldServer) entity.world;
      MinecraftServer minecraftServer = world.getMinecraftServer();

      if (minecraftServer == null) {
        return null;
      }

      PlayerList playerList = minecraftServer.getPlayerList();
      return playerList.getPlayerByUUID(playerUUID);
    }));

    // Ensure that the entity can path away from its home restriction.
    entityCreature.detachHome();

    // Ensure that the entity does not despawn normally.
    try {
      entityLiving$persistenceRequiredSetter.invokeExact((EntityLiving) entityCreature, true);

    } catch (Throwable throwable) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error invoking setter for field_82179_bU");
      ModOnslaught.LOG.log(Level.SEVERE, throwable.getMessage(), throwable);
    }
  }
}