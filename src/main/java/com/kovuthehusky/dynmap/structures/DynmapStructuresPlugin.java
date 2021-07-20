package com.kovuthehusky.dynmap.structures;

import java.io.*;
import java.util.*;

import com.google.common.collect.Lists;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import static org.bukkit.StructureType.*;
import static org.bukkit.block.Biome.*;

@SuppressWarnings("unused")
public class DynmapStructuresPlugin extends JavaPlugin implements Listener {
    private static final Map<Biome, List<StructureType>> BIOMES = new HashMap<>();
    private final Map<StructureType, String> LABELS = new HashMap<StructureType, String>() {{
        if (StructureType.getStructureTypes().containsKey("bastion_remnant")) {
            put(BASTION_REMNANT, "Bastion Remnant");
        }
        put(BURIED_TREASURE, "Buried Treasure");
        put(DESERT_PYRAMID, "Desert Pyramid");
        put(END_CITY, "End City");
        put(NETHER_FORTRESS, "Nether Fortress");
        put(IGLOO, "Igloo");
        put(JUNGLE_PYRAMID, "Jungle Pyramid");
        put(WOODLAND_MANSION, "Woodland Mansion");
        put(MINESHAFT, "Mineshaft");
        if (StructureType.getStructureTypes().containsKey("nether_fossil")) {
            put(NETHER_FOSSIL, "Nether Fossil");
        }
        put(OCEAN_MONUMENT, "Ocean Monument");
        put(OCEAN_RUIN, "Ocean Ruins");
        if (StructureType.getStructureTypes().containsKey("pillager_outpost")) {
            put(PILLAGER_OUTPOST, "Pillager Outpost");
        }
        if (StructureType.getStructureTypes().containsKey("ruined_portal")) {
            put(RUINED_PORTAL, "Ruined Portal");
        }
        put(SHIPWRECK, "Shipwreck");
        put(STRONGHOLD, "Stronghold");
        put(SWAMP_HUT, "Swamp Hut");
        put(VILLAGE, "Village");
    }};
    private static final Map<StructureType, Boolean> STRUCTURES = new HashMap<>();

    private MarkerAPI api;
    private MarkerSet set;
    private boolean noLabels;
    private boolean includeCoordinates;

    @Override
    public void onEnable() {
        // Set up the metrics
        new Metrics(this, 605);
        // Set up the configuration
        this.saveDefaultConfig();
        FileConfiguration configuration = this.getConfig();
        configuration.options().copyDefaults(true);
        this.saveConfig();
        Biome s;
        // Fill in biome data structure
        BIOMES.put(OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(PLAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(DESERT, Lists.newArrayList(DESERT_PYRAMID, MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(MOUNTAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(FOREST, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(TAIGA, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(SWAMP, Lists.newArrayList(MINESHAFT, STRONGHOLD, SWAMP_HUT));
        BIOMES.put(RIVER, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        try {
            BIOMES.put(Biome.valueOf("NETHER"), Lists.newArrayList(NETHER_FORTRESS));
        } catch (IllegalArgumentException e) {
            // This is expected behavior from 1.16 onward
        }
        try {
            Biome.valueOf("NETHER_WASTES");
            BIOMES.put(NETHER_WASTES, Lists.newArrayList(NETHER_FORTRESS));
        } catch (IllegalArgumentException e) {
            getLogger().warning("NETHER_WASTES not supported.");
        }
        BIOMES.put(THE_END, Lists.newArrayList(END_CITY));
        BIOMES.put(FROZEN_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(FROZEN_RIVER, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(SNOWY_TUNDRA, Lists.newArrayList(IGLOO, MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(SNOWY_MOUNTAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(MUSHROOM_FIELDS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(MUSHROOM_FIELD_SHORE, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(BEACH, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(DESERT_HILLS, Lists.newArrayList(DESERT_PYRAMID, MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(WOODED_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(TAIGA_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(MOUNTAIN_EDGE, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(JUNGLE, Lists.newArrayList(JUNGLE_PYRAMID, MINESHAFT, STRONGHOLD));
        BIOMES.put(JUNGLE_HILLS, Lists.newArrayList(JUNGLE_PYRAMID, MINESHAFT, STRONGHOLD));
        BIOMES.put(JUNGLE_EDGE, Lists.newArrayList(JUNGLE_PYRAMID, MINESHAFT, STRONGHOLD));
        BIOMES.put(DEEP_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_MONUMENT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(STONE_SHORE, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(SNOWY_BEACH, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(BIRCH_FOREST, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(BIRCH_FOREST_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(DARK_FOREST, Lists.newArrayList(MINESHAFT, STRONGHOLD, WOODLAND_MANSION));
        BIOMES.put(SNOWY_TAIGA, Lists.newArrayList(IGLOO, MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(SNOWY_TAIGA_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(GIANT_TREE_TAIGA, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(GIANT_TREE_TAIGA_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(WOODED_MOUNTAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(SAVANNA, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(SAVANNA_PLATEAU, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(BADLANDS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(WOODED_BADLANDS_PLATEAU, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(BADLANDS_PLATEAU, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(SMALL_END_ISLANDS, Lists.newArrayList(END_CITY));
        BIOMES.put(END_MIDLANDS, Lists.newArrayList(END_CITY));
        BIOMES.put(END_HIGHLANDS, Lists.newArrayList(END_CITY));
        BIOMES.put(END_BARRENS, Lists.newArrayList(END_CITY));
        BIOMES.put(WARM_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(LUKEWARM_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(COLD_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(DEEP_WARM_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_MONUMENT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(DEEP_LUKEWARM_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_MONUMENT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(DEEP_COLD_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_MONUMENT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(DEEP_FROZEN_OCEAN, Lists.newArrayList(BURIED_TREASURE, MINESHAFT, OCEAN_MONUMENT, OCEAN_RUIN, SHIPWRECK, STRONGHOLD));
        BIOMES.put(THE_VOID, Lists.newArrayList());
        BIOMES.put(SUNFLOWER_PLAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(DESERT_LAKES, Lists.newArrayList(DESERT_PYRAMID, MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(GRAVELLY_MOUNTAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(FLOWER_FOREST, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(TAIGA_MOUNTAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(SWAMP_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD, SWAMP_HUT));
        BIOMES.put(ICE_SPIKES, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(MODIFIED_JUNGLE, Lists.newArrayList(JUNGLE_PYRAMID, MINESHAFT, STRONGHOLD));
        BIOMES.put(MODIFIED_JUNGLE_EDGE, Lists.newArrayList(JUNGLE_PYRAMID, MINESHAFT, STRONGHOLD));
        BIOMES.put(TALL_BIRCH_FOREST, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(TALL_BIRCH_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(DARK_FOREST_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD, WOODLAND_MANSION));
        BIOMES.put(SNOWY_TAIGA_MOUNTAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(GIANT_SPRUCE_TAIGA, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(GIANT_SPRUCE_TAIGA_HILLS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(MODIFIED_GRAVELLY_MOUNTAINS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(SHATTERED_SAVANNA, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(SHATTERED_SAVANNA_PLATEAU, Lists.newArrayList(MINESHAFT, STRONGHOLD, VILLAGE));
        BIOMES.put(ERODED_BADLANDS, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(MODIFIED_WOODED_BADLANDS_PLATEAU, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        BIOMES.put(MODIFIED_BADLANDS_PLATEAU, Lists.newArrayList(MINESHAFT, STRONGHOLD));
        try {
            Biome.valueOf("BAMBOO_JUNGLE");
            BIOMES.put(BAMBOO_JUNGLE, Lists.newArrayList(JUNGLE_PYRAMID, MINESHAFT, STRONGHOLD));
        } catch (IllegalArgumentException e) {
            getLogger().warning("BAMBOO_JUNGLE not supported.");
        }
        try {
            Biome.valueOf("BAMBOO_JUNGLE_HILLS");
            BIOMES.put(BAMBOO_JUNGLE_HILLS, Lists.newArrayList(JUNGLE_PYRAMID, MINESHAFT, STRONGHOLD));
        } catch (IllegalArgumentException e) {
            getLogger().warning("BAMBOO_JUNGLE_HILLS not supported.");
        }
        try {
            Biome.valueOf("SOUL_SAND_VALLEY");
            BIOMES.put(SOUL_SAND_VALLEY, Lists.newArrayList(NETHER_FORTRESS));
        } catch (IllegalArgumentException e) {
            getLogger().warning("SOUL_SAND_VALLEY not supported.");
        }
        try {
            Biome.valueOf("CRIMSON_FOREST");
            BIOMES.put(CRIMSON_FOREST, Lists.newArrayList(NETHER_FORTRESS));
        } catch (IllegalArgumentException e) {
            getLogger().warning("CRIMSON_FOREST not supported.");
        }
        try {
            Biome.valueOf("WARPED_FOREST");
            BIOMES.put(WARPED_FOREST, Lists.newArrayList(NETHER_FORTRESS));
        } catch (IllegalArgumentException e) {
            getLogger().warning("WARPED_FOREST not supported.");
        }
        try {
            Biome.valueOf("BASALT_DELTAS");
            BIOMES.put(BASALT_DELTAS, Lists.newArrayList(NETHER_FORTRESS));
        } catch (IllegalArgumentException e) {
            getLogger().warning("BASALT_DELTAS not supported.");
        }
        try {
            Biome.valueOf("CUSTOM");
            BIOMES.put(CUSTOM, Lists.newArrayList(BASTION_REMNANT, BURIED_TREASURE, DESERT_PYRAMID, END_CITY, NETHER_FORTRESS, IGLOO, JUNGLE_PYRAMID, WOODLAND_MANSION, MINESHAFT, NETHER_FOSSIL, OCEAN_MONUMENT, OCEAN_RUIN, PILLAGER_OUTPOST, RUINED_PORTAL, SHIPWRECK, STRONGHOLD, SWAMP_HUT, VILLAGE));
        } catch (IllegalArgumentException e) {
            getLogger().warning("CUSTOM not supported.");
        }
        // Add pillager outposts if supported
        if (StructureType.getStructureTypes().containsKey("pillager_outpost")) {
            for (Biome biome : new Biome[]{PLAINS, DESERT, TAIGA, SNOWY_TUNDRA, SNOWY_MOUNTAINS, DESERT_HILLS, TAIGA_HILLS, SNOWY_TAIGA, SNOWY_TAIGA_HILLS, SAVANNA, SAVANNA_PLATEAU, SUNFLOWER_PLAINS, DESERT_LAKES, TAIGA_MOUNTAINS, ICE_SPIKES, SNOWY_TAIGA_MOUNTAINS, SHATTERED_SAVANNA, SHATTERED_SAVANNA_PLATEAU}) {
                BIOMES.get(biome).add(PILLAGER_OUTPOST);
            }
        }
        // Add bastion remnant if supported
        if (StructureType.getStructureTypes().containsKey("bastion_remnant")) {
            for (Biome biome : new Biome[]{NETHER_WASTES, SOUL_SAND_VALLEY, CRIMSON_FOREST, WARPED_FOREST}) {
                BIOMES.get(biome).add(BASTION_REMNANT);
            }
        }
        // Add nether fossils if supported
        if (StructureType.getStructureTypes().containsKey("nether_fossil")) {
            for (Biome biome : new Biome[]{SOUL_SAND_VALLEY}) {
                BIOMES.getOrDefault(biome, new ArrayList<>()).add(NETHER_FOSSIL);
            }
        }
        // Add ruined portals if supported
        if (StructureType.getStructureTypes().containsKey("ruined_portal")) {
            for (Biome biome : Biome.values()) {
                if (biome == THE_END) {
                    continue;
                }
                try {
                    BIOMES.get(biome).add(RUINED_PORTAL);
                }catch (NullPointerException exception){
                    getLogger().info("NullPointer in biome "+biome.name());
                }
            }
        }
        // Fill in id and label data structures
        for (StructureType type : StructureType.getStructureTypes().values()) {
            String id = type.getName().toLowerCase(Locale.ROOT).replace("_", "");
            STRUCTURES.put(type, configuration.getBoolean("structures." + id));
            String label = configuration.getString("labels." + id);
            if (label != null) {
                LABELS.put(type, label);
            }
        }
        // Register for events
        getServer().getPluginManager().registerEvents(this, this);
        // Check if Dynmap is even enabled
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap")) {
            // Set up our Dynmap api
            try {
                DynmapCommonAPI plugin = (DynmapCommonAPI) Bukkit.getPluginManager().getPlugin("dynmap");
                if (plugin != null) {
                    api = plugin.getMarkerAPI();
                }
            } catch (NullPointerException e) {
                return;
            }
            // Set up our Dynmap layer
            String layer = configuration.getString("layer.name");
            if (layer == null) {
                layer = "Structures";
            }
            set = api.getMarkerSet(layer.toLowerCase(Locale.ROOT));
            if (set == null) {
                set = api.createMarkerSet(layer.toLowerCase(Locale.ROOT), layer, null, true);
            }
            set.setHideByDefault(configuration.getBoolean("layer.hidebydefault"));
            set.setLayerPriority(configuration.getInt("layer.layerprio"));
            noLabels = configuration.getBoolean("layer.noLabels");
            int minZoom = configuration.getInt("layer.minzoom");
            if (minZoom > 0) {
                set.setMinZoom(minZoom);
            }
            includeCoordinates = configuration.getBoolean("layer.inc-coord");
            // Create the marker icons
            for (StructureType type : StructureType.getStructureTypes().values()) {
                String str = type.getName().toLowerCase(Locale.ROOT).replaceAll("_", "");
                InputStream in = this.getClass().getResourceAsStream("/" + str + ".png");
                if (in != null) {
                    if (api.getMarkerIcon("structures." + str) == null) {
                        api.createMarkerIcon("structures." + str, str, in);
                    } else {
                        api.getMarkerIcon("structures." + str).setMarkerIconImage(in);
                    }
                }
            }
            // Remove any markers for disabled types
            List<String> disabled = new ArrayList<>();
            for (StructureType type : StructureType.getStructureTypes().values()) {
                String id = type.getName().toLowerCase(Locale.ROOT).replace("_", "");
                if (!configuration.getBoolean("structures." + id)) {
                    disabled.add(id);
                }
            }
            for (Marker marker : set.getMarkers()) {
                for (String id : disabled) {
                    if (marker.getMarkerID().startsWith(id)) {
                        marker.deleteMarker();
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.getWorld().canGenerateStructures()) {
            Bukkit.getScheduler().runTask(this, new DynmapStructuresRunnable(event.getChunk()));
        }
    }

    private class DynmapStructuresRunnable implements Runnable {
        private final Chunk chunk;

        private DynmapStructuresRunnable(Chunk chunk) {
            this.chunk = chunk;
        }

        @Override
        public void run() {
            Location location = new Location(chunk.getWorld(), chunk.getX() << 4, 64, chunk.getZ() << 4);
            World world = location.getWorld();
            if (world != null) {
                Biome biome;
                try {
                    Biome.class.getMethod("getBiome", int.class, int.class, int.class);
                    biome = world.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                } catch (NoSuchMethodException e) {
                    biome = world.getBiome(location.getBlockX(), location.getBlockZ());
                }
                if (biome != null) {
                    for (StructureType type : BIOMES.get(biome)) {
                        if (STRUCTURES.get(type)) {
                            Location structure;
                            try {
                                structure = location.getWorld().locateNearestStructure(location, type, 1, false);
                            } catch (ConcurrentModificationException e) {
                                getLogger().warning("Skipping locate at ([" + location.getWorld().getName() + "], " + location.getBlockX() + ", " + location.getBlockZ() + ") due to concurrent modification exception.");
                                return;
                            } catch (NullPointerException e) {
                                getLogger().warning("Skipping locate at ([" + location.getWorld().getName() + "], " + location.getBlockX() + ", " + location.getBlockZ() + ") due to null pointer exception.");
                                return;
                            }
                            if (structure != null) {
                                String id = type.getName().toLowerCase(Locale.ROOT).replace("_", "");
                                int x = structure.getBlockX();
                                int z = structure.getBlockZ();
                                String label = "";
                                if (!noLabels) {
                                    label = LABELS.get(type);
                                    if (includeCoordinates) {
                                        label = label + " [" + x + "," + z + "]";
                                    }
                                }
                                set.createMarker(id + "," + x + "," + z, label, world.getName(), x, 64, z, api.getMarkerIcon("structures." + id), true);
                            }
                        }
                    }
                }
            }
        }
    }
}
