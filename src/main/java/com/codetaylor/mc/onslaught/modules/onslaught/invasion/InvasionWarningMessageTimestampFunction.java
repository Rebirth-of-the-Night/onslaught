package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;

import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * Responsible for calculating the invasion's warning message timestamp given
 * the invasion template id and invasion timestamp.
 */
public class InvasionWarningMessageTimestampFunction {

  public static final int MAX_TICKS = 10 * 60 * 20;

  private final Function<String, InvasionTemplate> idToInvasionTemplateFunction;
  private final IntSupplier defaultWarningTicks;

  public InvasionWarningMessageTimestampFunction(
      Function<String, InvasionTemplate> idToInvasionTemplateFunction,
      IntSupplier defaultWarningTicks
  ) {

    this.idToInvasionTemplateFunction = idToInvasionTemplateFunction;
    this.defaultWarningTicks = defaultWarningTicks;
  }

  public long apply(String invasionTemplateId, long invasionTimestamp) {

    InvasionTemplate invasionTemplate = this.idToInvasionTemplateFunction.apply(invasionTemplateId);
    int ticks = invasionTemplate.messages.warn.ticks;

    if (ticks < 0) {
      // check the default

      ticks = defaultWarningTicks.getAsInt();

      if (ticks < 0) {
        return -1;
      }
    }

    if (ticks > MAX_TICKS) {
      ticks = MAX_TICKS;
    }

    return invasionTimestamp - ticks;
  }
}