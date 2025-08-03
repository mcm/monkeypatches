# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.4.2] - 2025-08-03

### Added
- Rhino PR57 patch for object signature parsing in arrays
  - ClassFileWriterMixin: Fixes the `sizeOfParameters` method to properly parse object signatures in bytecode
  - Prevents parsing failures when object types appear within array signatures (e.g., `[Ljava/lang/String;`)
  - Overwrites the entire method with the fixed implementation from upstream PR
  - Only loads when Rhino mod is present (no configuration option due to early loading)

### Technical Details
- Fixes the 'L' case handling in signature parsing that was falling through to default
- Adds proper semicolon detection and bounds checking for object type signatures
- Uses @Overwrite mixin to replace the buggy method entirely

## [0.4.1] - 2025-07-28
- Create Stuff 'N Additions CF59 patch for fluid handler capabilities
  - Extended fluid handler patches to all gadgets
  - Read capacities from Create SA config file

## [0.4.0] - 2025-07-27

### Added
- Create Stuff 'N Additions CF59 patch for fluid handler capabilities
  - FluidCapability: Adds NeoForge fluid handler capabilities to jetpacks and tanks

### Technical Details
- Implements IFluidHandlerItem interface for jetpacks and tanks
- Uses dual-tank system (tank 0 always water, tank 1 always fuel) with proper capacity limits
- Fluid conversion: 1 fuel unit = 10 mB of fluid

## [0.3.1] - 2025-07-22

### Changed
- KubeJS GH972 patches will not load if
  [KubeJS Tweaks](https://www.curseforge.com/minecraft/mc-mods/kubejs-tweaks) is installed.

### Technical Details
- Enhanced configuration system for compatibility patches

## [0.3.0] - 2025-07-10

### Added
- PortableTanks GH12 patch for null compound handling
  - PortableTankItemMixin: Fixes NullPointerException in PortableTankItem$ItemFluidHandler.getFluid()
  - Prevents crashes when accessing fluid data from ItemStacks without proper NBT data
  - Uses safe null checks before accessing CompoundTag methods
  - Only loads when PortableTanks mod is present

### Technical Details
- Enhanced mixin safety with null compound validation
- Added configuration option for PortableTanks GH12 patches
- Comprehensive documentation for null pointer exception fixes

## [0.2.0] - 2025-07-08

### Added
- KubeJS GH972 patches for builder type assumption fixes
  - ServerScriptManagerMixin: Fixes ClassCastException in item registry iteration
  - KubeJSModEventHandlerMixin: Fixes ClassCastException in block entity capability registration
- Subtle Effects GH113 patch for End Remastered compatibility
  - EndRemasteredCompatMixin: Fixes NeoForge crashes with End Remastered installed
- Configuration system for enabling/disabling patches
- Debug logging for patch status
- Conditional Mixin integration for mod-dependent loading
- Comprehensive documentation for all patches

### Technical Details
- Built for Minecraft 1.21.1 with NeoForge 21.1.186+
- Uses Conditional Mixin library for safe mod integration
- Modular mixin organization by mod and issue number