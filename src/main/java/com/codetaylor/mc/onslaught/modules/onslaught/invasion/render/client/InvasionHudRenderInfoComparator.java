package com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.client;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.InvasionHudRenderInfo;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import java.util.Comparator;

public class InvasionHudRenderInfoComparator
    implements Comparator<InvasionHudRenderInfo> {

  public static final Ordering<InvasionHudRenderInfo> ORDERING_INSTANCE = Ordering.from(new InvasionHudRenderInfoComparator());

  @Override
  public int compare(InvasionHudRenderInfo o1, InvasionHudRenderInfo o2) {

    return ComparisonChain.start()
        .compare(o1.invasionCompletionPercentage, o2.invasionCompletionPercentage)
        .compare(o1.invasionName, o2.invasionName)
        .compare(o1.playerUuid, o2.playerUuid)
        .result();
  }
}