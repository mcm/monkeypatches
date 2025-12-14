# Copper Age Backport PR54

[Link to PR](https://github.com/Smallinger/Copper-Age-Backport/pull/54)<br>
[Link to issue](https://github.com/Smallinger/Copper-Age-Backport/issues/48)

## Overview

Fixes copper armor having infinite durability by adding explicit durability values during item registration. This prevents copper armor pieces from lasting forever when damaged.

## Problem

The copper armor items (helmet, chestplate, leggings, boots) in Copper Age Backport version 1.21.1 are registered without calling `.durability()` on their `Item.Properties`. This causes the armor to never break when damaged, even after sustaining significant damage.

The issue was reported in GitHub issue #48 titled "Copper Armor never breaks" by user Inonedn. The contributor mcm noted that this likely worked correctly in version 1.20.1 but broke in 1.21.1, suggesting a regression or API change in the newer Minecraft version.

## Solution

Add explicit durability values to all four copper armor pieces by calling `.durability()` on the `Item.Properties` during item registration. The durability is calculated using the standard Minecraft armor durability system with a multiplier of **11**, which positions copper armor's durability between leather and iron armor.

The fix adds the following to each armor piece's properties:
- **Helmet**: `.durability(ArmorItem.Type.HELMET.getDurability(11))`
- **Chestplate**: `.durability(ArmorItem.Type.CHESTPLATE.getDurability(11))`
- **Leggings**: `.durability(ArmorItem.Type.LEGGINGS.getDurability(11))`
- **Boots**: `.durability(ArmorItem.Type.BOOTS.getDurability(11))`

This results in the following durability values for each piece:
- Helmet: 143 durability
- Chestplate: 208 durability
- Leggings: 195 durability
- Boots: 169 durability

## Implementation Details

**File**: `ModItemsMixin.java`  
**Package**: `io.mcmaster.monkeypatches.mixin.copperagebackport.GH48`

### Target Class
- `com.github.smallinger.copperagebackport.registry.ModItems`

### Mixin Type
- `@Redirect` on the `Item.Properties.stacksTo()` calls in the lambda methods for each armor piece registration

### How It Works

The mixin redirects the `stacksTo()` method call in each copper armor registration lambda to add durability to the properties chain before they are passed to the `ArmorItem` constructor.

Four separate `@Redirect` methods target specific lambda methods that register each armor piece:
1. `lambda$register$74` - Copper Helmet
2. `lambda$register$75` - Copper Chestplate
3. `lambda$register$76` - Copper Leggings
4. `lambda$register$77` - Copper Boots

Each redirect method:
1. Calls the original `stacksTo(pMaxStackSize)` method
2. Chains `.durability(ArmorItem.Type.[TYPE].getDurability(11))` to add the appropriate durability
3. Returns the modified properties to be used in the `ArmorItem` constructor

This approach works by intercepting the property builder chain at a specific point where we can safely add the durability call.

## Configuration

This patch does not have a configuration option as mixins are applied during early class loading, before the mod configuration system is initialized. The patch is automatically applied whenever Copper Age Backport version 0.1.4 or earlier is present.

This ensures the fix is always available when needed and eliminates any potential timing issues with configuration loading.

**Note:** This patch only loads when Copper Age Backport version 0.1.4 or earlier is present (version predicate `[,0.1.4]`). If the mod is not installed or is version 0.1.5+, the mixin will not be applied.

## Technical Notes

- **Durability Multiplier**: The value 11 is used as the durability multiplier for copper armor, which is consistent with the material's positioning between leather (durability multiplier 5) and iron (durability multiplier 15) in the armor tiers.
- **Lambda Methods**: The mixin targets compiler-generated lambda methods (`lambda$static$N`) which are created during the static initialization of the ModItems class.
- **Remapping**: The mixin uses `remap = false` for the mixin class and methods since the target is a mod class, but uses `remap = true` for the ArmorItem constructor injection point since that's a Minecraft class.
- **Version Constraint**: This patch only applies to Copper Age Backport version 0.1.4 and earlier using the version predicate `[,0.1.4]`. PR #54 was merged on December 12, 2025, so future releases (0.1.5+) will include the fix natively and won't need this patch.
