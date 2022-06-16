package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

/** Responsible for sending invasion messages to the given player. */
public class InvasionMessageSender {

  private final Function<String, InvasionTemplate> idToInvasionTemplateFunction;
  private final Function<InvasionTemplate, String> messageFunction;
  private final Supplier<String> defaultMessageSupplier;

  public InvasionMessageSender(
      Function<String, InvasionTemplate> idToInvasionTemplateFunction,
      Function<InvasionTemplate, String> messageFunction,
      Supplier<String> defaultMessageSupplier) {

    this.idToInvasionTemplateFunction = idToInvasionTemplateFunction;
    this.messageFunction = messageFunction;
    this.defaultMessageSupplier = defaultMessageSupplier;
  }

  /**
   * Sends a message to the given player.
   *
   * <p>If the template message is null, the default message is used.
   *
   * <p>If the template message or the default message is empty, no message is sent.
   *
   * @param player the player to receive the message
   */
  public void sendMessage(EntityPlayerMP player) {

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(player.world);
    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());
    InvasionPlayerData.InvasionData invasionData = playerData.getInvasionData();

    if (invasionData != null) {
      String invasionTemplateId = invasionData.getInvasionTemplateId();
      InvasionTemplate invasionTemplate =
          this.idToInvasionTemplateFunction.apply(invasionTemplateId);
      String messageKey = this.messageFunction.apply(invasionTemplate);

      if (messageKey == null) {
        messageKey = this.defaultMessageSupplier.get();
      }

      if (messageKey != null && !messageKey.isEmpty()) {
        TextComponentTranslation textComponentTranslation =
            new TextComponentTranslation(messageKey);
        player.sendMessage(textComponentTranslation);
      }
    }
  }
}
