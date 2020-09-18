package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;

import java.util.function.Function;

/**
 * Responsible for calculating the invasion's warning message timestamp given
 * the invasion template id and invasion timestamp.
 */
public class InvasionWarningMessageTimestampFunction {

  private final Function<String, InvasionTemplate> idToInvasionTemplateFunction;

  public InvasionWarningMessageTimestampFunction(Function<String, InvasionTemplate> idToInvasionTemplateFunction) {

    this.idToInvasionTemplateFunction = idToInvasionTemplateFunction;
  }

  public long apply(String invasionTemplateId, long invasionTimestamp) {

    InvasionTemplate invasionTemplate = this.idToInvasionTemplateFunction.apply(invasionTemplateId);
    int ticks = invasionTemplate.messages.warn.ticks;

    if (ticks < 0) {
      return -1;
    }

    return invasionTimestamp - ticks;
  }
}