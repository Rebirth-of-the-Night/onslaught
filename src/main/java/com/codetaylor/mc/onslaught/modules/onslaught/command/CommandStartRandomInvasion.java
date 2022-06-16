package com.codetaylor.mc.onslaught.modules.onslaught.command;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCommandStarter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/** Starts a random invasion for a specific player or N random players. */
public class CommandStartRandomInvasion extends CommandBase {

  private static final String USAGE = "commands.onslaught.startrandom.usage";
  private static final String NO_INVASION_SELECTED =
      "commands.onslaught.startrandom.no.invasion.selected";
  private static final String INVASION_SKIPPING = "commands.onslaught.startrandom.skipping";
  private static final String INVASION_ALREADY_ACTIVE =
      "commands.onslaught.startrandom.already.active";
  private static final String INVASION_STARTING = "commands.onslaught.startrandom.starting";

  private final InvasionCommandStarter invasionCommandStarter;
  private final Function<EntityPlayerMP, String> invasionSelectorFunction;

  public CommandStartRandomInvasion(
      InvasionCommandStarter invasionCommandStarter,
      Function<EntityPlayerMP, String> invasionSelectorFunction) {

    this.invasionCommandStarter = invasionCommandStarter;
    this.invasionSelectorFunction = invasionSelectorFunction;
  }

  @Nonnull
  @Override
  public String getName() {

    return "ostartrandom";
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

    if (args.length > 0) {

      try {
        int count = Integer.parseInt(args[0]);
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        this.startInvasionsForNRandomPlayers(sender, count, players);

      } catch (NumberFormatException e) {
        EntityPlayerMP player = CommandBase.getPlayer(server, sender, args[0]);
        this.startInvasionForPlayer(sender, player);
      }

    } else {
      EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
      this.startInvasionForPlayer(sender, player);
    }
  }

  private void startInvasionsForNRandomPlayers(
      ICommandSender sender, int count, List<EntityPlayerMP> players) throws CommandException {

    List<EntityPlayerMP> shuffledPlayerList = new ArrayList<>(players);
    Collections.shuffle(shuffledPlayerList);

    for (int i = shuffledPlayerList.size() - 1; i >= 0; i--) {
      EntityPlayerMP player = shuffledPlayerList.remove(i);

      World world = player.world;
      InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(world);
      UUID uuid = player.getUniqueID();
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(uuid);

      // Skip players with an active invasion, notify command sender.
      if (data.getInvasionState() == InvasionPlayerData.EnumInvasionState.Active) {
        sender.sendMessage(new TextComponentTranslation(INVASION_SKIPPING, player.getName()));
        continue;
      }

      this.startInvasionForPlayer(sender, player);

      count -= 1;

      if (count == 0) {
        break;
      }
    }
  }

  private void startInvasionForPlayer(ICommandSender sender, EntityPlayerMP player)
      throws CommandException {

    String invasionTemplateId = this.invasionSelectorFunction.apply(player);

    if (invasionTemplateId == null) {
      throw new CommandException(NO_INVASION_SELECTED, player.getName());
    }

    if (this.invasionCommandStarter.startInvasionForPlayer(invasionTemplateId, player)) {
      sender.sendMessage(
          new TextComponentTranslation(INVASION_STARTING, player.getName(), invasionTemplateId));

    } else {
      throw new CommandException(INVASION_ALREADY_ACTIVE, player.getName());
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
