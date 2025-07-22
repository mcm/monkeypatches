# PortableTanks GH12

[Link to issue](https://github.com/Dremoline/PortableTanks/issues/12)<br>
[Link to fix](https://github.com/Dremoline/PortableTanks/blob/1.21.1-neoforge/src/main/java/com/dremoline/portabletanks/PortableTankItem.java#L137)

## Overview

Fixes a NullPointerException in PortableTanks when trying to access fluid data from an ItemStack that doesn't have the required NBT compound data.

## Problem

The `PortableTankItem$ItemFluidHandler.getFluid()` method calls `compound.contains("fluid")` without checking if `compound` is null first. This occurs when:

1. An ItemStack for a PortableTank is created without proper NBT data
2. The `BaseBlock.TILE_DATA` component is missing from the ItemStack
3. The method tries to access methods on a null CompoundTag

Stack trace:
```
java.lang.NullPointerException: Cannot invoke "net.minecraft.nbt.CompoundTag.contains(String)" because "compound" is null
	at TRANSFORMER/portabletanks@1.1.7/com.dremoline.portabletanks.PortableTankItem$ItemFluidHandler.getFluid(PortableTankItem.java:137)
	at TRANSFORMER/portabletanks@1.1.7/com.dremoline.portabletanks.PortableTankItem$ItemFluidHandler.getFluidInTank(PortableTankItem.java:64)
```

## Solution

Add null checks before accessing the CompoundTag methods by redirecting the method calls to safe wrappers:

1. Redirect `compound.contains("fluid")` to return false if compound is null
2. Redirect `compound.getCompound("fluid")` to return an empty CompoundTag if compound is null

## Implementation Details

The fix uses two `@Redirect` annotations to intercept the problematic method calls:

- `safeContainsFluid()`: Safely checks if the compound contains the "fluid" key
- `safeGetCompound()`: Safely retrieves the "fluid" compound or returns an empty one

This ensures that `getFluid()` will return `FluidStack.EMPTY` instead of throwing a NullPointerException when the NBT data is missing.

## Configuration

This patch can be enabled or disabled in the mod configuration:

```toml
[patches.portabletanks]
    # Enable PortableTanks issue #12 patches
    # Fixes NullPointerException in PortableTankItem$ItemFluidHandler.getFluid()
    # Prevents crashes when accessing fluid data from ItemStacks without proper NBT data
    gh12_enabled = true
```

The patch is enabled by default. When disabled, the original (buggy) behavior is restored.

**Note:** This patch only loads when the PortableTanks mod is present. If PortableTanks is not installed, the mixin will not be applied.
