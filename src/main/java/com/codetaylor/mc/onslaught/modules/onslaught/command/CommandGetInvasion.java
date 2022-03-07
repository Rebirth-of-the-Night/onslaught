package com.codetaylor.mc.onslaught.modules.onslaught.command;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/** Stops an invasion for a single player. */
public class CommandGetInvasion extends CommandBase {

  private static final String USAGE = "commands.onslaught.get.usage";
  private static final String NO_INVASION_DATA = "commands.onslaught.get.noinvasiondata";
  private static final String STATE = "commands.onslaught.get.state";
  private static final String INVALID_INVASION_DATA = "commands.onslaught.get.invalidinvasion";
  private static final String DATA_TEMPLATE = "commands.onslaught.get.datatemplate";

  public CommandGetInvasion() {

  }

  @Nonnull
  @Override
  public String getName() {

    return "oget";
  }

  @Override
  public int getRequiredPermissionLevel() {

    return 2;
  }

  @Nonnull
  @Override
  public String getUsage(@Nonnull ICommandSender sender) {

    return USAGE;
  }

  @ParametersAreNonnullByDefault
  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args)
      throws CommandException {

    EntityPlayerMP player;

    if (args.length > 0) {
      player = CommandBase.getPlayer(server, sender, args[0]);

    } else {
      player = CommandBase.getCommandSenderAsPlayer(sender);
    }

    this.sendCurrentInvasion(sender, player);
  }

  private void sendCurrentInvasion(ICommandSender sender, EntityPlayerMP player) {

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(player.world);
    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

    if (playerData == null) {
      sender.sendMessage(new TextComponentTranslation(NO_INVASION_DATA));
    } else {
      ITextComponent tc = new TextComponentTranslation(STATE, playerData.getInvasionState(), playerData.getTicksUntilEligible());
      if (playerData.getInvasionState() == InvasionPlayerData.EnumInvasionState.Active) {
        tc.appendText("\n");
        if (playerData.getInvasionData() == null) {
          tc.appendSibling(new TextComponentTranslation(INVALID_INVASION_DATA));
        } else {
          tc.appendSibling(new TextComponentTranslation(DATA_TEMPLATE, playerData.getInvasionData()));
        }
      }
      sender.sendMessage(tc);
    }
  }

  @ParametersAreNonnullByDefault
  @Nonnull
  @Override
  public List<String> getTabCompletions(
      MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

    if (args.length == 1) {
      return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

    return Collections.emptyList();
  }
}
