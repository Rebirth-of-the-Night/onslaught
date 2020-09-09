package com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

import java.util.function.IntSupplier;

/**
 * Responsible for supplying a player's current dimension id.
 */
public class DimensionSupplier
    implements IntSupplier {

  private final EntityPlayer player;

  public DimensionSupplier(EntityPlayer player) {

    this.player = player;
  }

  @Override
  public int getAsInt() {

    World world = this.player.world;
    WorldProvider provider = world.provider;
    return provider.getDimension();
  }
}
