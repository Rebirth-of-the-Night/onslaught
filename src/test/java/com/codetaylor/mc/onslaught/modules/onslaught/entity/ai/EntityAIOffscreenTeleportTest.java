package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import static com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIOffscreenTeleport.towards;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;
import net.minecraft.entity.EntityLiving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EntityAIOffscreenTeleportTest {
  EntityLiving mob;
  EntityLiving target;

  @BeforeEach
  void createMobAndTarget() {
    EntityAIOffscreenTeleport.setRunChanceOutcomes(1);

    mob = mock(EntityLiving.class, CALLS_REAL_METHODS);
    target = mock(EntityLiving.class, CALLS_REAL_METHODS);

    when(mob.getAttackTarget()).thenReturn(target);
    when(mob.getRNG()).thenReturn(new Random());
  }

  @Test
  void test_shouldExecute_close() {
    target.posX = 99;
    EntityAIOffscreenTeleport task = new EntityAIOffscreenTeleport(mob, 100, 0, false);

    assertThat(task.shouldExecute()).isFalse();
  }

  @Test
  void test_shouldExecute_far() {
    target.posX = 101;
    EntityAIOffscreenTeleport task = new EntityAIOffscreenTeleport(mob, 100, 0, false);

    assertThat(task.shouldExecute()).isTrue();
  }

  @Test
  void test_startExecuting() {
    mob.posX = 10;
    mob.posY = 10;
    mob.posZ = 10;

    target.posX = 100;
    target.posY = 20;
    target.posZ = -50;

    EntityAIOffscreenTeleport task = spy(new EntityAIOffscreenTeleport(mob, 100, 0.5f, false));
    doReturn(true).when(mob).attemptTeleport(anyDouble(), anyDouble(), anyDouble());
    doReturn(null).when(task).invisibilityEffect();
    doNothing().when(mob).addPotionEffect(any());

    task.startExecuting();

    verify(mob).attemptTeleport(55d, 15d, -20d);
  }

  @Test
  void towards_at_a_50_factor() {
    assertThat(towards(0, 100, 0.5f)).isEqualTo(50);
    assertThat(towards(-50, 100, 0.5f)).isEqualTo(25);
    assertThat(towards(100, 50, 0.5f)).isEqualTo(75);
  }

  @Test
  void towards_at_a_150_factor() {
    assertThat(towards(0, 100, 1.5f)).isEqualTo(150);
    assertThat(towards(-50, 100, 1.5f)).isEqualTo(175);
    assertThat(towards(100, 50, 1.5f)).isEqualTo(25);
  }
}
