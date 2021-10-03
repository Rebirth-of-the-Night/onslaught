package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityAntiAir;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IAntiAirPlayerData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

/** Responsible for pulling players down to the owner's Y level. */
public class EntityAIAntiAir extends EntityAIBase {

  private final EntityLiving taskOwner;
  private final int rangeSq;
  private final boolean sightRequired;
  private final double motionY;

  public EntityAIAntiAir(EntityLiving taskOwner, boolean sightRequired, int range, double motionY) {

    this.taskOwner = taskOwner;
    this.rangeSq = range * range;
    this.sightRequired = sightRequired;
    this.motionY = motionY;
    this.setMutexBits(0);
  }

  @Override
  public boolean shouldExecute() {

    return true;
  }

  @Override
  public void updateTask() {

    EntityLivingBase target = this.taskOwner.getAttackTarget();

    if (!(target instanceof EntityPlayer)) {
      return;
    }

    if (this.taskOwner.getDistanceSq(target) > this.rangeSq) {
      return;
    }

    if (this.sightRequired && !this.taskOwner.canEntityBeSeen(target)) {
      return;
    }

    if (this.taskOwner.posY >= target.posY) {
      return;
    }

    IAntiAirPlayerData data = CapabilityAntiAir.get((EntityPlayer) target);
    
    if (data != null) {
      if (ModuleOnslaughtConfig.CUSTOM_AI.ANTI_AIR.CUMULATIVE_MOTION_Y) {
        data.setMotionY(data.getMotionY() + this.motionY);

      } else if (this.motionY < data.getMotionY()) {
        data.setMotionY(this.motionY);
      }
    }
  }
}
