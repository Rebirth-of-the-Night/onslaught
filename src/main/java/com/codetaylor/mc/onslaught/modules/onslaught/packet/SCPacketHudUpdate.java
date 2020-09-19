package com.codetaylor.mc.onslaught.modules.onslaught.packet;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.InvasionHudRenderInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Packet sent to the client containing nearby invasion data for HUD rendering.
 */
public class SCPacketHudUpdate
    implements IMessage {

  private List<InvasionHudRenderInfo> invasionHudRenderInfoList;

  @SuppressWarnings("unused")
  public SCPacketHudUpdate() {
    // serialization
  }

  public SCPacketHudUpdate(List<InvasionHudRenderInfo> invasionHudRenderInfoList) {

    this.invasionHudRenderInfoList = invasionHudRenderInfoList;
  }

  public List<InvasionHudRenderInfo> getInvasionHudRenderInfoList() {

    return this.invasionHudRenderInfoList;
  }

  @Override
  public void fromBytes(ByteBuf buf) {

    PacketBuffer pb = new PacketBuffer(buf);

    int count = pb.readInt();
    this.invasionHudRenderInfoList = new ArrayList<>(count);

    for (int i = 0; i < count; i++) {
      InvasionHudRenderInfo info = new InvasionHudRenderInfo();
      info.playerUuid = new UUID(pb.readLong(), pb.readLong()); // most, least
      info.invasionName = pb.readString(Short.MAX_VALUE);
      info.invasionCompletionPercentage = pb.readFloat();
      this.invasionHudRenderInfoList.add(info);
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {

    PacketBuffer pb = new PacketBuffer(buf);

    pb.writeInt(this.invasionHudRenderInfoList.size());

    for (InvasionHudRenderInfo info : this.invasionHudRenderInfoList) {
      pb.writeLong(info.playerUuid.getMostSignificantBits());
      pb.writeLong(info.playerUuid.getLeastSignificantBits());
      pb.writeString(info.invasionName);
      pb.writeFloat(info.invasionCompletionPercentage);
    }
  }
}