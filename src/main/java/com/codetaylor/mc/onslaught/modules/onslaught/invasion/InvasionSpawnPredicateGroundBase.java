package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public abstract class InvasionSpawnPredicateGroundBase
    implements Predicate<EntityLiving> {

  protected final BlockPos.MutableBlockPos blockPos;

  public InvasionSpawnPredicateGroundBase() {

    this.blockPos = new BlockPos.MutableBlockPos();
  }

  @Override
  public boolean test(EntityLiving entity) {

    this.blockPos.setPos(entity);

    // Ensure we're testing an air block
    if (!entity.world.isAirBlock(this.blockPos)) {
      return false;
    }

    // Ensure the entity isn't colliding with blocks or fluids
    if (!entity.isNotColliding()) {
      return false;
    }

    // Ensure the block below the entity has a solid top
    this.blockPos.move(EnumFacing.DOWN);

    if (this.blockPos.getY() < 0 || this.blockPos.getY() > 255) {
      return false;
    }

    return entity.world.isSideSolid(this.blockPos, EnumFacing.UP);
  }
}
