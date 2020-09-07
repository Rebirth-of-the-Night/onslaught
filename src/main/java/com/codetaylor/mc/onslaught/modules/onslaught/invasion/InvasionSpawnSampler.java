package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

import static com.codetaylor.mc.onslaught.ModOnslaught.LOG;

/**
 * Responsible for sampling spawn locations within range of the player,
 * evaluating each using the given predicate, and returning the first
 * valid location found.
 */
public class InvasionSpawnSampler {

  private static final double TWO_PI = Math.PI * 2;

  private final Predicate<EntityLiving> predicate;

  public InvasionSpawnSampler(Predicate<EntityLiving> predicate) {

    this.predicate = predicate;
  }

  /**
   * @param entity         the entity attempting to spawn
   * @param origin         the origin to sample around
   * @param radiusMin      the minimum radius of the sample circles
   * @param radiusMax      the maximum radius of the sampler circles
   * @param stepRadius     the radial distance between sample circles
   * @param sampleDistance the linear distance between sample points on a circle
   * @return valid spawn location
   */
  @Nullable
  public Vec3d getSpawnLocation(EntityLiving entity, BlockPos origin, double radiusMin, double radiusMax, int stepRadius, double sampleDistance) {

//    double radiusMax = 128;
//    double radiusMin = 16;
//    int stepRadius = 4;
//    double sampleDistance = 2;

    if (ModuleOnslaughtConfig.DEBUG.SPAWN_SAMPLER) {
      LOG.fine("Entity: " + entity.getClass().getName());
      LOG.fine("Origin: " + origin);
      LOG.fine("Radius max: " + radiusMax);
      LOG.fine("Radius min: " + radiusMin);
      LOG.fine("Radius step: " + stepRadius);
      LOG.fine("Sample distance: " + sampleDistance);
      LOG.fine("Predicate: " + this.predicate.getClass().getName());

      System.out.println("Entity: " + entity.getClass().getName());
      System.out.println("Origin: " + origin);
      System.out.println("Radius max: " + radiusMax);
      System.out.println("Radius min: " + radiusMin);
      System.out.println("Radius step: " + stepRadius);
      System.out.println("Sample distance: " + sampleDistance);
      System.out.println("Predicate: " + this.predicate.getClass().getName());
    }

    long start = System.currentTimeMillis();

    int sampleCount = 0;

    // Used to shuffle the sample angle lists.
    Random random = new Random();

    // Start at the maximum radius and work inward toward the center.
    // This ensures that we prioritize larger radii.
    for (double currentRadius = radiusMax; currentRadius > radiusMin; currentRadius -= stepRadius) {

      // Derive our current angle step. This will be added to the current angle
      // in order to walk around the circle. This formula ensures a distance
      // of sampleDistance between the previous sampled point and the next.
      double angleStepRadians = Math.acos((2 * currentRadius * currentRadius - (sampleDistance * sampleDistance)) / (2 * currentRadius * currentRadius));

      // Create a list of all the angles to sample with.
      DoubleList angleList = new DoubleArrayList((int) (TWO_PI / angleStepRadians) + 1);

      // Start at angle 0 and walk the circle, adding the angle step each loop.
      // Save each angle in a list.
      for (double currentAngleRadians = 0; currentAngleRadians < TWO_PI; currentAngleRadians += angleStepRadians) {
        angleList.add(currentAngleRadians);
      }

      // Shuffle the sample angle list.
      DoubleLists.shuffle(angleList, random);

      // Iterate the shuffled angle list and sample each point.
      for (int i = 0; i < angleList.size(); i++) {

        double currentAngleRadians = angleList.getDouble(i);

        double x = Math.cos(currentAngleRadians) * currentRadius + origin.getX();
        double z = Math.sin(currentAngleRadians) * currentRadius + origin.getZ();

        sampleCount += 1;

        entity.setPosition(x, origin.getY(), z);

        // Test if the entity can spawn at its current location.
        if (this.predicate.test(entity)) {
          Vec3d result = new Vec3d(x, entity.posY, z);

          if (ModuleOnslaughtConfig.DEBUG.SPAWN_SAMPLER) {
            LOG.fine("Samples: " + sampleCount);
            LOG.fine("Time: " + (System.currentTimeMillis() - start) + " ms");
            LOG.fine("Result: " + result);
            LOG.fine("Distance: " + (new Vec3d(origin).distanceTo(result)));

            System.out.println("Samples: " + sampleCount);
            System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
            System.out.println("Result: " + result);
            System.out.println("Distance: " + (new Vec3d(origin).distanceTo(result)));
          }
          return result;
        }
      }
    }

    if (ModuleOnslaughtConfig.DEBUG.SPAWN_SAMPLER) {
      LOG.fine("Samples: " + sampleCount);
      LOG.fine("Time: " + (System.currentTimeMillis() - start) + " ms");
      LOG.fine("Result: null");

      System.out.println("Samples: " + sampleCount);
      System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
      System.out.println("Result: null");
    }

    return null;
  }
}
