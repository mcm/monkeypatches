# Create Stuff 'N Additions CF59

[Link to issue](https://legacy.curseforge.com/minecraft/mc-mods/create-stuff-additions/issues/59)<br>
[Link to fix]() <!-- Add link to your fix if available -->

## Overview

Adds NeoForge fluid handler capabilities to Create Stuff 'N Additions jetpacks and tanks, enabling compatibility with Create spouts and other fluid-handling systems. This addresses the issue where jetpacks and tanks couldn't be filled using Create's spout system.

## Problem

Create Stuff 'N Additions jetpacks and tanks store fuel/fluid data in custom NBT formats but don't expose this through NeoForge's standard `IFluidHandlerItem` capability. This prevents other mods (particularly Create) from interacting with these items through their fluid handling systems.

The specific issue was that Create spouts couldn't fill jetpacks or tanks because the capability wasn't registered, even though the items internally support fluid storage.

## Solution

This fix registers `IFluidHandlerItem` capabilities for all Create Stuff 'N Additions jetpacks and tanks:

**Jetpacks**:
- Andesite Jetpack Chestplate
- Copper Jetpack Chestplate  
- Brass Jetpack Chestplate
- Netherite Jetpack Chestplate

**Tanks**:
- Small Filling Tank & Small Fueling Tank
- Medium Filling Tank & Medium Fueling Tank
- Large Filling Tank & Large Fueling Tank

## Implementation Details

**File**: `FluidCapability.java`  
**Package**: `io.mcmaster.monkeypatches.fixes.createstuffadditions.fluid_capability`

**Key Features**:
- Implements `IFluidHandlerItem` interface for jetpacks and tanks
- Supports dual-tank system (water and lava for jetpacks, different fluids for tanks)
- Converts between NBT storage format and FluidStack API
- Respects capacity limits for different item types
- Capability registration handled in `MonkeyPatches.registerCapabilities()`

**Capability Registration**:
The capabilities are registered in the main mod class during the `RegisterCapabilitiesEvent`:
- Only registers when Create Stuff 'N Additions mod is loaded
- Controlled by configuration setting
- Registers capability for each specific item type

**Fluid Conversion**:
- Jetpacks: 1 fuel unit = 10 mB of fluid
- Different capacity limits based on item type (8000-32000 mB)
- Safe NBT handling with proper null checks

## Configuration

This fix can be enabled or disabled in the mod configuration:

```toml
[patches.create_sa]
    # Enable Create Stuff 'N Additions issue #59 capabilities
    # Adds Neoforge fluid handler capabilities to enable e.g. Create spout filling
    fluid_handler_capabilities_enabled = true
```

The fix is enabled by default. When disabled, the capability registrations are skipped.

**Note:** This fix only loads when the Create Stuff 'N Additions mod is present. If the mod is not installed, the capability registrations will not be attempted.