package com.xxsx.fantasyearth;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Loads data-driven magic graphs from {@code data/fantasy_earth/spell_graph}.
 *
 * <p>JSON only names whitelisted sources and nodes, so a data pack cannot run code.
 * Each definition is validated as a whole; a rejected file is logged with its id and
 * never replaces a built-in graph.</p>
 */
public final class SpellGraphLoader implements ResourceManagerReloadListener {
    private static final FileToIdConverter LISTER = FileToIdConverter.json("spell_graph");
    private static final double MAX_NODE_COST = 1000.0D;
    private static final int MAX_NODES = 64;
    private static volatile Map<Identifier, SpellGraph> loaded = Map.of();

    public static void register(AddServerReloadListenersEvent event) {
        event.addListener(FantasyEarth.id("spell_graph"), new SpellGraphLoader());
    }

    public static Map<Identifier, SpellGraph> loaded() {
        return loaded;
    }

    /** Resolves a data graph, falling back to the built-in catalog. */
    public static SpellGraph find(String id) {
        Identifier key = Identifier.tryParse(id);
        if (key == null) {
            return null;
        }
        SpellGraph data = loaded.get(key);
        return data != null ? data : SpellCatalog.graphs().get(id);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Map<Identifier, SpellGraph> parsed = new LinkedHashMap<>();
        for (Map.Entry<Identifier, Resource> entry
                : LISTER.listMatchingResourcesFromNamespace(resourceManager, FantasyEarth.MODID).entrySet()) {
            Identifier fileId = LISTER.fileToId(entry.getKey());
            try (Reader reader = entry.getValue().openAsReader()) {
                SpellGraph graph = parse(fileId, JsonParser.parseReader(reader).getAsJsonObject());
                SpellGraph.Validation validation = graph.validate();
                if (!validation.valid()) {
                    FantasyEarth.LOGGER.error("Rejected spell graph {}: {} ({})",
                            fileId, validation.errorCode(), validation.message());
                    continue;
                }
                parsed.put(fileId, graph);
            } catch (Exception exception) {
                FantasyEarth.LOGGER.error("Invalid spell graph definition {}", fileId, exception);
            }
        }
        loaded = Map.copyOf(parsed);
        FantasyEarth.LOGGER.info("Loaded {} spell graph definitions", loaded.size());
    }

    private static SpellGraph parse(Identifier fileId, JsonObject json) {
        String declaredId = requireString(json, "id");
        if (!declaredId.equals(fileId.toString())) {
            throw new IllegalArgumentException("id " + declaredId + " does not match file " + fileId);
        }
        List<SpellSource> sources = new ArrayList<>();
        for (JsonElement element : requireArray(json, "sources")) {
            sources.add(SpellSource.valueOf(element.getAsString().toUpperCase(Locale.ROOT)));
        }
        JsonArray nodeArray = requireArray(json, "nodes");
        if (nodeArray.size() > MAX_NODES) {
            throw new IllegalArgumentException("graph exceeds " + MAX_NODES + " nodes");
        }
        List<SpellGraph.SpellNodeInstance> nodes = new ArrayList<>();
        for (JsonElement element : nodeArray) {
            JsonObject node = element.getAsJsonObject();
            nodes.add(new SpellGraph.SpellNodeInstance(
                    SpellNode.valueOf(requireString(node, "kind").toUpperCase(Locale.ROOT)),
                    requireString(node, "input"),
                    boundedCost(node, "cost_aeu"),
                    boundedCost(node, "cost_mana")));
        }
        return new SpellGraph(declaredId, sources, nodes);
    }

    private static double boundedCost(JsonObject node, String key) {
        if (!node.has(key)) {
            return 0.0D;
        }
        double value = node.get(key).getAsDouble();
        if (!Double.isFinite(value) || value < 0.0D || value > MAX_NODE_COST) {
            throw new IllegalArgumentException(key + " must be within 0.." + MAX_NODE_COST);
        }
        return value;
    }

    private static JsonArray requireArray(JsonObject json, String key) {
        if (!json.has(key) || !json.get(key).isJsonArray()) {
            throw new IllegalArgumentException("Missing array field: " + key);
        }
        return json.getAsJsonArray(key);
    }

    private static String requireString(JsonObject json, String key) {
        if (!json.has(key) || !json.get(key).isJsonPrimitive()) {
            throw new IllegalArgumentException("Missing string field: " + key);
        }
        String value = json.get(key).getAsString().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Empty string field: " + key);
        }
        return value;
    }
}
