package com.xxsx.fantasyearth;

import com.mojang.logging.LogUtils;
import com.xxsx.earthlore.registry.LoreContent;
import com.xxsx.fantasyearth.client.FantasyEarthClient;
import com.xxsx.fantasyearth.entity.AetherFoxEntity;
import com.xxsx.fantasyearth.entity.ArcaneSettlerEntity;
import com.xxsx.fantasyearth.entity.ContractGolemEntity;
import com.xxsx.fantasyearth.entity.ContractableFamiliarEntity;
import com.xxsx.fantasyearth.entity.CrystalArmoredSpiderEntity;
import com.xxsx.fantasyearth.entity.ManaWispEntity;
import com.xxsx.fantasyearth.entity.RuneWolfEntity;
import com.xxsx.fantasyearth.entity.RunicWatcherEntity;
import com.xxsx.fantasyearth.settlement.MagicSettlementAnchorBlock;
import com.xxsx.fantasyearth.settlement.MagicSettlementAnchorBlockEntity;
import com.xxsx.fantasyearth.settlement.MagicSettlementCatalog;
import com.xxsx.fantasyearth.settlement.MagicSettlementFeature;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(FantasyEarth.MODID)
public class FantasyEarth {
    public static final String MODID = "fantasy_earth";
    private static final String LEGACY_MODID = "earth_online_magic";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    private static final List<ItemLike> TAB_ITEMS = new ArrayList<>();
    private static final List<DeferredBlock<MagicMachineBlock>> MACHINE_BLOCKS = new ArrayList<>();

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AetherCrystalComponent>> AETHER_CRYSTAL_COMPONENT =
            DATA_COMPONENTS.registerComponentType("aether_crystal", builder -> builder.persistent(AetherCrystalComponent.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RuneItemComponent>> RUNE_ITEM_COMPONENT =
            DATA_COMPONENTS.registerComponentType("rune_item", builder -> builder.persistent(RuneItemComponent.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArcaneFocusComponent>> ARCANE_FOCUS_COMPONENT =
            DATA_COMPONENTS.registerComponentType("arcane_focus", builder -> builder.persistent(ArcaneFocusComponent.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RitualCoreComponent>> RITUAL_CORE_COMPONENT =
            DATA_COMPONENTS.registerComponentType("ritual_core", builder -> builder.persistent(RitualCoreComponent.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ConstructCoreComponent>> CONSTRUCT_CORE_COMPONENT =
            DATA_COMPONENTS.registerComponentType("construct_core", builder -> builder.persistent(ConstructCoreComponent.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpellScrollComponent>> SPELL_SCROLL_COMPONENT =
            DATA_COMPONENTS.registerComponentType("spell_scroll", builder -> builder.persistent(SpellScrollComponent.CODEC));

    public static final DeferredBlock<MagicMachineBlock> ALCHEMY_TABLE = machineBlock("alchemy_table", MagicMachineBlock.Kind.ALCHEMY_TABLE, MapColor.WOOD, SoundType.WOOD, 2.5F, "tooltip.fantasy_earth.alchemy_table");
    public static final DeferredBlock<MagicMachineBlock> RUNE_CARVING_TABLE = machineBlock("rune_carving_table", MagicMachineBlock.Kind.RUNE_CARVING_TABLE, MapColor.STONE, SoundType.STONE, 3.0F, "tooltip.fantasy_earth.rune_carving_table");
    public static final DeferredBlock<MagicMachineBlock> RITUAL_PEDESTAL = machineBlock("ritual_pedestal", MagicMachineBlock.Kind.RITUAL_PEDESTAL, MapColor.COLOR_PURPLE, SoundType.STONE, 4.0F, "tooltip.fantasy_earth.ritual_pedestal");
    public static final DeferredBlock<Block> AETHER_CRYSTAL_CLUSTER = simpleBlock("aether_crystal_cluster", MapColor.COLOR_LIGHT_BLUE, SoundType.AMETHYST, 2.0F, "tooltip.fantasy_earth.aether_crystal_cluster");
    public static final DeferredBlock<ArcaneFocusMatBlock> ARCANE_FOCUS_MAT = focusMatBlock("arcane_focus_mat", MapColor.COLOR_PURPLE, SoundType.WOOL, 0.25F, "tooltip.fantasy_earth.arcane_focus_mat");
    public static final DeferredBlock<AetherBatteryBlock> AETHER_BATTERY = BLOCKS.registerBlock("aether_battery", AetherBatteryBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(3.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.COPPER));
    public static final DeferredItem<?> AETHER_BATTERY_ITEM = registerBlockItem("aether_battery", AETHER_BATTERY, "tooltip.fantasy_earth.aether_battery");
    public static final DeferredBlock<Block> RUNE_STONE = BLOCKS.registerBlock("rune_stone", Block::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE));
    public static final DeferredItem<?> RUNE_STONE_ITEM = registerBlockItem("rune_stone", RUNE_STONE, "tooltip.fantasy_earth.rune_stone");
    public static final DeferredBlock<RitualCandleBlock> RITUAL_CANDLE = BLOCKS.registerBlock("ritual_candle", RitualCandleBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.2F, 0.5F).sound(SoundType.CANDLE).noOcclusion().lightLevel(s -> s.getValue(RitualCandleBlock.LIT) ? 12 : 0));
    public static final DeferredItem<?> RITUAL_CANDLE_ITEM = registerBlockItem("ritual_candle", RITUAL_CANDLE, "tooltip.fantasy_earth.ritual_candle");
    public static final DeferredBlock<OfferingBowlBlock> OFFERING_BOWL = BLOCKS.registerBlock("offering_bowl", OfferingBowlBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 3.0F).sound(SoundType.STONE).noOcclusion());
    public static final DeferredItem<?> OFFERING_BOWL_ITEM = registerBlockItem("offering_bowl", OFFERING_BOWL, "tooltip.fantasy_earth.offering_bowl");
    public static final DeferredBlock<RitualCircleMarkerBlock> RITUAL_CIRCLE_MARKER = BLOCKS.registerBlock("ritual_circle_marker", RitualCircleMarkerBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).strength(0.1F, 0.2F).sound(SoundType.CALCITE).noOcclusion().noCollision());
    public static final DeferredItem<?> RITUAL_CIRCLE_MARKER_ITEM = registerBlockItemStacksTo("ritual_circle_marker", RITUAL_CIRCLE_MARKER, "tooltip.fantasy_earth.ritual_circle_marker", 16);
    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneSeatEntity>> ARCANE_SEAT =
            ENTITY_TYPES.register("arcane_seat", () -> EntityType.Builder
                    .of(ArcaneSeatEntity::new, MobCategory.MISC)
                    .sized(0.01F, 0.01F)
                    .passengerAttachments(new Vec3(0.0D, 0.17D, 0.0D))
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .noSummon()
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("arcane_seat"))));
    public static final DeferredHolder<EntityType<?>, EntityType<RunicWatcherEntity>> RUNIC_WATCHER =
            ENTITY_TYPES.register("runic_watcher", () -> EntityType.Builder
                    .of(RunicWatcherEntity::new, MobCategory.MONSTER)
                    .sized(0.78F, 1.85F)
                    .eyeHeight(1.62F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("runic_watcher"))));
    public static final DeferredHolder<EntityType<?>, EntityType<AetherFoxEntity>> AETHER_FOX =
            ENTITY_TYPES.register("aether_fox", () -> EntityType.Builder
                    .of(AetherFoxEntity::new, MobCategory.CREATURE)
                    .sized(0.78F, 0.82F)
                    .eyeHeight(0.61F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("aether_fox"))));
    public static final DeferredHolder<EntityType<?>, EntityType<RuneWolfEntity>> RUNE_WOLF =
            ENTITY_TYPES.register("rune_wolf", () -> EntityType.Builder
                    .of(RuneWolfEntity::new, MobCategory.CREATURE)
                    .sized(0.92F, 1.12F)
                    .eyeHeight(0.84F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("rune_wolf"))));
    public static final DeferredHolder<EntityType<?>, EntityType<CrystalArmoredSpiderEntity>> CRYSTAL_ARMORED_SPIDER =
            ENTITY_TYPES.register("crystal_armored_spider", () -> EntityType.Builder
                    .of(CrystalArmoredSpiderEntity::new, MobCategory.MONSTER)
                    .sized(1.45F, 0.9F)
                    .eyeHeight(0.56F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("crystal_armored_spider"))));
    public static final DeferredHolder<EntityType<?>, EntityType<ManaWispEntity>> MANA_WISP =
            ENTITY_TYPES.register("mana_wisp", () -> EntityType.Builder
                    .of(ManaWispEntity::new, MobCategory.CREATURE)
                    .sized(0.48F, 0.72F)
                    .eyeHeight(0.42F)
                    .clientTrackingRange(12)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("mana_wisp"))));
    public static final DeferredHolder<EntityType<?>, EntityType<ArcaneSettlerEntity>> ARCANE_SETTLER =
            ENTITY_TYPES.register("arcane_settler", () -> EntityType.Builder
                    .of(ArcaneSettlerEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.62F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("arcane_settler"))));
    public static final DeferredHolder<EntityType<?>, EntityType<ContractGolemEntity>> CONTRACT_GOLEM =
            ENTITY_TYPES.register("contract_golem", () -> EntityType.Builder
                    .of(ContractGolemEntity::new, MobCategory.CREATURE)
                    .sized(1.3F, 2.2F)
                    .eyeHeight(1.85F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id("contract_golem"))));
    public static final DeferredBlock<MagicSettlementAnchorBlock> SETTLEMENT_ANCHOR =
            BLOCKS.registerBlock("settlement_anchor", MagicSettlementAnchorBlock::new,
                    () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                            .strength(5.0F, 10.0F).requiresCorrectToolForDrops()
                            .sound(SoundType.AMETHYST).noOcclusion());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagicMachineBlockEntity>> MAGIC_MACHINE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("magic_machine", () -> new BlockEntityType<>(
                    MagicMachineBlockEntity::new,
                    MACHINE_BLOCKS.stream().map(DeferredBlock::get).toArray(Block[]::new)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagicSettlementAnchorBlockEntity>> SETTLEMENT_ANCHOR_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("settlement_anchor", () -> new BlockEntityType<>(
                    MagicSettlementAnchorBlockEntity::new, SETTLEMENT_ANCHOR.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<MagicMachineMenu>> MAGIC_MACHINE_MENU =
            MENUS.register("magic_machine", () -> IMenuTypeExtension.create(MagicMachineMenu::new));

    public static final DeferredItem<ArcaneNotebookItem> FIELD_ARCANE_NOTEBOOK = ITEMS.registerItem("field_arcane_notebook", ArcaneNotebookItem::new, props -> props.stacksTo(1));
    public static final DeferredItem<ArcaneInitiationNotesItem> ARCANE_INITIATION_NOTES = ITEMS.registerItem("arcane_initiation_notes", ArcaneInitiationNotesItem::new,
            props -> props.stacksTo(1).component(LoreContent.LORE_TEXT.get(), ArcaneInitiationNotesItem.defaultLoreText()));
    public static final DeferredItem<ArcaneAdaptationNotesItem> ARCANE_BODY_WARD_NOTES = ITEMS.registerItem("arcane_body_ward_notes",
            props -> new ArcaneAdaptationNotesItem(props.stacksTo(1), ArcaneAdaptationNotesItem.Type.BODY_WARD),
            props -> props.component(LoreContent.LORE_TEXT.get(), ArcaneAdaptationNotesItem.Type.BODY_WARD.defaultLoreText()));
    public static final DeferredItem<ArcaneAdaptationNotesItem> ARCANE_BREATH_WARD_NOTES = ITEMS.registerItem("arcane_breath_ward_notes",
            props -> new ArcaneAdaptationNotesItem(props.stacksTo(1), ArcaneAdaptationNotesItem.Type.BREATH_WARD),
            props -> props.component(LoreContent.LORE_TEXT.get(), ArcaneAdaptationNotesItem.Type.BREATH_WARD.defaultLoreText()));
    public static final DeferredItem<ArcaneAdaptationNotesItem> EVOCATION_NOTES = ITEMS.registerItem("evocation_notes",
            props -> new ArcaneAdaptationNotesItem(props.stacksTo(1), ArcaneAdaptationNotesItem.Type.EVOCATION),
            props -> props.component(LoreContent.LORE_TEXT.get(), ArcaneAdaptationNotesItem.Type.EVOCATION.defaultLoreText()));
    public static final DeferredItem<ArcaneAdaptationNotesItem> ILLUSION_NOTES = ITEMS.registerItem("illusion_notes",
            props -> new ArcaneAdaptationNotesItem(props.stacksTo(1), ArcaneAdaptationNotesItem.Type.ILLUSION),
            props -> props.component(LoreContent.LORE_TEXT.get(), ArcaneAdaptationNotesItem.Type.ILLUSION.defaultLoreText()));
    public static final DeferredItem<ArcaneAdaptationNotesItem> TRANSLOCATION_NOTES = ITEMS.registerItem("translocation_notes",
            props -> new ArcaneAdaptationNotesItem(props.stacksTo(1), ArcaneAdaptationNotesItem.Type.TRANSLOCATION),
            props -> props.component(LoreContent.LORE_TEXT.get(), ArcaneAdaptationNotesItem.Type.TRANSLOCATION.defaultLoreText()));
    public static final DeferredItem<ArcaneResearchNotesItem> ARCANE_RESEARCH_NOTES = ITEMS.registerItem("arcane_research_notes",
            ArcaneResearchNotesItem::new, props -> props.stacksTo(1));
    public static final DeferredItem<Item> ARCANE_DUST = materialItem("arcane_dust", "tooltip.fantasy_earth.arcane_dust");
    public static final DeferredItem<Item> RUNE_INK = materialItem("rune_ink", "tooltip.fantasy_earth.rune_ink");
    public static final DeferredItem<Item> RITUAL_CHALK = materialItem("ritual_chalk", "tooltip.fantasy_earth.ritual_chalk");
    public static final DeferredItem<Item> CRYSTALLIZED_MANA_SALT = manaItem("crystallized_mana_salt", "tooltip.fantasy_earth.crystallized_mana_salt", 22.0D);
    public static final DeferredItem<Item> AETHER_GLASS = materialItem("aether_glass", "tooltip.fantasy_earth.aether_glass");
    public static final DeferredItem<Item> RUNE_COPPER_PLATE = materialItem("rune_copper_plate", "tooltip.fantasy_earth.rune_copper_plate");
    public static final DeferredItem<AetherCrystalItem> AETHER_CRYSTAL = registerCrystalItem();
    public static final DeferredItem<Item> AETHER_SHARD = materialItem("aether_shard", "tooltip.fantasy_earth.aether_shard");
    public static final DeferredItem<Item> AETHER_LENS = materialItemStacksTo("aether_lens", "tooltip.fantasy_earth.aether_lens", 8);
    public static final DeferredItem<Item> AETHER_CONDUIT = materialItemStacksTo("aether_conduit", "tooltip.fantasy_earth.aether_conduit", 32);
    public static final DeferredItem<Item> RUNE_BLANK = materialItemStacksTo("rune_blank", "tooltip.fantasy_earth.rune_blank", 16);
    public static final DeferredItem<RuneStoneItem> RUNE_POWER = runeItem("rune_power", "power", "tooltip.fantasy_earth.rune_power");
    public static final DeferredItem<RuneStoneItem> RUNE_GUARD = runeItem("rune_guard", "guard", "tooltip.fantasy_earth.rune_guard");
    public static final DeferredItem<RuneStoneItem> RUNE_FLOW = runeItem("rune_flow", "flow", "tooltip.fantasy_earth.rune_flow");
    public static final DeferredItem<RuneStoneItem> RUNE_BIND = runeItem("rune_bind", "bind", "tooltip.fantasy_earth.rune_bind");
    public static final DeferredItem<RuneStoneItem> RUNE_JADE = runeItem("rune_jade", "jade", "tooltip.fantasy_earth.rune_jade");
    public static final DeferredItem<Item> JADE_FOCUS_HOUSING = materialItemStacksTo("jade_focus_housing", "tooltip.fantasy_earth.jade_focus_housing", 1);
    public static final DeferredItem<Item> DORMANT_RITUAL_CORE = materialItem("dormant_ritual_core", "tooltip.fantasy_earth.dormant_ritual_core");
    public static final DeferredItem<Item> INSCRIBED_RITUAL_CORE = materialItemStacksTo("inscribed_ritual_core", "tooltip.fantasy_earth.inscribed_ritual_core", 1);
    public static final DeferredItem<ConstructCoreItem> CONSTRUCT_CORE = registerConstructCoreItem();
    public static final DeferredItem<ArcaneFocusItem> ARCANE_FOCUS_ITEM = registerArcaneFocusItem();
    public static final DeferredItem<SpellScrollItem> SPELL_SCROLL = registerSpellScrollItem();
    public static final DeferredItem<Item> FAMILIAR_CONTRACT = materialItem(
            "familiar_contract", "tooltip.fantasy_earth.familiar_contract");
    public static final DeferredItem<SpawnEggItem> RUNIC_WATCHER_SPAWN_EGG = spawnEgg("runic_watcher_spawn_egg", RUNIC_WATCHER);
    public static final DeferredItem<SpawnEggItem> AETHER_FOX_SPAWN_EGG = spawnEgg("aether_fox_spawn_egg", AETHER_FOX);
    public static final DeferredItem<SpawnEggItem> RUNE_WOLF_SPAWN_EGG = spawnEgg("rune_wolf_spawn_egg", RUNE_WOLF);
    public static final DeferredItem<SpawnEggItem> CRYSTAL_ARMORED_SPIDER_SPAWN_EGG = spawnEgg(
            "crystal_armored_spider_spawn_egg", CRYSTAL_ARMORED_SPIDER);
    public static final DeferredItem<SpawnEggItem> MANA_WISP_SPAWN_EGG = spawnEgg("mana_wisp_spawn_egg", MANA_WISP);
    public static final DeferredItem<SpawnEggItem> ARCANE_SETTLER_SPAWN_EGG = spawnEgg("arcane_settler_spawn_egg", ARCANE_SETTLER);

    public static final DeferredHolder<Feature<?>, MagicSettlementFeature> WITCH_HAMLET_FEATURE =
            FEATURES.register("witch_hamlet", () -> new MagicSettlementFeature(
                    NoneFeatureConfiguration.CODEC, MagicSettlementFeature.Type.WITCH_HAMLET));
    public static final DeferredHolder<Feature<?>, MagicSettlementFeature> GOBLIN_EXCHANGE_FEATURE =
            FEATURES.register("goblin_exchange", () -> new MagicSettlementFeature(
                    NoneFeatureConfiguration.CODEC, MagicSettlementFeature.Type.GOBLIN_EXCHANGE));
    public static final DeferredHolder<Feature<?>, MagicSettlementFeature> ACADEMY_OUTPOST_FEATURE =
            FEATURES.register("academy_outpost", () -> new MagicSettlementFeature(
                    NoneFeatureConfiguration.CODEC, MagicSettlementFeature.Type.ACADEMY_OUTPOST));
    public static final DeferredHolder<Feature<?>, MagicSettlementFeature> DWARVEN_DELVING_FEATURE =
            FEATURES.register("dwarven_delving", () -> new MagicSettlementFeature(
                    NoneFeatureConfiguration.CODEC, MagicSettlementFeature.Type.DWARVEN_DELVING));
    public static final DeferredHolder<Feature<?>, MagicSettlementFeature> ELVEN_GROVE_FEATURE =
            FEATURES.register("elven_grove", () -> new MagicSettlementFeature(
                    NoneFeatureConfiguration.CODEC, MagicSettlementFeature.Type.ELVEN_GROVE));

    public FantasyEarth(IEventBus modBus) {
        registerLegacyAliases();
        DATA_COMPONENTS.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        ENTITY_TYPES.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        MENUS.register(modBus);
        FEATURES.register(modBus);
        FantasyEarthParticles.PARTICLE_TYPES.register(modBus);
        FantasyLoreIntegration.init();
        modBus.addListener(ArcaneNetwork::registerPayloads);
        modBus.addListener(this::registerEntityAttributes);
        modBus.addListener(this::registerSpawnPlacements);
        NeoForge.EVENT_BUS.addListener(ArcanaEvents::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(FantasyLoreIntegration::onLoreLearned);
        NeoForge.EVENT_BUS.addListener(ArcanaPassiveEffects::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ArcanaPassiveEffects::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(ArcanaPassiveEffects::onFall);
        NeoForge.EVENT_BUS.addListener(ArcanaPassiveEffects::onBreathe);
        NeoForge.EVENT_BUS.addListener(MagicSettlementCatalog::register);
        NeoForge.EVENT_BUS.addListener(SpellGraphLoader::register);
        TAB_ITEMS.add(FIELD_ARCANE_NOTEBOOK);
        TAB_ITEMS.add(ARCANE_INITIATION_NOTES);
        TAB_ITEMS.add(ARCANE_BODY_WARD_NOTES);
        TAB_ITEMS.add(ARCANE_BREATH_WARD_NOTES);
        TAB_ITEMS.add(EVOCATION_NOTES);
        TAB_ITEMS.add(ILLUSION_NOTES);
        TAB_ITEMS.add(TRANSLOCATION_NOTES);
        TAB_ITEMS.add(ARCANE_RESEARCH_NOTES);
        modBus.addListener(this::registerCreativeTab);
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            FantasyEarthClient.register(modBus);
        }
        LOGGER.info("[Fantasy Earth] NeoForge 26.1.2 module loaded");
    }

    private static DeferredBlock<MagicMachineBlock> machineBlock(String id, MagicMachineBlock.Kind kind, MapColor color,
                                                                 SoundType sound, float strength, String hintKey) {
        DeferredBlock<MagicMachineBlock> block = BLOCKS.registerBlock(id, p -> new MagicMachineBlock(p, kind), () -> BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength, strength * 2.0F)
                .requiresCorrectToolForDrops()
                .sound(sound));
        DeferredItem<?> item = ITEMS.registerItem(id,
                props -> new MagicBlockItem(block.get(), props, hintKey),
                props -> props);
        MACHINE_BLOCKS.add(block);
        TAB_ITEMS.add(item);
        return block;
    }

    private static DeferredBlock<Block> simpleBlock(String id, MapColor color, SoundType sound, float strength, String hintKey) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, MagicFieldBlock::new, () -> BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength, strength * 2.0F)
                .requiresCorrectToolForDrops()
                .lightLevel(s -> 9)
                .sound(sound));
        DeferredItem<?> item = ITEMS.registerItem(id,
                props -> new MagicBlockItem(block.get(), props, hintKey),
                props -> props);
        TAB_ITEMS.add(item);
        return block;
    }

    private static DeferredBlock<ArcaneFocusMatBlock> focusMatBlock(String id, MapColor color, SoundType sound, float strength, String hintKey) {
        DeferredBlock<ArcaneFocusMatBlock> block = BLOCKS.registerBlock(id, ArcaneFocusMatBlock::new, () -> BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(strength, strength * 2.0F)
                .sound(sound)
                .noOcclusion());
        DeferredItem<?> item = ITEMS.registerItem(id,
                props -> new MagicBlockItem(block.get(), props, hintKey),
                props -> props);
        TAB_ITEMS.add(item);
        return block;
    }

    private static DeferredItem<Item> materialItem(String id, String hintKey) {
        return materialItemStacksTo(id, hintKey, 64);
    }

    private static DeferredItem<Item> materialItemStacksTo(String id, String hintKey, int stackSize) {
        DeferredItem<Item> item = ITEMS.registerItem(id, props -> new MagicMaterialItem(props, hintKey), props -> props.stacksTo(stackSize));
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<?> registerBlockItem(String id, DeferredBlock<? extends Block> block, String hintKey) {
        return registerBlockItemStacksTo(id, block, hintKey, 64);
    }

    private static DeferredItem<?> registerBlockItemStacksTo(String id, DeferredBlock<? extends Block> block, String hintKey, int stackSize) {
        DeferredItem<?> item = ITEMS.registerItem(id,
                props -> new MagicBlockItem(block.get(), props, hintKey),
                props -> props.stacksTo(stackSize));
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<AetherCrystalItem> registerCrystalItem() {
        DeferredItem<AetherCrystalItem> item = ITEMS.registerItem("aether_crystal",
                props -> new AetherCrystalItem(props, "tooltip.fantasy_earth.aether_crystal"),
                props -> props.stacksTo(1));
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<RuneStoneItem> runeItem(String id, String runeKind, String hintKey) {
        DeferredItem<RuneStoneItem> item = ITEMS.registerItem(id,
                props -> new RuneStoneItem(props, runeKind, hintKey),
                props -> props.stacksTo(16));
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<ConstructCoreItem> registerConstructCoreItem() {
        DeferredItem<ConstructCoreItem> item = ITEMS.registerItem("construct_core",
                props -> new ConstructCoreItem(props, "tooltip.fantasy_earth.construct_core"),
                props -> props.stacksTo(1));
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<ArcaneFocusItem> registerArcaneFocusItem() {
        DeferredItem<ArcaneFocusItem> item = ITEMS.registerItem("arcane_focus",
                props -> new ArcaneFocusItem(props, "tooltip.fantasy_earth.arcane_focus"),
                props -> props.stacksTo(1));
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<SpellScrollItem> registerSpellScrollItem() {
        DeferredItem<SpellScrollItem> item = ITEMS.registerItem("spell_scroll",
                props -> new SpellScrollItem(props, "tooltip.fantasy_earth.spell_scroll"),
                props -> props.stacksTo(16));
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<Item> manaItem(String id, String hintKey, double restoreAmount) {
        DeferredItem<Item> item = ITEMS.registerItem(id, props -> new ManaConsumableItem(props, hintKey, restoreAmount), props -> props);
        TAB_ITEMS.add(item);
        return item;
    }

    private static DeferredItem<SpawnEggItem> spawnEgg(String id,
                                                       DeferredHolder<EntityType<?>, ? extends EntityType<?>> entityType) {
        DeferredItem<SpawnEggItem> item = ITEMS.registerItem(id,
                props -> new SpawnEggItem(props.spawnEgg(entityType.get())),
                props -> props);
        TAB_ITEMS.add(item);
        return item;
    }

    private void registerCreativeTab(RegisterEvent event) {
        var registry = event.getRegistry(Registries.CREATIVE_MODE_TAB);
        if (registry != null) {
            registry.addAlias(
                    Identifier.fromNamespaceAndPath(LEGACY_MODID, "main"),
                    id("main"));
        }
        event.register(Registries.CREATIVE_MODE_TAB, helper -> helper.register(id("main"),
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.fantasy_earth"))
                        .icon(() -> new ItemStack(FIELD_ARCANE_NOTEBOOK.get()))
                        .displayItems((params, output) -> TAB_ITEMS.forEach(output::accept))
                        .build()));
    }

    private static void registerLegacyAliases() {
        addLegacyAliases(BLOCKS);
        addLegacyAliases(ITEMS);
        addLegacyAliases(ENTITY_TYPES);
        addLegacyAliases(BLOCK_ENTITY_TYPES);
        addLegacyAliases(MENUS);
        addLegacyAliases(FEATURES);
    }

    private static void addLegacyAliases(DeferredRegister<?> register) {
        for (var entry : register.getEntries()) {
            Identifier current = entry.getId();
            register.addAlias(
                    Identifier.fromNamespaceAndPath(LEGACY_MODID, current.getPath()),
                    current);
        }
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(RUNIC_WATCHER.get(), RunicWatcherEntity.createAttributes().build());
        event.put(AETHER_FOX.get(), AetherFoxEntity.createAttributes().build());
        event.put(RUNE_WOLF.get(), RuneWolfEntity.createAttributes().build());
        event.put(CRYSTAL_ARMORED_SPIDER.get(), CrystalArmoredSpiderEntity.createAttributes().build());
        event.put(MANA_WISP.get(), ManaWispEntity.createAttributes().build());
        event.put(ARCANE_SETTLER.get(), ArcaneSettlerEntity.createAttributes().build());
        event.put(CONTRACT_GOLEM.get(), ContractGolemEntity.createAttributes().build());
    }

    private void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(RUNIC_WATCHER.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                RunicWatcherEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(AETHER_FOX.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ContractableFamiliarEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(RUNE_WOLF.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ContractableFamiliarEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(CRYSTAL_ARMORED_SPIDER.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                CrystalArmoredSpiderEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(MANA_WISP.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                ContractableFamiliarEntity::checkSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
