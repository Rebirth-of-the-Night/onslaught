package com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.client;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.InvasionHudRenderInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class InvasionHudRenderer {

  private static final int BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f).getRGB();
  private static final int WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f).getRGB();

  private final List<InvasionHudRenderInfo> invasionHudRenderInfoList;
  private final IntSupplier xPositionSupplier;
  private final IntSupplier yPositionSupplier;
  private final IntSupplier widthSupplier;
  private final Supplier<int[]> barColorSupplier;

  public InvasionHudRenderer(
      List<InvasionHudRenderInfo> invasionHudRenderInfoList,
      IntSupplier xPositionSupplier,
      IntSupplier yPositionSupplier,
      IntSupplier widthSupplier,
      Supplier<int[]> barColorSupplier
  ) {

    this.invasionHudRenderInfoList = invasionHudRenderInfoList;
    this.xPositionSupplier = xPositionSupplier;
    this.yPositionSupplier = yPositionSupplier;
    this.widthSupplier = widthSupplier;
    this.barColorSupplier = barColorSupplier;
  }

  public void render() {

    // Uncomment for testing
//    this.invasionHudRenderInfoList.clear();
//
//    for (int i = 0; i < 4; i++) {
//      InvasionHudRenderInfo info = new InvasionHudRenderInfo();
//      info.invasionName = "Invasion " + i;
//      info.invasionCompletionPercentage = i / 3f;
//      Minecraft minecraft = Minecraft.getMinecraft();
//      EntityPlayerSP player = minecraft.player;
//
//      info.playerUuid = player.getUniqueID();
//      this.invasionHudRenderInfoList.add(info);
//    }

    if (this.invasionHudRenderInfoList.isEmpty()) {
      return;
    }

    for (int i = 0; i < this.invasionHudRenderInfoList.size(); i++) {
      this.render(i, this.invasionHudRenderInfoList.get(i));
    }
  }

  private void render(int index, InvasionHudRenderInfo info) {

    Minecraft minecraft = Minecraft.getMinecraft();

    int headSize = 32;
    int padCard = 4;
    int marginCard = 2;
    int widthCard = this.widthSupplier.getAsInt() + padCard + padCard;
    int heightCard = 40 + padCard + padCard;
    int xCard = this.xPositionSupplier.getAsInt();
    int yCard = this.yPositionSupplier.getAsInt() + index * (heightCard + marginCard);
    int heightBar = 16;

    // Uncomment to render card -- for layout testing
    // Gui.drawRect(xCard, yCard, xCard + widthCard, yCard + heightCard, new Color(1, 0, 0, 0.25f).getRGB());

    // -------------------------------------------------------------------------
    // - Bar
    // -------------------------------------------------------------------------

    {
      int x = xCard + padCard + headSize + 3;
      int y = yCard + heightCard / 2 - 2;
      int right = xCard + widthCard - padCard;
      int rightFill = (int) ((right - x) * info.invasionCompletionPercentage) + x;
      int bottom = y + heightBar;
      Gui.drawRect(x - 3, y - 3, right + 3, bottom + 3, BLACK);
      Gui.drawRect(x - 2, y - 2, right + 2, bottom + 2, WHITE);
      Gui.drawRect(x - 1, y - 1, right + 1, bottom + 1, BLACK);

      Gui.drawRect(x, y, rightFill, bottom, this.encodeColorInt(this.barColorSupplier.get()));

      String text = (int) (info.invasionCompletionPercentage * 100) + "%";
      int xBarText = x + (right - x) / 2 - minecraft.fontRenderer.getStringWidth(text) / 2;
      int yBarText = y + heightBar / 2 - minecraft.fontRenderer.FONT_HEIGHT / 2;
      minecraft.fontRenderer.drawStringWithShadow(text, xBarText, yBarText, WHITE);
    }

    // -------------------------------------------------------------------------
    // - Head
    // -------------------------------------------------------------------------

    {
      if (minecraft.isIntegratedServerRunning() || minecraft.getConnection().getNetworkManager().isEncrypted()) {
        NetHandlerPlayClient nethandlerplayclient = minecraft.player.connection;
        NetworkPlayerInfo playerInfo = nethandlerplayclient.getPlayerInfo(info.playerUuid);

        minecraft.getTextureManager().bindTexture(playerInfo.getLocationSkin());
        int x = xCard + padCard;
        int y = yCard + heightCard / 2 - headSize / 2;

        Gui.drawRect(x - 3, y - 3, x + headSize + 3, y + headSize + 3, BLACK);
        Gui.drawRect(x - 2, y - 2, x + headSize + 2, y + headSize + 2, WHITE);
        Gui.drawRect(x - 1, y - 1, x + headSize + 1, y + headSize + 1, BLACK);

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, headSize, headSize, 64, 64);
      }
    }

    // -------------------------------------------------------------------------
    // - Name
    // -------------------------------------------------------------------------

    if (info.invasionName.length() > 0) {
      int x = xCard + padCard + headSize + 4;
      int y = yCard + heightCard / 2 - heightBar / 2 - minecraft.fontRenderer.FONT_HEIGHT + 2;
      minecraft.fontRenderer.drawStringWithShadow(I18n.format(info.invasionName), x, y, WHITE);
    }
  }

  private int encodeColorInt(int[] rgb) {

    return ((0xFF) << 24) |
        ((rgb[0] & 0xFF) << 16) |
        ((rgb[1] & 0xFF) << 8) |
        ((rgb[2] & 0xFF));
  }
}