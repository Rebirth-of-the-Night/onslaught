package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Responsible for applying potion effects to players within range of a
 * deferred spawn location.
 */
public class DeferredSpawnEffectApplicator
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final List<DeferredSpawnData> deferredSpawnDataList;
  private final Supplier<List<Potion>> effectListSupplier;
  private final IntSupplier effectDurationSupplier;
  private final IntSupplier effectRangeSupplier;

  public DeferredSpawnEffectApplicator(
      List<DeferredSpawnData> deferredSpawnDataList,
      Supplier<List<Potion>> effectListSupplier,
      IntSupplier effectDurationSupplier,
      IntSupplier effectRangeSupplier
  ) {

    this.deferredSpawnDataList = deferredSpawnDataList;
    this.effectListSupplier = effectListSupplier;
    this.effectDurationSupplier = effectDurationSupplier;
    this.effectRangeSupplier = effectRangeSupplier;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    List<Potion> effectList = this.effectListSupplier.get();

    if (effectList.isEmpty()) {
      return;
    }

    int effectRange = this.effectRangeSupplier.getAsInt();

    if (effectRange == 0) {
      return;
    }

    int effectRangeSq = effectRange * effectRange;

    int effectDuration = this.effectDurationSupplier.getAsInt();

    if (effectDuration == 0) {
      return;
    }

    for (EntityPlayerMP player : playerList.getPlayers()) {

      for (DeferredSpawnData data : this.deferredSpawnDataList) {

        if (player.world.provider.getDimension() == data.getDimensionId()
            && player.getDistanceSq(data.getEntityLiving()) <= effectRangeSq) {

          for (Potion potion : effectList) {
            player.addPotionEffect(new PotionEffect(potion, effectDuration));
          }
        }
      }
    }
  }

  public static class EffectListSupplier
      implements Supplier<List<Potion>> {

    private static final Logger LOGGER = LogManager.getLogger(EffectListSupplier.class);

    private final String[] effects;

    private List<Potion> potionList;

    public EffectListSupplier(String[] effects) {

      this.effects = effects;
    }

    @Override
    public List<Potion> get() {

      if (this.potionList == null) {
        this.potionList = new ArrayList<>();

        for (String effect : this.effects) {

          ResourceLocation resourceLocation = new ResourceLocation(effect);
          Potion potion = ForgeRegistries.POTIONS.getValue(resourceLocation);

          if (potion == null) {
            String message = "Unknown forced spawn effect id: " + resourceLocation;
            ModOnslaught.LOG.log(Level.SEVERE, message);
            LOGGER.error(message);

          } else {
            this.potionList.add(potion);
          }
        }
      }

      return this.potionList;
    }
  }
}
