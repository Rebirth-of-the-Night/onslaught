package com.codetaylor.mc.onslaught.modules.onslaught.lib;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public final class MethodHandleHelper {

  public static MethodHandle unreflectGetter(Class<?> c, String field) {

    try {
      return MethodHandles.lookup().unreflectGetter(
          ObfuscationReflectionHelper.findField(c, field)
      );

    } catch (IllegalAccessException e) {
      throw new RuntimeException(String.format("Error unreflecting getter for %s", field), e);
    }
  }

  public static MethodHandle unreflectSetter(Class<?> c, String field) {

    try {
      return MethodHandles.lookup().unreflectSetter(
          ObfuscationReflectionHelper.findField(c, field)
      );

    } catch (IllegalAccessException e) {
      throw new RuntimeException(String.format("Error unreflecting setter for %s", field), e);
    }
  }

  private MethodHandleHelper() {
    //
  }
}
