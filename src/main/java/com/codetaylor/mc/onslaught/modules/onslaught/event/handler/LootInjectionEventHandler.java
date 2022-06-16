package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.loot.ExtraLootInjector;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Responsible for adding loot from the extra tables to the loot list when a mob dies. */
public class LootInjectionEventHandler {

  private final ExtraLootInjector injector;

  public LootInjectionEventHandler(ExtraLootInjector injector) {

    this.injector = injector;
  }

  @SubscribeEvent
  public void on(LivingDropsEvent event) {

    Entity entity = event.getEntity();

    if (entity.world.isRemote) {
      return;
    }

    DamageSource source = event.getSource();
    boolean recentlyHit = event.isRecentlyHit();
    List<EntityItem> drops = event.getDrops();
    this.injector.inject(entity, source, recentlyHit, drops);
  }
}
