package com.xxsx.fantasyearth;

import com.xxsx.earthlore.api.GateResult;
import com.xxsx.earthlore.api.Grade;
import com.xxsx.earthlore.api.GradeAccess;
import com.xxsx.earthlore.api.LoreApi;
import com.xxsx.earthlore.api.LoreLearnedEvent;
import com.xxsx.earthlore.api.LoreMachines;
import com.xxsx.earthlore.api.LoreMigrator;
import com.xxsx.earthlore.data.LoreText;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Bridges fantasy_earth with earth_lore (03 册 & 15 册 §2):
 * - Registers research condition provider 'aether'
 * - Registers save migrator 'fantasy_earth:arcana_v1'
 * - Registers RuneItemComponent GradeAccess
 * - Listens to LoreLearnedEvent to grant legacy research levels and bonuses on study/grant
 */
public final class FantasyLoreIntegration {
    public static final Identifier DISCIPLINE_MAG = Identifier.fromNamespaceAndPath("earth_lore", "mag");

    public static final Identifier K_MAG_01_ARCANE_INITIATION = node("arcane_initiation");
    public static final Identifier K_MAG_02_ARCANE_RESEARCH_2 = node("arcane_research_2");
    public static final Identifier K_MAG_03_ARCANE_RESEARCH_3 = node("arcane_research_3");
    public static final Identifier K_MAG_04_ARCANE_RESEARCH_4 = node("arcane_research_4");
    public static final Identifier K_MAG_05_ARCANE_RESEARCH_5 = node("arcane_research_5");
    public static final Identifier K_MAG_06_AETHER_CRYSTALS = node("aether_crystals");
    public static final Identifier K_MAG_07_BODY_WARD = node("body_ward");
    public static final Identifier K_MAG_08_BREATH_WARD = node("breath_ward");
    public static final Identifier K_MAG_09_RUNE_CARVING = node("rune_carving");
    public static final Identifier K_MAG_10_RUNE_COMBINATION = node("rune_combination");
    public static final Identifier K_MAG_11_JADE_RUNES = node("jade_runes");
    public static final Identifier K_MAG_12_GEM_SOCKETING = node("gem_socketing");
    public static final Identifier K_MAG_13_RITUALS = node("rituals");
    public static final Identifier K_MAG_14_FOCI_AND_SCROLLS = node("foci_and_scrolls");
    public static final Identifier K_MAG_15_FAMILIARS = node("familiars");
    public static final Identifier K_MAG_16_EVOCATION = node("evocation");
    public static final Identifier K_MAG_17_ILLUSION = node("illusion");
    public static final Identifier K_MAG_18_TRANSLOCATION = node("translocation");
    public static final Identifier K_MAG_19_ALCHEMY = node("alchemy");
    public static final Identifier K_MAG_20_AETHER_STORAGE = node("aether_storage");
    public static final Identifier K_MAG_21_CONSTRUCTS = node("constructs");
    public static final Identifier K_MAG_22_AETHER_SENSING = node("aether_sensing");
    public static final Identifier K_MAG_23_MAGIC_MASTERY = node("magic_mastery");

    private static final Identifier[] RESEARCH_LEVEL_NODES = {
            K_MAG_01_ARCANE_INITIATION,
            K_MAG_02_ARCANE_RESEARCH_2,
            K_MAG_03_ARCANE_RESEARCH_3,
            K_MAG_04_ARCANE_RESEARCH_4,
            K_MAG_05_ARCANE_RESEARCH_5
    };

    private FantasyLoreIntegration() {
    }

    public static Identifier node(String slug) {
        return Identifier.fromNamespaceAndPath(FantasyEarth.MODID, "mag/" + slug);
    }

    public static Identifier focusNode(ArcaneFocus focus) {
        return switch (focus) {
            case ATTUNEMENT -> K_MAG_01_ARCANE_INITIATION;
            case BODY_WARD -> K_MAG_07_BODY_WARD;
            case BREATH_WARD -> K_MAG_08_BREATH_WARD;
            case EVOCATION -> K_MAG_16_EVOCATION;
            case ILLUSION -> K_MAG_17_ILLUSION;
            case TRANSLOCATION -> K_MAG_18_TRANSLOCATION;
        };
    }

    public static LoreText notesLoreText(List<Identifier> nodes) {
        return new LoreText(nodes, DISCIPLINE_MAG, "arcane_notes", Optional.empty(), "", 0);
    }

    public static void init() {
        LoreApi.registerCondition("aether", (level, pos) -> AetherChunkField.read(level, pos).value());

        LoreApi.registerMigrator(new LoreMigrator() {
            @Override
            public String id() {
                return "fantasy_earth:arcana_v1";
            }

            @Override
            public Set<Identifier> nodesFor(ServerPlayer player) {
                return computeMigratedNodes(
                        ArcanaPower.getMagicResearchLevel(player),
                        ArcanaPower.getArcaneBodyWardLevel(player),
                        ArcanaPower.getArcaneBreathWardLevel(player));
            }
        });

        LoreApi.registerGradeAccess(new GradeAccess() {
            @Override
            public Optional<Grade> read(ItemStack stack) {
                RuneItemComponent rune = stack.get(FantasyEarth.RUNE_ITEM_COMPONENT.get());
                if (rune == null) {
                    return Optional.empty();
                }
                return Grade.byId(rune.gemGrade());
            }

            @Override
            public ItemStack write(ItemStack stack, Grade grade) {
                RuneItemComponent rune = stack.get(FantasyEarth.RUNE_ITEM_COMPONENT.get());
                if (rune == null) {
                    return stack;
                }
                stack.set(FantasyEarth.RUNE_ITEM_COMPONENT.get(), rune.withGrade(grade.id()));
                return stack;
            }
        });
    }

    public static Set<Identifier> computeMigratedNodes(int magicResearchLevel, int bodyWardLevel, int breathWardLevel) {
        Set<Identifier> granted = new LinkedHashSet<>();
        int cappedResearch = Math.max(0, Math.min(RESEARCH_LEVEL_NODES.length, magicResearchLevel));
        for (int i = 1; i <= cappedResearch; i++) {
            granted.add(RESEARCH_LEVEL_NODES[i - 1]);
        }
        if (bodyWardLevel >= 1) {
            granted.add(K_MAG_07_BODY_WARD);
        }
        if (breathWardLevel >= 1) {
            granted.add(K_MAG_08_BREATH_WARD);
        }
        return granted;
    }

    public static void onLoreLearned(LoreLearnedEvent event) {
        if (event.source() == LoreLearnedEvent.Source.MIGRATE) {
            return;
        }
        ServerPlayer player = event.player();
        Identifier learned = event.node();
        boolean updated = false;
        if (K_MAG_01_ARCANE_INITIATION.equals(learned)) {
            if (ArcanaPower.learnArcaneInitiation(player)) {
                MagicJourney.complete(player, MagicJourney.Milestone.INITIATION);
                updated = true;
            }
        } else if (K_MAG_02_ARCANE_RESEARCH_2.equals(learned)) {
            updated = ArcanaPower.ensureMagicResearchLevelAtLeast(player, 2);
        } else if (K_MAG_03_ARCANE_RESEARCH_3.equals(learned)) {
            updated = ArcanaPower.ensureMagicResearchLevelAtLeast(player, 3);
        } else if (K_MAG_04_ARCANE_RESEARCH_4.equals(learned)) {
            updated = ArcanaPower.ensureMagicResearchLevelAtLeast(player, 4);
        } else if (K_MAG_05_ARCANE_RESEARCH_5.equals(learned)) {
            updated = ArcanaPower.ensureMagicResearchLevelAtLeast(player, 5);
        } else if (K_MAG_07_BODY_WARD.equals(learned)) {
            updated = ArcanaPower.learnArcaneBodyWardFromLore(player);
        } else if (K_MAG_08_BREATH_WARD.equals(learned)) {
            updated = ArcanaPower.learnArcaneBreathWardFromLore(player);
        } else if (K_MAG_16_EVOCATION.equals(learned)) {
            updated = ArcanaPower.learnEvocationFromLore(player);
        } else if (K_MAG_17_ILLUSION.equals(learned)) {
            updated = ArcanaPower.learnIllusionFromLore(player);
        } else if (K_MAG_18_TRANSLOCATION.equals(learned)) {
            updated = ArcanaPower.learnTranslocationFromLore(player);
        }
        if (updated) {
            ArcaneNetwork.sync(player, player.blockPosition(), false);
        }
    }

    public static boolean knowsInitiation(ServerPlayer player) {
        return LoreApi.knows(player, K_MAG_01_ARCANE_INITIATION) || ArcanaPower.getMagicResearchLevel(player) >= 1;
    }

    public static boolean knowsFocus(ServerPlayer player, ArcaneFocus focus) {
        return LoreApi.knows(player, focusNode(focus)) || focus.isUnlockedByLegacyTag(player);
    }

    public static boolean knowsConstructs(ServerPlayer player) {
        return LoreApi.knows(player, K_MAG_21_CONSTRUCTS);
    }

    public static boolean knowsCustomSpells(ServerPlayer player) {
        return LoreApi.knows(player, K_MAG_14_FOCI_AND_SCROLLS);
    }

    public static GateResult checkMachineProduct(BlockEntity be, Object product) {
        return LoreMachines.check(be, product);
    }

    public static Grade capMachineGrade(BlockEntity be, Object product, Grade wanted) {
        Grade capped = LoreMachines.capGrade(be, product, wanted);
        return LoreMachines.capGrade(be, FantasyEarth.ALCHEMY_TABLE.get(), capped);
    }
}
