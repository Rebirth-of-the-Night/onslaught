package com.codetaylor.mc.onslaught.modules.onslaught;

import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.AntiAirPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IAntiAirPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandReload;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandStartInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandStartRandomInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandStopAllInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandStopInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandSummon;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIChaseLongDistance;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIChaseLongDistanceGhast;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIPlayerTarget;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIAntiAirInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIAttackMeleeInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIChaseLongDistanceInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAICounterAttackInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIExplodeWhenStuckInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIInjectorBase;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAILungeInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIMiningInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIPlayerTargetInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.EffectApplicator;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.LootTableApplicator;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.MobTemplateEntityFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.EntityAIAntiAirPlayerTickEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.EntityAIInjectionEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.EntityAttachCapabilitiesEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionCleanupEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionClientUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionCommandEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionDimensionTravelRestrictionEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionKillCountUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionMessageEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionPlayerDataInitializationHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionSleepInhibitorEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.LootInjectionEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.LootTableManagerInjectionEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.client.EntityAIAntiAirClientEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.client.InvasionHudRenderEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.EntityInvasionDataRemover;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.EntityInvasionPeriodicWorldCleanup;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionClientHUDUpdateSender;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCommandExecutor;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCommandExecutorStaged;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCommandSender;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCommandStarter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCompletionPercentageCalculator;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionFinishedPredicate;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionKillCountUpdater;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionMessageSender;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionMessageSenderWarning;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerDataFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerTimerValueSupplier;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionSpawnDataConverterFunction;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionStopExecutor;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionTimestampFunction;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionWarningMessageTimestampFunction;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.InvasionHudRenderInfo;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.client.InvasionHudRenderer;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.SpawnSampler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.predicate.SpawnPredicateFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector.InvasionSelectorFunction;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.ActiveMobCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.ActiveWavePredicate;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.DeferredSpawnClientParticlePacketSender;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.DeferredSpawnData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.DeferredSpawnEffectApplicator;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.DeferredSpawner;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.EntityInvasionDataInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.Spawner;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.SpawnerMob;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.SpawnerMobForced;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.SpawnerWave;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.WaveDelayTimer;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.state.StateChangeActiveToWaiting;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.state.StateChangeEligibleToPending;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.state.StateChangePendingToActive;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.state.StateChangeWaitingToEligible;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.FilePathCreator;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.JsonFileLocator;
import com.codetaylor.mc.onslaught.modules.onslaught.loot.CustomLootTableManagerInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.loot.ExtraLootInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketAntiAir;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketDeferredSpawn;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketHudUpdate;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketHudUpdateHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.template.TemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.template.TemplateStore;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateAdapter;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.template.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.template.mob.MobTemplateAdapter;
import com.codetaylor.mc.onslaught.modules.onslaught.template.mob.MobTemplateLoader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleOnslaught extends ModuleBase {

  public static final String MOD_ID = ModOnslaught.MOD_ID;

  /** Holds a static reference to the module's packet service. */
  public static IPacketService PACKET_SERVICE;
  /** Holds the hud render info on the client. */
  private final List<InvasionHudRenderInfo> invasionHudRenderInfoList = new ArrayList<>(1);
  /**
   * Holds the commands constructed during pre-init for registration with the server when it starts.
   */
  private CommandBase[] commands;

  public ModuleOnslaught() {

    super(0, MOD_ID);

    PACKET_SERVICE = this.enableNetwork();
  }

  @Override
  public void onPreInitializationEvent(FMLPreInitializationEvent event) {

    super.onPreInitializationEvent(event);

    // -------------------------------------------------------------------------

    final TemplateStore templateStore = new TemplateStore();

    // -------------------------------------------------------------------------

    File modConfigurationDirectory = event.getModConfigurationDirectory();
    Path modConfigurationPath = modConfigurationDirectory.toPath();
    FilePathCreator filePathCreator = new FilePathCreator();

    // -------------------------------------------------------------------------
    // - Json Templates
    // -------------------------------------------------------------------------

    TemplateLoader templateLoader =
        new TemplateLoader(
            templateStore::setMobTemplateRegistry,
            templateStore::setInvasionTemplateRegistry,
            modConfigurationPath,
            filePathCreator,
            new JsonFileLocator(),
            new MobTemplateLoader(new MobTemplateAdapter()),
            new InvasionTemplateLoader(new InvasionTemplateAdapter()));

    templateLoader.load();

    // -------------------------------------------------------------------------
    // - Extra Loot Injection
    // -------------------------------------------------------------------------

    try {
      filePathCreator.initialize(modConfigurationPath.resolve(MOD_ID + "/loot"));

    } catch (IOException e) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error creating path: " + MOD_ID + "/loot");
      ModOnslaught.LOG.log(Level.SEVERE, e.getMessage(), e);
      throw new RuntimeException(e);
    }

    MinecraftForge.EVENT_BUS.register(
        new LootTableManagerInjectionEventHandler(
            new CustomLootTableManagerInjector(
                modConfigurationPath.resolve(MOD_ID + "/loot").toFile())));

    MinecraftForge.EVENT_BUS.register(new LootInjectionEventHandler(new ExtraLootInjector()));

    // -------------------------------------------------------------------------
    // - AI Injection
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(
        new EntityAIInjectionEventHandler(
            new EntityAIInjectorBase[] {
              new EntityAIPlayerTargetInjector(),
              new EntityAIChaseLongDistanceInjector(),
              new EntityAIMiningInjector(),
              new EntityAIAttackMeleeInjector(),
              new EntityAICounterAttackInjector(),
              new EntityAIExplodeWhenStuckInjector(),
              new EntityAILungeInjector(),
              new EntityAIAntiAirInjector()
            }));

    // The entity AI classes to strip from mobs during invasion cleanup
    Class<?>[] entityAIToRemoveOnCleanup = {
      EntityAIPlayerTarget.class,
      EntityAIChaseLongDistance.class,
      EntityAIChaseLongDistanceGhast.class
    };

    // -------------------------------------------------------------------------
    // - Entity Capability Injection
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(new EntityAttachCapabilitiesEventHandler());

    // -------------------------------------------------------------------------
    // - AntiAir
    // -------------------------------------------------------------------------

    CapabilityManager.INSTANCE.register(
        IAntiAirPlayerData.class, new AntiAirPlayerData(), AntiAirPlayerData::new);

    MinecraftForge.EVENT_BUS.register(new EntityAIAntiAirPlayerTickEventHandler());

    // -------------------------------------------------------------------------
    // - Invasion
    // -------------------------------------------------------------------------

    /*
    This is the set of players with an expired invasion timer. A LinkedHashSet is
    used to ensure retention of insertion order and eliminate duplicate elements.
    */
    Set<UUID> eligiblePlayers = new LinkedHashSet<>();

    /*
    List of deferred spawns.
     */
    List<DeferredSpawnData> deferredSpawnDataList = new ArrayList<>();

    SpawnPredicateFactory spawnPredicateFactory = new SpawnPredicateFactory();

    SpawnSampler spawnSampler = new SpawnSampler(spawnPredicateFactory);

    Function<String, InvasionTemplate> idToInvasionTemplateFunction =
        (id -> templateStore.getInvasionTemplateRegistry().get(id));
    Function<String, MobTemplate> idToMobTemplateFunction =
        (id -> templateStore.getMobTemplateRegistry().get(id));

    MobTemplateEntityFactory mobTemplateEntityFactory =
        new MobTemplateEntityFactory(new EffectApplicator(), new LootTableApplicator());

    EntityInvasionDataInjector entityInvasionDataInjector = new EntityInvasionDataInjector();
    InvasionSpawnDataConverterFunction invasionSpawnDataConverterFunction =
        new InvasionSpawnDataConverterFunction();

    InvasionPlayerDataFactory invasionPlayerDataFactory =
        new InvasionPlayerDataFactory(
            idToInvasionTemplateFunction, invasionSpawnDataConverterFunction);

    Function<EntityPlayerMP, String> invasionSelectorFunction =
        new InvasionSelectorFunction(
            () -> templateStore.getInvasionTemplateRegistry().getAll().stream(),
            id -> templateStore.getInvasionTemplateRegistry().has(id),
            () -> ModuleOnslaughtConfig.INVASION.DEFAULT_FALLBACK_INVASION);

    IntSupplier invasionPlayerTimerValueSupplier =
        new InvasionPlayerTimerValueSupplier(
            () -> ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[0],
            () -> ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[1]);

    InvasionCommandSender invasionCommandSender = new InvasionCommandSender();
    InvasionCompletionPercentageCalculator invasionCompletionPercentageCalculator =
        new InvasionCompletionPercentageCalculator();

    MinecraftForge.EVENT_BUS.register(
        new InvasionUpdateEventHandler(
            new InvasionUpdateEventHandler.IInvasionUpdateComponent[] {

              // State changes -----------------------------------------------

              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  13, new StateChangeWaitingToEligible(eligiblePlayers)),
              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  15,
                  new StateChangeEligibleToPending(
                      eligiblePlayers,
                      invasionSelectorFunction,
                      invasionPlayerDataFactory,
                      () -> ModuleOnslaughtConfig.INVASION.MAX_CONCURRENT_INVASIONS,
                      new InvasionCounter(),
                      new InvasionTimestampFunction(),
                      new InvasionWarningMessageTimestampFunction(
                          idToInvasionTemplateFunction,
                          () -> ModuleOnslaughtConfig.INVASION.DEFAULT_MESSAGE_WARNING_TICKS))),
              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  17, new StateChangePendingToActive()),
              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  18,
                  new StateChangeActiveToWaiting(
                      new InvasionStopExecutor(
                          invasionPlayerTimerValueSupplier, new InvasionFinishedPredicate()))),

              // Wave Timers -------------------------------------------------

              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  19, new WaveDelayTimer(new ActiveWavePredicate())),

              // Spawns ------------------------------------------------------

              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  20,
                  new Spawner(
                      idToInvasionTemplateFunction,
                      new SpawnerWave(
                          invasionSpawnDataConverterFunction,
                          new SpawnerMob(
                              spawnSampler,
                              idToMobTemplateFunction,
                              mobTemplateEntityFactory,
                              entityInvasionDataInjector),
                          new SpawnerMobForced(
                              spawnSampler,
                              idToMobTemplateFunction,
                              mobTemplateEntityFactory,
                              deferredSpawnDataList,
                              () -> ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_DELAY_TICKS),
                          new ActiveMobCounter(deferredSpawnDataList)))),
              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  21,
                  new DeferredSpawner(
                      entityInvasionDataInjector,
                      spawnPredicateFactory,
                      invasionSpawnDataConverterFunction,
                      idToMobTemplateFunction,
                      mobTemplateEntityFactory,
                      deferredSpawnDataList)),
              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  11,
                  new DeferredSpawnEffectApplicator(
                      deferredSpawnDataList,
                      new DeferredSpawnEffectApplicator.EffectListSupplier(
                          ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_EFFECTS),
                      () -> ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_EFFECT_DURATION_TICKS,
                      () -> ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_EFFECT_RANGE)),
              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  20, new DeferredSpawnClientParticlePacketSender(deferredSpawnDataList)),
              new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                  20 * 5,
                  new InvasionMessageSenderWarning(
                      new InvasionMessageSender(
                          idToInvasionTemplateFunction,
                          invasionTemplate -> invasionTemplate.messages.warn.message,
                          () -> ModuleOnslaughtConfig.INVASION.DEFAULT_MESSAGE_WARNING)))
            }));

    MinecraftForge.EVENT_BUS.register(
        new InvasionPlayerDataInitializationHandler(invasionPlayerTimerValueSupplier));

    MinecraftForge.EVENT_BUS.register(
        new InvasionKillCountUpdateEventHandler(new InvasionKillCountUpdater()));

    MinecraftForge.EVENT_BUS.register(
        new InvasionCleanupEventHandler(
            new EntityInvasionPeriodicWorldCleanup(
                () -> ModuleOnslaughtConfig.INVASION.OFFLINE_CLEANUP_DELAY_TICKS,
                new EntityInvasionDataRemover(entityAIToRemoveOnCleanup))));

    MinecraftForge.EVENT_BUS.register(
        new InvasionMessageEventHandler(
            new InvasionMessageSender(
                idToInvasionTemplateFunction,
                invasionTemplate -> invasionTemplate.messages.start,
                () -> ModuleOnslaughtConfig.INVASION.DEFAULT_MESSAGE_BEGIN),
            new InvasionMessageSender(
                idToInvasionTemplateFunction,
                invasionTemplate -> invasionTemplate.messages.end,
                () -> ModuleOnslaughtConfig.INVASION.DEFAULT_MESSAGE_END)));

    MinecraftForge.EVENT_BUS.register(new InvasionSleepInhibitorEventHandler());

    MinecraftForge.EVENT_BUS.register(new InvasionDimensionTravelRestrictionEventHandler());

    MinecraftForge.EVENT_BUS.register(
        new InvasionCommandEventHandler(
            new InvasionCommandExecutor(
                idToInvasionTemplateFunction,
                invasionTemplate -> invasionTemplate.commands.start,
                invasionCommandSender),
            new InvasionCommandExecutor(
                idToInvasionTemplateFunction,
                invasionTemplate -> invasionTemplate.commands.end,
                invasionCommandSender),
            new InvasionCommandExecutorStaged(
                idToInvasionTemplateFunction,
                invasionCompletionPercentageCalculator,
                invasionCommandSender)));

    MinecraftForge.EVENT_BUS.register(
        new InvasionClientUpdateEventHandler(
            new InvasionClientHUDUpdateSender(
                () -> ModuleOnslaughtConfig.CLIENT.INVASION_HUD_UPDATE_RANGE,
                invasionCompletionPercentageCalculator)));

    // -------------------------------------------------------------------------
    // - Commands
    // -------------------------------------------------------------------------

    /*
    Commands are constructed in pre-init to leverage dependency injection.
     */

    InvasionCommandStarter invasionCommandStarter =
        new InvasionCommandStarter(invasionPlayerDataFactory, eligiblePlayers);

    InvasionStopExecutor invasionStopActiveExecutor =
        new InvasionStopExecutor(
            invasionPlayerTimerValueSupplier,
            invasionPlayerData ->
                invasionPlayerData.getInvasionState()
                    == InvasionPlayerData.EnumInvasionState.Active);

    this.commands =
        new CommandBase[] {
          new CommandSummon(
              idToMobTemplateFunction,
              () -> templateStore.getMobTemplateRegistry().getIdList(),
              mobTemplateEntityFactory),
          new CommandReload(templateLoader),
          new CommandStartInvasion(
              invasionCommandStarter,
              idToInvasionTemplateFunction,
              () -> templateStore.getInvasionTemplateRegistry().getIdList()),
          new CommandStartRandomInvasion(invasionCommandStarter, invasionSelectorFunction),
          new CommandStopInvasion(invasionStopActiveExecutor),
          new CommandStopAllInvasion(invasionStopActiveExecutor)
        };
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void onClientPreInitializationEvent(FMLPreInitializationEvent event) {

    super.onClientPreInitializationEvent(event);

    // -------------------------------------------------------------------------
    // - AntiAir
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(new EntityAIAntiAirClientEventHandler());

    // -------------------------------------------------------------------------
    // - HUD Render
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(
        new InvasionHudRenderEventHandler(
            new InvasionHudRenderer(
                this.invasionHudRenderInfoList,
                () -> ModuleOnslaughtConfig.CLIENT.INVASION_HUD_POSITION_XY[0],
                () -> ModuleOnslaughtConfig.CLIENT.INVASION_HUD_POSITION_XY[1],
                () -> ModuleOnslaughtConfig.CLIENT.INVASION_HUD_BAR_WIDTH,
                () -> ModuleOnslaughtConfig.CLIENT.INVASION_HUD_BAR_COLOR_RGB)));
  }

  @Override
  public void onNetworkRegister(IPacketRegistry registry) {

    registry.register(SCPacketAntiAir.class, SCPacketAntiAir.class, Side.CLIENT);

    registry.register(SCPacketDeferredSpawn.class, SCPacketDeferredSpawn.class, Side.CLIENT);

    registry.register(
        new SCPacketHudUpdateHandler(this.invasionHudRenderInfoList),
        SCPacketHudUpdate.class,
        Side.CLIENT);
  }

  @Override
  public void onServerStartingEvent(FMLServerStartingEvent event) {

    super.onServerStartingEvent(event);

    // -------------------------------------------------------------------------
    // - Command Registration
    // -------------------------------------------------------------------------

    for (CommandBase command : this.commands) {
      event.registerServerCommand(command);
    }
  }
}
