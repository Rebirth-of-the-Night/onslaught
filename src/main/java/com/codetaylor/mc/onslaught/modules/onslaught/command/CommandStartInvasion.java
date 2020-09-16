package com.codetaylor.mc.onslaught.modules.onslaught.command;

import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCommandStarter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Starts a specific invasion for a specific player or N random players.
 */
public class CommandStartInvasion
    extends CommandBase {

  private static final String USAGE = "commands.onslaught.start.usage";
  private static final String INVALID_ID = "commands.onslaught.start.invalid.template.id";
  private static final String INVASION_ALREADY_ACTIVE = "commands.onslaught.start.already.active";
  private static final String INVASION_STARTING = "commands.onslaught.start.starting";
  private static final String INVASION_SKIPPING = "commands.onslaught.start.skipping";

  private final InvasionCommandStarter invasionCommandStarter;
  private final Function<String, InvasionTemplate> invasionTemplateFunction;
  private final Supplier<List<String>> invasionTemplateIdListSupplier;

  public CommandStartInvasion(
      InvasionCommandStarter invasionCommandStarter,
      Function<String, InvasionTemplate> invasionTemplateFunction,
      Supplier<List<String>> invasionTemplateIdListSupplier
  ) {

    this.invasionCommandStarter = invasionCommandStarter;
    this.invasionTemplateFunction = invasionTemplateFunction;
    this.invasionTemplateIdListSupplier = invasionTemplateIdListSupplier;
  }

  @Nonnull
  @Override
  public String getName() {

    return "ostart";
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

    if (args.length < 1) {
      throw new WrongUsageException(USAGE);

    } else {

      String templateId = args[0];
      InvasionTemplate invasionTemplate = this.invasionTemplateFunction.apply(templateId);

      if (invasionTemplate == null) {
        throw new CommandException(INVALID_ID, templateId);
      }

      if (args.length > 1) {

        try {
          int count = Integer.parseInt(args[1]);
          List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
          this.startInvasionsForNRandomPlayers(sender, templateId, count, players);

        } catch (NumberFormatException e) {
          EntityPlayerMP player = CommandBase.getPlayer(server, sender, args[1]);
          this.startInvasionForPlayer(sender, templateId, player);
        }

      } else {
        EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
        this.startInvasionForPlayer(sender, templateId, player);
      }
    }
  }

  private void startInvasionsForNRandomPlayers(
      ICommandSender sender,
      String templateId,
      int count,
      List<EntityPlayerMP> players
  ) throws CommandException {

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

      this.startInvasionForPlayer(sender, templateId, player);

      count -= 1;

      if (count == 0) {
        break;
      }
    }
  }

  private void startInvasionForPlayer(
      ICommandSender sender,
      String templateId,
      EntityPlayerMP player
  ) throws CommandException {

    if (this.invasionCommandStarter.startInvasionForPlayer(templateId, player)) {
      sender.sendMessage(new TextComponentTranslation(INVASION_STARTING, player.getName(), templateId));

    } else {
      throw new CommandException(INVASION_ALREADY_ACTIVE, player.getName());
    }
  }

  @ParametersAreNonnullByDefault
  @Nonnull
  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

    if (args.length == 1) {
      return CommandBase.getListOfStringsMatchingLastWord(args, this.invasionTemplateIdListSupplier.get());

    } else if (args.length == 2) {
      return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }

    return Collections.emptyList();
  }
}
