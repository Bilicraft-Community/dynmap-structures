# Dynmap-Structures [![Build Status](https://travis-ci.org/Codeski/dynmap-structures.svg?branch=master)](https://travis-ci.org/Codeski/dynmap-structures)

A Bukkit plugin that shows your world's structures (such as Villages, Strongholds, and Temples) on Dynmap.

![Dynmap-Structures](http://codeski.com/img/dynmapstructures2.png)

## Features

* Adds markers to Dynmap where all structures are located.
* Configure which types of structures you would like to be shown.
* Compatible with CraftBukkit, as well as MCPC+, Cauldron and other Forge based Bukkit-compatible servers with [DynmapForge](http://www.curse.com/mc-mods/minecraft/dynmapforge) and [DynmapCBBridge](http://www.curse.com/mc-mods/minecraft/dynmapcbbridge) installed.
* Includes compatibility with Biomes O' Plenty, including legacy (tested on 1.6.4) versions.

## Configuration

The **structures** node supports boolean values for the following keys:

**fortress**

    If true, displays Nether Fortresses on your map.

**igloo**

    If true, displays Igloos on your map.

**mansion**

    If true, displays Woodland Mansions on your map.

**mineshaft**

    If true, displays Abandoned Mineshafts on your map. Default value is false.

**monument**

    If true, displays Ocean Monuments on your map.

**stronghold**

    If true, displays Strongholds on your map.

**temple**

    If true, displays Desert Temples and Jungle Temples on your map.

**witch**

    If true, displays Witch Huts on your map.

**village**

    If true, displays Villages on your map.

The **layer** node supports the following key-value pairs:

**name**

    A string that is used for the name of the layer. It is shown in the layer control UI element.

**hidebydefault**

    If true, the structures layer will be hidden by default.

**layerprio**

    An integer representing the layer priority in Dynmap.

**nolabels**

    If true, no labels will be shown for structures on the map.

**minzoom**

    The minimum zoom level where structures will be shown on the map.

**inc-coord**

    If true, coordinates will be included in the labels for structures.

You can also place a hash in front of any of the nodes to comment it out and disable it.

## Links

* Website: <http://codeski.com/#dynmapstructures>
* Example: <http://codeski.com/examples/dynmap-structures>
* Issues: <https://github.com/Codeski/dynmap-structures/issues>
* Source: <https://github.com/Codeski/dynmap-structures>
* Builds: <https://travis-ci.org/Codeski/dynmap-structures>
* Bukkit: <http://dev.bukkit.org/bukkit-plugins/dynmap-structures>
* Metrics: <http://mcstats.org/plugin/dynmap-structures>
