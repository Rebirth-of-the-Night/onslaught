package com.codetaylor.mc.onslaught.modules.onslaught.command;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionStopExecutor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Stops an invasion for a single player.
 */
public class CommandStopInvasion
    extends CommandBase {

  private static final String USAGE = "commands.onslaught.stop.usage";
  private static final String SUCCESS = "commands.onslaught.stop.success";

  private final InvasionStopExecutor invasionStopExecutor;

  public CommandStopInvasion(InvasionStopExecutor invasionStopExecutor) {

    this.invasionStopExecutor = invasionStopExecutor;
  }

  @Nonnull
  @Override
  public String getName() {

    return "ostop";
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
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    EntityPlayerMP player;

    if (args.length > 0) {
      player = CommandBase.getPlayer(server, sender, args[0]);

    } else {
      player = CommandBase.getCommandSenderAsPlayer(sender);
    }

    this.stopInvasionForPlayer(sender, player);
  }

  private void stopInvasionForPlayer(
      ICommandSender sender,
      EntityPlayerMP player
  ) {

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(player.world);
    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

    if (this.invasionStopExecutor.stopWithCheck(player, invasionGlobalSavedData, playerData)) {
      sender.sendMessage(new TextComponentTranslation(SUCCESS, player.getName()));
    }
  }

  @ParametersAreNonnullByDefault
  @Nonnull
  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

    if (args.length == 1) {
      return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

    return Collections.emptyList();
  }
}
