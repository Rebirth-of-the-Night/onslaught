package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import java.util.Deque;
import java.util.UUID;

public class EligiblePlayerQueue {

  private final Deque<UUID> queue;

  public EligiblePlayerQueue(Deque<UUID> queue) {

    this.queue = queue;
  }

  public void add(UUID uuid) {

    if (!this.queue.contains(uuid)) {
      this.queue.add(uuid);
    }
  }

  public void remove(UUID uuid) {

    this.queue.remove(uuid);
  }

  public boolean isEmpty() {

    return this.queue.isEmpty();
  }

  public UUID pop() {

    return this.queue.pop();
  }
}
