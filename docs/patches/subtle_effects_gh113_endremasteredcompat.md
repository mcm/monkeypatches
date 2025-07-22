# Subtle Effects GH113 EndRemasteredCompat Fix

[Link to issue](https://github.com/MincraftEinstein/SubtleEffects/issues/113)<br>
[Link to fix](https://github.com/MincraftEinstein/SubtleEffects/commit/b73e58ca65f37ec8577508e9cf00d148dcfea4eb)

## Overview

This patch fixes the NeoForge crashing issue with End Remastered installed by changing how Subtle Effects registers block tickers for the ancient portal frame block to avoid "Registry is already frozen" exceptions.

## Problem

Subtle Effects was trying to access End Remastered's `CommonBlockRegistry.ANCIENT_PORTAL_FRAME` during initialization, causing a "Registry is already frozen" exception. This happened because the mod was attempting to register block tickers during a phase when the block registry could no longer be modified.

The problematic code was:
```java
ModBlockTickers.REGISTERED.put(CommonBlockRegistry.ANCIENT_PORTAL_FRAME, ticker);
```

This direct access to the block registry during initialization caused NeoForge to crash when End Remastered was installed.

## Solution

The patch changes the block ticker registration from direct block access to a predicate-based approach:

**From**: `ModBlockTickers.REGISTERED.put(block, ticker)`  
**To**: `ModBlockTickers.REGISTERED_SPECIAL.put(predicate, ticker)`

Where the predicate checks if a block state matches the ancient portal frame without directly accessing the block registry during initialization.

## Implementation Details

**File**: `EndRemasteredCompatMixin.java`  
**Package**: `io.mcmaster.monkeypatches.mixin.subtle_effects.GH113`

**Key Features**:
- Uses `@Redirect` mixin to intercept the `Map.put()` call in `EndRemasteredCompat.init()`
- Version-specific targeting (only applies to Subtle Effects version 1.11.0)
- Uses reflection with fallback to original behavior if anything fails
- Predicate-based block state checking instead of direct block access
- Early loading compatible (works during mod initialization phase)

**Mixin Target**: `einstein.subtle_effects.compat.EndRemasteredCompat.init()` method  
**Injection Point**: `Map.put(Object, Object)` call

**Fallback Strategy**:
- If reflection fails to access `REGISTERED_SPECIAL`, falls back to original behavior
- If End Remastered classes cannot be accessed, predicate returns false
- Graceful handling of all potential runtime errors

**Version Targeting**:
- Only applies to Subtle Effects version 1.11.0 where this issue exists
- Uses `@Restriction` with version predicates for precise targeting
- Automatic detection prevents application to other versions

## Configuration

This patch does not have configuration options because it loads too early in the mod loading process to access configuration files. The patch automatically applies when:
- Subtle Effects version 1.11.0 is detected
- End Remastered is also installed
- The conditional mixin system determines it should load

The patch is designed to be completely transparent and requires no user intervention.
