package com.codetaylor.mc.onslaught.modules.onslaught.command;

import com.codetaylor.mc.onslaught.modules.onslaught.data.DataStore;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.MobTemplateEntityFactory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for spawning a mob from a mob template.
 */
public class CommandSummon
    extends CommandBase {

  private static final String USAGE = "commands.onslaught.summon.usage";
  private static final String OUT_OF_WORLD = "commands.summon.outOfWorld";
  private static final String FAILED = "commands.summon.failed";
  private static final String SUCCESS = "commands.summon.success";
  private static final String INVALID_ID = "commands.onslaught.summon.invalid.template.id";

  private final DataStore dataStore;
  private final MobTemplateEntityFactory mobTemplateEntityFactory;

  public CommandSummon(DataStore dataStore, MobTemplateEntityFactory mobTemplateEntityFactory) {

    this.dataStore = dataStore;
    this.mobTemplateEntityFactory = mobTemplateEntityFactory;
  }

  @Nonnull
  @Override
  public String getName() {

    return "osummon";
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
      BlockPos blockpos = sender.getPosition();
      Vec3d vec3d = sender.getPositionVector();
      double x = vec3d.x;
      double y = vec3d.y;
      double z = vec3d.z;

      if (args.length >= 4) {
        x = parseDouble(x, args[1], true);
        y = parseDouble(y, args[2], false);
        z = parseDouble(z, args[3], true);
        blockpos = new BlockPos(x, y, z);
      }

      World world = sender.getEntityWorld();

      if (!world.isBlockLoaded(blockpos)) {
        throw new CommandException(OUT_OF_WORLD);

      } else {
        MobTemplate mobTemplate = this.dataStore.getMobTemplateRegistry().get(templateId);

        if (mobTemplate == null) {
          throw new CommandException(INVALID_ID, templateId);
        }

        Entity entity = this.mobTemplateEntityFactory.create(mobTemplate, world);

        if (entity == null) {
          throw new CommandException(FAILED);
        }

        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);

        if (!world.spawnEntity(entity)) {
          throw new CommandException(FAILED);
        }

        if (entity instanceof EntityLiving) {
          ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
        }

        notifyCommandListener(sender, this, SUCCESS);
      }
    }
  }

  @ParametersAreNonnullByDefault
  @Nonnull
  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {

    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, this.dataStore.getMobTemplateRegistry().getIdList());

    } else {
      return args.length > 1 && args.length <= 4 ? getTabCompletionCoordinate(args, 1, targetPos) : Collections.emptyList();
    }
  }
}
