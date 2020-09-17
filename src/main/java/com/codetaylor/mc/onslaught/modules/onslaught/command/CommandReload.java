package com.codetaylor.mc.onslaught.modules.onslaught.command;

import com.codetaylor.mc.onslaught.modules.onslaught.template.TemplateLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Responsible for spawning a mob from a mob template.
 */
public class CommandReload
    extends CommandBase {

  private static final String USAGE = "commands.onslaught.reload.usage";
  private static final String SUCCESS = "commands.onslaught.reload.success";
  private static final String FAILED = "commands.onslaught.reload.failed";

  private final TemplateLoader templateLoader;

  public CommandReload(TemplateLoader templateLoader) {

    this.templateLoader = templateLoader;
  }

  @Nonnull
  @Override
  public String getName() {

    return "oreload";
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

    if (args.length > 0) {
      throw new WrongUsageException(USAGE);

    } else {

      if (this.templateLoader.load()) {
        notifyCommandListener(sender, this, SUCCESS);

      } else {
        notifyCommandListener(sender, this, FAILED);
      }
    }
  }
}
