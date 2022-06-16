package com.codetaylor.mc.onslaught.modules.onslaught.command;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionStopExecutor;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentTranslation;

/** Stops an invasion for a single player. */
public class CommandStopAllInvasion extends CommandBase {

  private static final String USAGE = "commands.onslaught.stopall.usage";
  private static final String SUCCESS = "commands.onslaught.stopall.success";

  private final InvasionStopExecutor invasionStopExecutor;

  public CommandStopAllInvasion(InvasionStopExecutor invasionStopExecutor) {

    this.invasionStopExecutor = invasionStopExecutor;
  }

  @Nonnull
  @Override
  public String getName() {

    return "ostopall";
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
      throw new CommandException(USAGE);

    } else {
      player = CommandBase.getCommandSenderAsPlayer(sender);
    }

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(player.world);
    PlayerList playerList = server.getPlayerList();
    List<EntityPlayerMP> players = playerList.getPlayers();

    this.invasionStopExecutor.stopAllWithCheck(players, invasionGlobalSavedData);
    sender.sendMessage(new TextComponentTranslation(SUCCESS, player.getName()));
  }
}
