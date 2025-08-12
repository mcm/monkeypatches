# JustEnoughResources GH547 - Biome Helper Fix

[Link to issue](https://github.com/way2muchnoise/JustEnoughResources/issues/547)<br>
[Link to fix](https://github.com/mcm001/monkeypatches/tree/main/src/main/java/io/mcmaster/monkeypatches/mixin/jeresources/GH547)

## Overview

Fixes JustEnoughResources (JER) compatibility with Vanilla Backport mod and other mods that add custom biomes to NeoForge 1.21+. This patch prevents the villager trades tab from disappearing and fixes crashes when JER attempts to load biome data.

## Problem

When using JustEnoughResources with mods that add custom biomes (such as Vanilla Backport), JER fails to properly load biome information, resulting in:

- Missing villager trades tab in JEI
- Complete breakdown of JER functionality 
- `ExceptionInInitializerError` during JER plugin initialization
- Empty biome lists preventing ore distribution and mob drop information from displaying

The issue stems from JER's `BiomeHelper` class using outdated registry access methods that don't work properly with NeoForge's updated registry system in 1.21+.

## Solution

Replace the problematic biome lookup methods in JER's `BiomeHelper` class with updated NeoForge-compatible registry access:

- `getAllBiomes()` - Use `level.registryAccess().registry(Registries.BIOME)` instead of legacy methods
- `getBiome(ResourceKey<Biome>)` - Use proper registry lookup for individual biomes
- `getBiomes(ResourceKey<Biome>)` - Use updated registry iteration for biome categories

## Implementation Details

The `BiomeHelperMixin` patches three critical methods in `jeresources.api.util.BiomeHelper`:

1. **getAllBiomes()**: Replaces the entire method to use `Minecraft.getInstance().level.registryAccess().registry(Registries.BIOME)` for safe biome enumeration
2. **getBiome(ResourceKey<Biome>)**: Updates single biome lookup to use the new registry system
3. **getBiomes(ResourceKey<Biome>)**: Fixes biome category filtering to work with the updated registry access

All patches include null safety checks and proper error handling:
- Check if level exists before attempting registry access
- Verify registry is present before proceeding  
- Return early if patches are disabled via configuration

## Configuration

**Config Option**: `patches.jeresources.gh547_enabled` (default: `true`)

This patch can be disabled if it conflicts with other mods or if JER releases an official fix. When disabled, JER will use its original biome lookup methods.

**Note**: This patch only loads when JustEnoughResources is installed, preventing conflicts in environments without JER.
