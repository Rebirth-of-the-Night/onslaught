package com.codetaylor.mc.onslaught.modules.onslaught;

import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketRegistry;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.AntiAirPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IAntiAirPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandReload;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandSummon;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandTest;
import com.codetaylor.mc.onslaught.modules.onslaught.data.DataLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.data.DataStore;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateAdapter;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateAdapter;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.*;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.EffectApplicator;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.LootTableApplicator;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.MobTemplateEntityFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.event.*;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionKillCountUpdater;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionSpawnDataConverter;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.TickIntervalCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.SpawnSampler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.predicate.SpawnPredicateFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector.InvasionSelector;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner.*;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.state.*;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.FilePathCreator;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.JsonFileLocator;
import com.codetaylor.mc.onslaught.modules.onslaught.loot.CustomLootTableManagerInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.loot.ExtraLootInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketAntiAir;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

public class ModuleOnslaught
    extends ModuleBase {

  public static final String MOD_ID = ModOnslaught.MOD_ID;

  public static IPacketService PACKET_SERVICE;

  private final DataStore dataStore;
  private DataLoader dataLoader;

  public ModuleOnslaught() {

    super(0, MOD_ID);
    this.dataStore = new DataStore();

    PACKET_SERVICE = this.enableNetwork();
  }

  @Override
  public void onPreInitializationEvent(FMLPreInitializationEvent event) {

    super.onPreInitializationEvent(event);

    File modConfigurationDirectory = event.getModConfigurationDirectory();
    Path modConfigurationPath = modConfigurationDirectory.toPath();
    FilePathCreator filePathCreator = new FilePathCreator();

    // -------------------------------------------------------------------------
    // - Json Templates
    // -------------------------------------------------------------------------

    this.dataLoader = new DataLoader(
        this.dataStore::setMobTemplateRegistry,
        this.dataStore::setInvasionTemplateRegistry,
        modConfigurationPath,
        filePathCreator,
        new JsonFileLocator(),
        new MobTemplateLoader(
            new MobTemplateAdapter()
        ),
        new InvasionTemplateLoader(
            new InvasionTemplateAdapter()
        )
    );
    this.dataLoader.load();

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

    MinecraftForge.EVENT_BUS.register(new LootTableManagerInjectionEventHandler(
        new CustomLootTableManagerInjector(
            modConfigurationPath.resolve(MOD_ID + "/loot").toFile()
        )
    ));

    MinecraftForge.EVENT_BUS.register(new LootInjectionEventHandler(
        new ExtraLootInjector()
    ));

    // -------------------------------------------------------------------------
    // - AI Injection
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(new EntityAIInjectionEventHandler(
        new EntityAIPlayerTargetInjector(),
        new EntityAIChaseLongDistanceInjector(),
        new EntityAIMiningInjector(),
        new EntityAIAttackMeleeInjector(),
        new EntityAICounterAttackInjector(),
        new EntityAIExplodeWhenStuckInjector(),
        new EntityAILungeInjector(),
        new EntityAIAntiAirInjector()
    ));

    // -------------------------------------------------------------------------
    // - Entity Capability Injection
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(new EntityAttachCapabilitiesEventHandler());

    // -------------------------------------------------------------------------
    // - AntiAir
    // -------------------------------------------------------------------------

    CapabilityManager.INSTANCE.register(IAntiAirPlayerData.class, new AntiAirPlayerData(), AntiAirPlayerData::new);

    MinecraftForge.EVENT_BUS.register(new EntityAIAntiAirPlayerTickEventHandler());

    // -------------------------------------------------------------------------
    // - Invasion
    // -------------------------------------------------------------------------

    /*
    This is the set of players with an expired invasion timer. A LinkedHashSet is
    used to ensure retention of insertion order and eliminate duplicate elements.
     */
    LinkedHashSet<UUID> eligiblePlayers = new LinkedHashSet<>();

    /*
    List of deferred spawns.
     */
    List<DeferredSpawnData> deferredSpawnDataList = new ArrayList<>();

    SpawnPredicateFactory spawnPredicateFactory = new SpawnPredicateFactory();

    SpawnSampler spawnSampler = new SpawnSampler(
        spawnPredicateFactory
    );

    Function<String, InvasionTemplate> idToInvasionTemplateFunction = (id -> this.dataStore.getInvasionTemplateRegistry().get(id));
    Function<String, MobTemplate> idToMobTemplateFunction = (id -> this.dataStore.getMobTemplateRegistry().get(id));

    MobTemplateEntityFactory mobTemplateEntityFactory = new MobTemplateEntityFactory(
        new EffectApplicator(),
        new LootTableApplicator()
    );

    EntityInvasionDataInjector entityInvasionDataInjector = new EntityInvasionDataInjector();
    InvasionSpawnDataConverter invasionSpawnDataConverter = new InvasionSpawnDataConverter();

    MinecraftForge.EVENT_BUS.register(
        new InvasionUpdateEventHandler(
            new InvasionUpdateEventHandler.IInvasionUpdateComponent[]{

                // State changes

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    13,
                    new StateChangeWaitingToEligible(
                        eligiblePlayers
                    )
                ),

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    15,
                    new StateChangeEligibleToPending(
                        eligiblePlayers,
                        new InvasionSelector(
                            () -> this.dataStore.getInvasionTemplateRegistry().getAll().stream(),
                            id -> this.dataStore.getInvasionTemplateRegistry().has(id),
                            () -> ModuleOnslaughtConfig.INVASION.DEFAULT_FALLBACK_INVASION
                        ),
                        new InvasionPlayerDataFactory(
                            idToInvasionTemplateFunction,
                            invasionSpawnDataConverter
                        ),
                        () -> ModuleOnslaughtConfig.INVASION.MAX_CONCURRENT_INVASIONS,
                        new InvasionCounter()
                    )
                ),

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    17,
                    new StateChangePendingToActive()
                ),

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    18,
                    new StateChangeActiveToWaiting()
                ),

                // Wave Timers

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    19,
                    new WaveDelayTimer()
                ),

                // Spawns

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    20,
                    new Spawner(
                        idToInvasionTemplateFunction,
                        new SpawnerWave(
                            invasionSpawnDataConverter,
                            new SpawnerMob(
                                spawnSampler,
                                idToMobTemplateFunction,
                                mobTemplateEntityFactory,
                                entityInvasionDataInjector
                            ),
                            new SpawnerMobForced(
                                spawnSampler,
                                idToMobTemplateFunction,
                                mobTemplateEntityFactory,
                                deferredSpawnDataList,
                                () -> ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_DELAY_TICKS
                            ),
                            deferredSpawnDataList
                        )
                    )
                ),

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    21,
                    new DeferredSpawner(
                        entityInvasionDataInjector,
                        spawnPredicateFactory,
                        invasionSpawnDataConverter,
                        idToMobTemplateFunction,
                        mobTemplateEntityFactory,
                        deferredSpawnDataList
                    )
                ),

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    11,
                    new DeferredSpawnEffectApplicator(
                        deferredSpawnDataList,
                        new DeferredSpawnEffectApplicator.EffectListSupplier(
                            ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_EFFECTS
                        ),
                        () -> ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_EFFECT_DURATION_TICKS,
                        () -> ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_EFFECT_RANGE
                    )
                ),

                new InvasionUpdateEventHandler.InvasionTimedUpdateComponent(
                    20,
                    new DeferredSpawnClientParticlePacketSender(
                        deferredSpawnDataList
                    )
                )
            }
        )
    );

    MinecraftForge.EVENT_BUS.register(new InvasionKillCountUpdateEventHandler(
        new InvasionKillCountUpdater()
    ));
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void onClientPreInitializationEvent(FMLPreInitializationEvent event) {

    super.onClientPreInitializationEvent(event);

    // -------------------------------------------------------------------------
    // - AntiAir
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(new EntityAIAntiAirClientEventHandler());
  }

  @Override
  public void onNetworkRegister(IPacketRegistry registry) {

    registry.register(
        SCPacketAntiAir.class,
        SCPacketAntiAir.class,
        Side.CLIENT
    );
  }

  @Override
  public void onServerStartingEvent(FMLServerStartingEvent event) {

    super.onServerStartingEvent(event);

    // -------------------------------------------------------------------------
    // - Command Registration
    // -------------------------------------------------------------------------

    event.registerServerCommand(new CommandSummon(
        id -> this.dataStore.getMobTemplateRegistry().get(id),
        () -> this.dataStore.getMobTemplateRegistry().getIdList(),
        new MobTemplateEntityFactory(
            new EffectApplicator(),
            new LootTableApplicator()
        )
    ));

    event.registerServerCommand(new CommandReload(
        this.dataLoader
    ));

    event.registerServerCommand(new CommandTest());
  }
}
