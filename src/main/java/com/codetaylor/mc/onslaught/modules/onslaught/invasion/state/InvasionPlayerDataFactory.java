package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.athenaeum.util.WeightedPicker;
import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionSpawnDataConverterFunction;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Responsible for creating {@link InvasionPlayerData.InvasionData} from the
 * given template id, using the given {@link Random} to select values from
 * ranges.
 */
public class InvasionPlayerDataFactory {

  private final Function<String, InvasionTemplate> invasionTemplateFunction;
  private final InvasionSpawnDataConverterFunction invasionSpawnDataConverterFunction;

  public InvasionPlayerDataFactory(
      Function<String, InvasionTemplate> invasionTemplateFunction,
      InvasionSpawnDataConverterFunction invasionSpawnDataConverterFunction
  ) {

    this.invasionTemplateFunction = invasionTemplateFunction;
    this.invasionSpawnDataConverterFunction = invasionSpawnDataConverterFunction;
  }

  @Nullable
  public InvasionPlayerData.InvasionData create(String templateId, UUID invasionUuid, Random random, long timestamp) {

    InvasionTemplate invasionTemplate = this.invasionTemplateFunction.apply(templateId);

    if (invasionTemplate == null) {
      ModOnslaught.LOG.log(Level.SEVERE, "Unknown template id: " + templateId);
      return null;
    }

    InvasionPlayerData.InvasionData invasionData = new InvasionPlayerData.InvasionData();
    invasionData.setInvasionTemplateId(templateId);
    invasionData.setInvasionUuid(invasionUuid);
    invasionData.setTimestamp(timestamp);

    for (InvasionTemplateWave waveTemplate : invasionTemplate.waves) {
      invasionData.getWaveDataList().add(this.createWaveData(waveTemplate, random));
    }

    return invasionData;
  }

  private InvasionPlayerData.InvasionData.WaveData createWaveData(InvasionTemplateWave waveTemplate, Random random) {

    InvasionPlayerData.InvasionData.WaveData waveData = new InvasionPlayerData.InvasionData.WaveData();
    waveData.setDelayTicks(this.evaluateRange(waveTemplate.delayTicks, random));

    InvasionTemplateWave.Group group = this.selectGroup(waveTemplate.groups, random);

    for (InvasionTemplateWave.Mob mob : group.mobs) {
      waveData.getMobDataList().add(this.createMobData(mob, random));
    }

    return waveData;
  }

  /**
   * @param mob    the {@link InvasionTemplateWave.Mob}
   * @param random the {@link Random} prng
   * @return an {@link InvasionPlayerData.InvasionData.MobData} generated from the given {@link InvasionTemplateWave.Mob} and {@link Random}
   */
  private InvasionPlayerData.InvasionData.MobData createMobData(InvasionTemplateWave.Mob mob, Random random) {

    InvasionPlayerData.InvasionData.MobData mobData = new InvasionPlayerData.InvasionData.MobData();
    mobData.setMobTemplateId(mob.id);
    mobData.setTotalCount(this.evaluateRange(mob.count, random));
    mobData.setKilledCount(0);
    mobData.setSpawnData(this.invasionSpawnDataConverterFunction.apply(mob.spawn));
    return mobData;
  }

  /**
   * @param groups the array of {@link InvasionTemplateWave.Group} to select from
   * @param random the {@link Random} prng
   * @return an {@link InvasionTemplateWave.Group} selected using a {@link WeightedPicker} and the given {@link Random} prng
   */
  private InvasionTemplateWave.Group selectGroup(InvasionTemplateWave.Group[] groups, Random random) {

    WeightedPicker<InvasionTemplateWave.Group> weightedPicker = new WeightedPicker<>(random);

    for (InvasionTemplateWave.Group group : groups) {
      weightedPicker.add(group.weight, group);
    }

    return weightedPicker.get();
  }

  /**
   * @param range  the given range, either [fixed] or [min,max]
   * @param random the {@link Random} prng
   * @return a random value in the given range, accounting for fixed, single value arrays
   */
  private int evaluateRange(int[] range, Random random) {

    if (range.length == 1) {
      return range[0];
    }

    int min = Math.min(range[0], range[1]);
    int max = Math.max(range[0], range[1]);

    return random.nextInt(max - min + 1) + min;
  }

}
