package com.codetaylor.mc.onslaught.modules.onslaught.ai;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class EntityAIMining
    extends EntityAIBase {

  private static final int BLOCK_DIG_RANGE_SQ = 16;
  private static final float BASE_DESTROY_SPEED = 1;

  private final EntityLiving taskOwner;

  private EntityLivingBase attackTarget;
  private BlockPos blockTarget;
  private int blockBreakTickCounter;

  public EntityAIMining(EntityLiving taskOwner) {

    this.taskOwner = taskOwner;
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

    this.updateBlockTarget();

    return (this.blockTarget != null);
  }

  @Override
  public void startExecuting() {

    this.taskOwner.getNavigator().clearPath();
  }

  @Override
  public void resetTask() {

    this.blockBreakTickCounter = 0;
    this.blockTarget = null;
  }

  @Override
  public boolean shouldContinueExecuting() {

    return (this.blockTarget != null)
        && (this.taskOwner.getDistanceSq(this.blockTarget) <= BLOCK_DIG_RANGE_SQ);
  }

  @Override
  public void updateTask() {

    this.taskOwner.getLookHelper().setLookPosition(
        this.attackTarget.posX, this.attackTarget.posY + this.attackTarget.getEyeHeight(), this.attackTarget.posZ,
        this.taskOwner.getHorizontalFaceSpeed(), this.taskOwner.getVerticalFaceSpeed()
    );

    PathNavigate navigator = this.taskOwner.getNavigator();
    navigator.clearPath();

    World world = this.taskOwner.world;

    if (world.isAirBlock(this.blockTarget)) {
      this.resetTask();
      return;
    }

    this.blockBreakTickCounter += 1;

    IBlockState blockState = world.getBlockState(this.blockTarget);
    float blockStrength = this.getBlockStrength(blockState, this.taskOwner, world, this.blockTarget) * (this.blockBreakTickCounter + 1);

    if (blockStrength >= 1) {
      boolean canHarvest = this.canHarvest(blockState, this.taskOwner);
      world.destroyBlock(this.blockTarget, canHarvest);
      navigator.setPath(navigator.getPathToEntityLiving(this.attackTarget), this.taskOwner.getMoveHelper().getSpeed());
      this.resetTask();

    } else if (this.blockBreakTickCounter % 5 == 0) {
      world.playSound(null, this.blockTarget, blockState.getBlock().getSoundType(blockState, world, this.blockTarget, this.taskOwner).getHitSound(), SoundCategory.BLOCKS, 1, 1);
      this.taskOwner.swingArm(EnumHand.MAIN_HAND);
      world.sendBlockBreakProgress(this.taskOwner.getEntityId(), this.blockTarget, (int) (blockStrength * 10));
    }
  }

  private void updateBlockTarget() {

    World world = this.taskOwner.world;
    Vec3d origin = new Vec3d(this.taskOwner.posX, this.taskOwner.posY + 1, this.taskOwner.posZ);
    Vec3d target = new Vec3d(this.attackTarget.posX, this.attackTarget.posY, this.attackTarget.posZ);

    RayTraceResult rayTraceResult = world.rayTraceBlocks(origin, target, false);

    if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
      this.blockTarget = rayTraceResult.getBlockPos();
    }
  }

  /**
   * This is derived from the linked method and altered to support non-player entities.
   *
   * @see net.minecraftforge.common.ForgeHooks#blockStrength(IBlockState, EntityPlayer, World, BlockPos)
   */
  private float getBlockStrength(IBlockState blockState, EntityLivingBase entity, World world, BlockPos pos) {

    final float hardness = blockState.getBlockHardness(world, pos);

    if (hardness <= 0f) {
      return 0;
    }

    return this.getDigSpeed(entity, blockState, pos) / hardness / (this.canHarvest(blockState, entity) ? 30f : 100f);
  }

  /**
   * This is derived from the linked method and altered to support non-player entities.
   *
   * @see net.minecraftforge.common.ForgeHooks#canHarvestBlock(Block, EntityPlayer, IBlockAccess, BlockPos)
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
  private float getDigSpeed(EntityLivingBase entity, IBlockState blockState, BlockPos pos) {

    ItemStack heldItemMainHand = entity.getHeldItemMainhand();
    float f = heldItemMainHand.isEmpty() ? BASE_DESTROY_SPEED : heldItemMainHand.getDestroySpeed(blockState);

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

    if (entity.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(entity)) {
      f /= 5.0f;
    }

    if (!entity.onGround) {
      f /= 5.0f;
    }

    return (f < 0 ? 0 : f);
  }
}
