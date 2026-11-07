package com.xxsx.fantasyearth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xxsx.earthlore.core.CarrierForms;
import com.xxsx.earthlore.core.Gate;
import com.xxsx.earthlore.core.GraphValidator;
import com.xxsx.earthlore.core.NodeDef;
import com.xxsx.earthlore.core.Prereq;
import com.xxsx.earthlore.core.Reachability;
import com.xxsx.fantasyearth.client.ArcaneAttunementScreen;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FantasyLoreIntegrationTest {
    private static final Set<String> DISCIPLINES = Set.of("earth_lore:mag", "earth_lore:geo", "earth_lore:che");
    private static final GraphValidator.DependencyRule STANDALONE_DEPS =
            (owner, other) -> owner.equals(other) || "earth_lore".equals(other);

    @Test
    void allTwentyThreeMagNodesValidateAndAreReachableStandalone() throws IOException {
        List<NodeDef> nodes = loadMagNodes();
        assertEquals(23, nodes.size(), "Expected 23 K-MAG nodes");

        GraphValidator.Result validated = GraphValidator.validate(nodes, DISCIPLINES, STANDALONE_DEPS);
        assertTrue(validated.rejected().isEmpty(), () -> "Rejected nodes: " + validated.rejected());
        assertEquals(23, validated.accepted().size());

        Reachability.Report reach = Reachability.compute(validated.accepted());
        assertTrue(reach.unreachable().isEmpty(), () -> "Unreachable nodes: " + reach.unreachable());
        assertEquals(23, reach.learnable().size());
        assertEquals(3, reach.altUsed(), "All 3 cross-mod any-groups should use their self-taught aether_sensing fallback standalone");
    }

    @Test
    void carrierFormsResolveDedicatedAndGenericNotes() throws IOException {
        Path file = Path.of("src/main/resources/data/fantasy_earth/earth_lore/carrier_forms.json");
        JsonObject root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        List<CarrierForms.Decl> decls = new ArrayList<>();
        for (JsonElement el : root.getAsJsonArray("forms")) {
            JsonObject obj = el.getAsJsonObject();
            String carrier = obj.get("carrier").getAsString();
            String result = obj.get("result").getAsString();
            String discipline = obj.has("discipline") ? obj.get("discipline").getAsString() : null;
            Set<String> nodes = new LinkedHashSet<>();
            if (obj.has("nodes")) {
                obj.getAsJsonArray("nodes").forEach(n -> nodes.add(n.getAsString()));
            }
            decls.add(new CarrierForms.Decl(carrier, result, discipline, nodes));
        }

        assertEquals("fantasy_earth:arcane_initiation_notes",
                CarrierForms.resolve(decls, "earth_lore:blank_notes", "earth_lore:mag",
                        List.of("fantasy_earth:mag/arcane_initiation")).orElseThrow());
        assertEquals("fantasy_earth:arcane_body_ward_notes",
                CarrierForms.resolve(decls, "earth_lore:blank_notes", "earth_lore:mag",
                        List.of("fantasy_earth:mag/body_ward")).orElseThrow());
        assertEquals("fantasy_earth:arcane_breath_ward_notes",
                CarrierForms.resolve(decls, "earth_lore:blank_notes", "earth_lore:mag",
                        List.of("fantasy_earth:mag/breath_ward")).orElseThrow());
        assertEquals("fantasy_earth:evocation_notes",
                CarrierForms.resolve(decls, "earth_lore:blank_notes", "earth_lore:mag",
                        List.of("fantasy_earth:mag/evocation")).orElseThrow());
        assertEquals("fantasy_earth:illusion_notes",
                CarrierForms.resolve(decls, "earth_lore:blank_notes", "earth_lore:mag",
                        List.of("fantasy_earth:mag/illusion")).orElseThrow());
        assertEquals("fantasy_earth:translocation_notes",
                CarrierForms.resolve(decls, "earth_lore:blank_notes", "earth_lore:mag",
                        List.of("fantasy_earth:mag/translocation")).orElseThrow());
        assertEquals("fantasy_earth:arcane_research_notes",
                CarrierForms.resolve(decls, "earth_lore:blank_notes", "earth_lore:mag",
                        List.of("fantasy_earth:mag/rune_carving")).orElseThrow());
    }

    @Test
    void legacySaveMigrationMatchesSpec() {
        Set<Identifier> migrated = FantasyLoreIntegration.computeMigratedNodes(3, 1, 0);
        assertEquals(Set.of(
                FantasyLoreIntegration.K_MAG_01_ARCANE_INITIATION,
                FantasyLoreIntegration.K_MAG_02_ARCANE_RESEARCH_2,
                FantasyLoreIntegration.K_MAG_03_ARCANE_RESEARCH_3,
                FantasyLoreIntegration.K_MAG_07_BODY_WARD
        ), migrated);

        Set<Identifier> full = FantasyLoreIntegration.computeMigratedNodes(5, 1, 1);
        assertTrue(full.contains(FantasyLoreIntegration.K_MAG_04_ARCANE_RESEARCH_4));
        assertTrue(full.contains(FantasyLoreIntegration.K_MAG_05_ARCANE_RESEARCH_5));
        assertTrue(full.contains(FantasyLoreIntegration.K_MAG_08_BREATH_WARD));
        assertEquals(7, full.size());
    }

    @Test
    void alchemyTemperatureFocusRunesAndFamiliarAffinityMatchSpec() {
        assertEquals(600, ArcanaBalance.computeAlchemyTemperature(30));
        assertTrue(ArcanaBalance.computeAlchemyTemperature(30) < 700);
        assertEquals(700, ArcanaBalance.computeAlchemyTemperature(40));
        assertEquals(800, ArcanaBalance.computeAlchemyTemperature(50));

        ArcaneFocusComponent focus = ArcaneFocusComponent.DEFAULT
                .withRuneSlot(0, "power:1")
                .withRuneSlot(1, "flow:2")
                .withGemSlot(0, "ruby:fine")
                .withJadeHousing(true);
        assertEquals(1, focus.maxRuneTier("power"));
        assertEquals(2, focus.maxRuneTier("flow"));
        assertEquals(600, focus.maxDurability());
        assertTrue(focus.hasDuplicateGemSpecies("ruby"));
        assertEquals(1.5D, focus.gemBonus("ruby"), 1e-6);

        assertFalse(ArcanaBalance.canContractWithAffinity(20));
        assertTrue(ArcanaBalance.canContractWithAffinity(30));
    }

    @Test
    void arcaneAttunementScreenLayoutNeverOverlapsAtAnyResolution() throws IOException {
        int[][] resolutions = {
                {320, 180},
                {360, 200},
                {427, 240},
                {640, 360},
                {854, 480},
                {1280, 720},
                {1920, 1080}
        };
        for (int[] res : resolutions) {
            ArcaneAttunementScreen.LayoutMetrics layout =
                    ArcaneAttunementScreen.computeLayout(res[0], res[1]);
            assertTrue(layout.hasNoVerticalOverlap(),
                    "Focus buttons overlapped bottom action buttons at " + res[0] + "x" + res[1] + ": " + layout);
            assertTrue(layout.hasNoHeaderOverlap(),
                    "Top navigation buttons overlapped at " + res[0] + "x" + res[1] + ": " + layout);
            assertTrue(layout.focusCellWidth() >= 48,
                    "Focus cell width too narrow at " + res[0] + "x" + res[1] + ": " + layout.focusCellWidth());
        }

        for (String langFile : List.of(
                "src/main/resources/assets/fantasy_earth/lang/zh_cn.json",
                "src/main/resources/assets/fantasy_earth/lang/en_us.json")) {
            JsonObject lang = JsonParser.parseString(
                    Files.readString(Path.of(langFile), StandardCharsets.UTF_8)).getAsJsonObject();
            assertTrue(lang.has("screen.fantasy_earth.attunement.nav.hub"),
                    "Missing hub nav key in " + langFile);
            assertTrue(lang.has("screen.fantasy_earth.attunement.nav.lore_tree"),
                    "Missing lore_tree nav key in " + langFile);
        }
    }

    @Test
    void settlementTerrainValidationAndAetherCrystalClusterLoopAreClosed() throws IOException {
        assertTrue(ArcanaBalance.isSettlementSiteValid(64, 66, false, 0));
        assertTrue(ArcanaBalance.isSettlementSiteValid(64, 64 + ArcanaBalance.MAX_SETTLEMENT_SLOPE_DELTA, false, 0));
        assertFalse(ArcanaBalance.isSettlementSiteValid(64, 64 + ArcanaBalance.MAX_SETTLEMENT_SLOPE_DELTA + 1, false, 0),
                "Steep slope must be rejected");
        assertFalse(ArcanaBalance.isSettlementSiteValid(64, 64, true, 0),
                "Water/fluid site must be rejected");
        assertFalse(ArcanaBalance.isSettlementSiteValid(64, 65, false, 1),
                "Site with missing ground column must be rejected");

        JsonObject placed = JsonParser.parseString(Files.readString(
                Path.of("src/main/resources/data/fantasy_earth/worldgen/placed_feature/aether_crystal_cluster.json"),
                StandardCharsets.UTF_8)).getAsJsonObject();
        assertEquals("fantasy_earth:aether_crystal_cluster", placed.get("feature").getAsString());
        JsonObject firstPlacement = placed.getAsJsonArray("placement").get(0).getAsJsonObject();
        assertEquals("minecraft:count", firstPlacement.get("type").getAsString());
        assertTrue(firstPlacement.get("count").getAsInt() >= 4);

        JsonObject loot = JsonParser.parseString(Files.readString(
                Path.of("src/main/resources/data/fantasy_earth/loot_table/blocks/aether_crystal_cluster.json"),
                StandardCharsets.UTF_8)).getAsJsonObject();
        JsonObject entry = loot.getAsJsonArray("pools").get(0).getAsJsonObject()
                .getAsJsonArray("entries").get(0).getAsJsonObject();
        assertEquals("fantasy_earth:aether_shard", entry.get("name").getAsString());
    }

    private static List<NodeDef> loadMagNodes() throws IOException {
        Path dir = Path.of("src/main/resources/data/fantasy_earth/earth_lore/knowledge_node/mag");
        List<NodeDef> list = new ArrayList<>();
        try (Stream<Path> paths = Files.list(dir)) {
            for (Path file : paths.filter(p -> p.toString().endsWith(".json")).sorted().toList()) {
                String slug = file.getFileName().toString().replace(".json", "");
                String id = "fantasy_earth:mag/" + slug;
                JsonObject obj = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
                String discipline = obj.get("discipline").getAsString();
                int tier = obj.get("tier").getAsInt();
                Gate gate = Gate.parse(obj.get("gate").getAsString()).orElseThrow();
                List<Prereq> requires = new ArrayList<>();
                for (JsonElement reqEl : obj.getAsJsonArray("requires")) {
                    JsonObject reqObj = reqEl.getAsJsonObject();
                    if (reqObj.has("all")) {
                        requires.add(new Prereq.All(reqObj.get("all").getAsString()));
                    } else if (reqObj.has("any")) {
                        List<String> members = new ArrayList<>();
                        JsonArray arr = reqObj.getAsJsonArray("any");
                        arr.forEach(m -> members.add(m.getAsString()));
                        requires.add(new Prereq.Any(members));
                    }
                }
                List<String> unlockKeys = new ArrayList<>();
                for (JsonElement uEl : obj.getAsJsonArray("unlocks")) {
                    JsonObject uObj = uEl.getAsJsonObject();
                    Map.Entry<String, JsonElement> entry = uObj.entrySet().iterator().next();
                    String kind = entry.getKey();
                    String val = entry.getValue().getAsString();
                    unlockKeys.add("capstone".equals(kind) ? ("capstone:" + val + "@fantasy_earth") : (kind + ":" + val));
                }
                list.add(new NodeDef(id, discipline, tier, gate, requires, unlockKeys, false));
            }
        }
        return list;
    }
}
