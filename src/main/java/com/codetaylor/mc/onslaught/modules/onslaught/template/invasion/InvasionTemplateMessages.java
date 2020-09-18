package com.codetaylor.mc.onslaught.modules.onslaught.template.invasion;

/**
 * Responsible for holding invasion message data read from json files.
 * <p>
 * If the strings are null, the config defaults are used. Set to empty string in
 * the json template to disable.
 */
public class InvasionTemplateMessages {

  public Warn warn = new Warn();
  public String begin;
  public String end;

  public static class Warn {

    public int ticks = -1;
    public String message;
  }
}