package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Responsible for allowing an entity to break blocks to get to their target. */
public class EntityAIMining extends EntityAIBase {

  private final EntityLiving taskOwner;
  private final int rangeSq;
  private final double speedModifier;
  private final float defaultSpeed;

  private EntityLivingBase attackTarget;
  private BlockPos blockTarget;
  private int blockBreakTickCounter;

  public EntityAIMining(EntityLiving taskOwner, int range, double speedModifier) {

    this.taskOwner = taskOwner;
    this.rangeSq = range * range;
    this.speedModifier = speedModifier;
    this.defaultSpeed = 1;
    this.setMutexBits(0);
  }

  @Override
  public boolean shouldExecute() {

    this.attackTarget = this.taskOwner.getAttackTarget();

    if (this.attackTarget == null) {
      return false;

    } else if (!this.attackTarget.isEntityAlive()) {
      return false;

    } else if (!this.taskOwner.getNavigator().noPath()) {
      return false;

    } else if (this.taskOwner.getDistanceSq(this.attackTarget) < 1) {
      return false;
    }

    this.blockTarget = this.getBlockTarget();

    return (this.blockTarget != null);
  }

  @Override
  public void startExecuting() {

    this.taskOwner.getNavigator().clearPath();
  }

  @Override
  public void resetTask() {

    if (this.blockTarget != null) {
      this.taskOwner.world.sendBlockBreakProgress(
          this.taskOwner.getEntityId(), this.blockTarget, -1);
    }

    this.blockBreakTickCounter = 0;
    this.blockTarget = null;
  }

  @Override
  public boolean shouldContinueExecuting() {

    BlockPos blockTarget = this.getBlockTarget();

    if (blockTarget == null) {
      return false;

    } else if (!blockTarget.equals(this.blockTarget)) {
      return false;
    }

    return (this.taskOwner.getDistanceSq(this.blockTarget) <= this.rangeSq);
  }

  @Override
  public void updateTask() {

    this.taskOwner
        .getLookHelper()
        .setLookPosition(
            this.attackTarget.posX,
            this.attackTarget.posY + this.attackTarget.getEyeHeight(),
            this.attackTarget.posZ,
            this.taskOwner.getHorizontalFaceSpeed(),
            this.taskOwner.getVerticalFaceSpeed());

    PathNavigate navigator = this.taskOwner.getNavigator();
    navigator.clearPath();

    World world = this.taskOwner.world;

    if (world.isAirBlock(this.blockTarget)) {
      this.resetTask();
      return;
    }

    this.blockBreakTickCounter += 1;

    IBlockState blockState = world.getBlockState(this.blockTarget);
    float blockStrength =
        this.getBlockStrength(blockState, this.taskOwner, world, this.blockTarget)
            * (this.blockBreakTickCounter + 1);

    if (blockStrength >= 1) {
      boolean canHarvest = this.canHarvest(blockState, this.taskOwner);
      world.destroyBlock(this.blockTarget, canHarvest);
      navigator.setPath(
          navigator.getPathToEntityLiving(this.attackTarget),
          this.taskOwner.getMoveHelper().getSpeed());
      this.resetTask();

    } else if (this.blockBreakTickCounter % 5 == 0) {
      world.playSound(
          null,
          this.blockTarget,
          blockState
              .getBlock()
              .getSoundType(blockState, world, this.blockTarget, this.taskOwner)
              .getHitSound(),
          SoundCategory.BLOCKS,
          1,
          1);
      this.taskOwner.swingArm(EnumHand.MAIN_HAND);
      world.sendBlockBreakProgress(
          this.taskOwner.getEntityId(), this.blockTarget, (int) (blockStrength * 10));
    }
  }

  @Nullable
  private BlockPos getBlockTarget() {

    World world = this.taskOwner.world;
    Vec3d origin = new Vec3d(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ);
    Vec3d target =
        new Vec3d(this.attackTarget.posX, this.attackTarget.posY, this.attackTarget.posZ);

    Vec3d center = target.subtract(origin).normalize().scale(1);
    AxisAlignedBB boundingBox = this.taskOwner.getEntityBoundingBox().offset(center).grow(0.5);
    List<AxisAlignedBB> collisionBoxes = world.getCollisionBoxes(this.taskOwner, boundingBox);

    // Sort the boxes into an ordered list, closest to farthest. This will
    // ensure that the mob mines the closest blocks first.
    collisionBoxes.sort(
        (b1, b2) -> {
          double x1 = b1.minX + (b1.maxX - b1.minX) * 0.5;
          double y1 = b1.minY + (b1.maxY - b1.minY) * 0.5;
          double z1 = b1.minZ + (b1.maxZ - b1.minZ) * 0.5;

          double x2 = b2.minX + (b2.maxX - b2.minX) * 0.5;
          double y2 = b2.minY + (b2.maxY - b2.minY) * 0.5;
          double z2 = b2.minZ + (b2.maxZ - b2.minZ) * 0.5;

          double dx1 = x1 - center.x;
          double dy1 = y1 - center.y;
          double dz1 = z1 - center.z;

          double dx2 = x2 - center.x;
          double dy2 = y2 - center.y;
          double dz2 = z2 - center.z;

          double d1 = dx1 * dx1 + dy1 * dy1 + dz1 * dz1;
          double d2 = dx2 * dx2 + dy2 * dy2 + dz2 * dz2;

          return Double.compare(d1, d2);
        });

    for (AxisAlignedBB collisionBox : collisionBoxes) {
      BlockPos pos = new BlockPos(collisionBox.getCenter());

      if (!world.isAirBlock(pos)) {

        // If the target is above the mob, don't mine the block at the same
        // level as the mob's feet. This ensures that the logic will try to
        // stair up to the target.
        if (this.taskOwner.posY < this.attackTarget.posY && this.taskOwner.posY >= pos.getY()) {
          continue;
        }

        if (this.taskOwner.getDistanceSq(pos) > this.rangeSq) {
          continue;
        }

        return pos;
      }
    }

    return null;
  }

  /**
   * This is derived from the linked method and altered to support non-player entities.
   *
   * @see net.minecraftforge.common.ForgeHooks#blockStrength(IBlockState, EntityPlayer, World,
   *     BlockPos)
   */
  private float getBlockStrength(
      IBlockState blockState, EntityLivingBase entity, World world, BlockPos pos) {

    final float hardness = blockState.getBlockHardness(world, pos);

    if (hardness <= 0f) {
      return 0;
    }

    return this.getMiningSpeed(entity, blockState)
        / hardness
        / (this.canHarvest(blockState, entity) ? 30f : 100f);
  }

  /**
   * This is derived from the linked method and altered to support non-player entities.
   *
   * @see net.minecraftforge.common.ForgeHooks#canHarvestBlock(Block, EntityPlayer, IBlockAccess,
   *     BlockPos)
   */
  private boolean canHarvest(IBlockState state, EntityLivingBase entity) {

    Block block = state.getBlock();

    if (state.getMaterial().isToolNotRequired()) {
      return true;
    }

    ItemStack stack = entity.getHeldItemMainhand();
    String tool = block.getHarvestTool(state);

    if (stack.isEmpty() || tool == null) {
      return false;
    }

    int toolLevel = stack.getItem().getHarvestLevel(stack, tool, null, state);

    if (toolLevel < 0) {
      return stack.canHarvestBlock(state);
    }

    return toolLevel >= block.getHarvestLevel(state);
  }

  /**
   * This is derived from the linked method and altered to support non-player entities.
   *
   * @see net.minecraft.entity.player.EntityPlayer#getDigSpeed(IBlockState, BlockPos)
   */
  private float getMiningSpeed(EntityLivingBase entity, IBlockState blockState) {

    ItemStack heldItemMainHand = entity.getHeldItemMainhand();
    float f =
        heldItemMainHand.isEmpty()
            ? this.defaultSpeed
            : heldItemMainHand.getDestroySpeed(blockState);

    f *= this.speedModifier;

    if (f > 1) {
      int i = EnchantmentHelper.getEfficiencyModifier(entity);

      if (i > 0 && !heldItemMainHand.isEmpty()) {
        f += (float) (i * i + 1);
      }
    }

    if (entity.isPotionActive(MobEffects.HASTE)) {
      // We just checked if the potion is active, so this shouldn't be null
      //noinspection ConstantConditions
      f *= 1 + (float) (entity.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2f;
    }

    if (entity.isPotionActive(MobEffects.MINING_FATIGUE)) {
      float f1;

      // Again, we just checked if the potion is active...
      //noinspection ConstantConditions
      switch (entity.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
        case 0:
          f1 = 0.3f;
          break;
        case 1:
          f1 = 0.09f;
          break;
        case 2:
          f1 = 0.0027f;
          break;
        case 3:
        default:
          f1 = 8.1E-4f;
      }

      f *= f1;
    }

    if (entity.isInsideOfMaterial(Material.WATER)
        && !EnchantmentHelper.getAquaAffinityModifier(entity)) {
      f /= 5.0f;
    }

    if (!entity.onGround) {
      f /= 5.0f;
    }

    return (f < 0 ? 0 : f);
  }
}
