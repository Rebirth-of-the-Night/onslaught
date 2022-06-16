package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData.EnumInvasionState;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData.InvasionData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData.InvasionData.MobData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData.InvasionData.WaveData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvasionFinishedPredicateTest {

  InvasionFinishedPredicate subject = new InvasionFinishedPredicate();

  /**
   * Testing method naming convention: "{METHOD}: {PREMISE}, then {EXPECTATION}
   *
   * <p>A good unit test does these things in this order: 1. Setup 2. Change 3. Assertion
   */
  @Test
  @DisplayName("test: If player data is missing, then invasion is finished")
  void test_missing_playerData() {
    assertThat(subject.test(null)).isTrue();
  }

  @Test
  @DisplayName("test: If state is active, but all enemies are slain, then invasion is finished")
  void test_all_mobs_killed() {
    InvasionPlayerData playerData = new InvasionPlayerData();
    InvasionData iData = new InvasionData();
    playerData.setInvasionData(iData);

    playerData.setInvasionState(EnumInvasionState.Active);
    iData.getWaveDataList().add(waveOf(mobDataOf(1, 1), mobDataOf(3, 3)));
    iData.getWaveDataList().add(waveOf(mobDataOf(5, 5)));

    assertThat(subject.test(playerData)).isTrue();
  }

  @Test
  @DisplayName("test: If state is active and enemies remain, then invasion is unfinished")
  void test_not_all_mobs_killed() {
    InvasionPlayerData playerData = new InvasionPlayerData();
    InvasionData iData = new InvasionData();
    playerData.setInvasionData(iData);

    playerData.setInvasionState(EnumInvasionState.Active);
    iData.getWaveDataList().add(waveOf(mobDataOf(7, 7), mobDataOf(3, 3)));
    iData.getWaveDataList().add(waveOf(mobDataOf(0, 1)));

    assertThat(subject.test(playerData)).isFalse();
  }

  @Test
  @DisplayName("test: If state is not active, then invasion is unfinished")
  void test_non_active_state() {
    InvasionPlayerData playerData = new InvasionPlayerData();
    playerData.setInvasionState(EnumInvasionState.Waiting);

    assertThat(subject.test(playerData)).isFalse();
  }

  MobData mobDataOf(int killed, int total) {
    MobData mobData = new MobData();
    mobData.setKilledCount(killed);
    mobData.setTotalCount(total);
    return mobData;
  }

  WaveData waveOf(MobData... mobDatas) {
    WaveData waveData = new WaveData();
    for (MobData mobData : mobDatas) {
      waveData.getMobDataList().add(mobData);
    }
    return waveData;
  }
}
