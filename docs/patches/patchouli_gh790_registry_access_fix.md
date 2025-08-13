# Patchouli GH790 - Registry Access Fix

[Link to issue](https://github.com/VazkiiMods/Patchouli/issues/790)<br>
[Link to fix](https://github.com/VazkiiMods/Patchouli/pull/793)

## Overview

Fixes Patchouli's registry access issues during book loading when custom book items are parsed before their mod has registered items.

## Problem

Patchouli uses `VanillaRegistries` to access registries, potentially before mods have fully registered all of their items. While this no longer causes crashes thanks to the fix in #790, there are still spurious error messages with unknown consequences.

## Solution

The patch redirects the `VanillaRegistries.createLookup()` call in the Book constructor to use the current level's registry access when available, falling back to the vanilla registry lookup when the level is not available.

This ensures that:
1. When a level exists (during normal gameplay), the current registry is used, which includes all loaded mods
2. When no level exists (early startup), it falls back to vanilla registries
3. Registry access issues during mod loading are avoided

## Implementation Details

**File**: `BookMixin.java`  
**Package**: `io.mcmaster.monkeypatches.mixin.patchouli.GH790`

### Target Class
- `vazkii.patchouli.common.book.Book`

### Mixin Type
- `@Redirect` on the `VanillaRegistries.createLookup()` call in the lambda method

## Configuration

- **Config Option**: `patches.patchouli.gh790_enabled` (default: true)
- **Condition Method**: `MixinConditions.shouldApplyPatchouliGH790()`