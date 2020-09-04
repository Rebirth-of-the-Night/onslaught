package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleLists;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Predicate;

public class InvasionSpawnSampler {

  private static final double TWO_PI = Math.PI * 2;

  private final Predicate<EntityLiving> predicate;

  public InvasionSpawnSampler(Predicate<EntityLiving> predicate) {

    this.predicate = predicate;
  }

  /**
   * @param radiusMin      the minimum radius of the sample circles
   * @param radiusMax      the maximum radius of the sampler circles
   * @param stepRadius     the radial distance between sample circles
   * @param sampleDistance the linear distance between sample points on a circle
   * @param verticalRange  the vertical +/- range to sample for each point
   * @return
   */
  @Nullable
  public Vec3d getSpawnLocation(EntityLiving entity, BlockPos origin, double radiusMin, double radiusMax, int stepRadius, double sampleDistance, int verticalRange) {

//    double radiusMax = 128;
//    double radiusMin = 16;
//    int stepRadius = 4;
//    double sampleDistance = 2;

//    System.out.println("Radius max: " + radiusMax);
//    System.out.println("Radius min: " + radiusMin);
//    System.out.println("Radius step: " + stepRadius);
//    System.out.println("Sample distance: " + sampleDistance);

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

        double verticalMin = Math.max(0, -verticalRange + origin.getY());
        double verticalMax = Math.min(255, verticalRange + origin.getY());

        for (double y = verticalMin; y <= verticalMax; y++) {
          sampleCount += 1;

          // Test if this location can spawn the given entity.
          entity.setPosition(x, y, z);

          if (this.predicate.test(entity)) {
            System.out.println("Sampled points: " + sampleCount);
            System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
            Vec3d result = new Vec3d(x, y, z);
            System.out.println("Distance: " + (new Vec3d(origin).distanceTo(result)));
            return result;
          }
        }
      }
    }

    System.out.println("Sampled points: " + sampleCount);
    System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
    return null;
  }
}
