package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIAttackMelee;
import java.util.logging.Level;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/** Responsible for injecting the AI melee attack task into entities with the tag. */
public class EntityAIAttackMeleeInjector extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!(entity instanceof EntityCreature)) {
      ModOnslaught.LOG.log(
          Level.SEVERE,
          "EntityAIAttackMelee can only be applied to subclasses of EntityCreature, was "
              + entity.getClass());
      return;
    }

    if (!tag.hasKey(Tag.AI_ATTACK_MELEE)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_ATTACK_MELEE);

    int priority = this.getPriority(aiTag, DefaultPriority.ATTACK_MELEE);
    double speed =
        this.getDouble(
            aiTag, Tag.AI_PARAM_SPEED, ModuleOnslaughtConfig.CUSTOM_AI.ATTACK_MELEE.DEFAULT_SPEED);
    float attackDamage =
        (float)
            this.getDouble(
                aiTag,
                Tag.AI_PARAM_ATTACK_DAMAGE,
                ModuleOnslaughtConfig.CUSTOM_AI.ATTACK_MELEE.DEFAULT_ATTACK_DAMAGE);

    entity.tasks.addTask(
        priority, new EntityAIAttackMelee((EntityCreature) entity, speed, attackDamage, false));
  }
}
