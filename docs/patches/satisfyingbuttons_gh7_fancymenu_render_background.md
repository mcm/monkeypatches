# Satisfying Buttons GH7 - FancyMenu Render Background Fix

[Link to issue](https://github.com/txnimc/SatisfyingButtons/issues/7)<br>
Link to fix: none upstream yet

## Overview

Fixes a startup crash when Satisfying Buttons (1.1.2 / reports as 1.1.1) is installed alongside FancyMenu 3.9.x (verified against FancyMenu 3.9.11 for NeoForge 1.21.1).

## Problem

Satisfying Buttons' `FancyMenuButtonMixin` uses a `@WrapOperation` on the `WidgetSprites.get(ZZ)` call inside FancyMenu's `ExtendedButton.renderBackground`, so that the vanilla button texture doesn't jump to the hovered sprite immediately. The released jar targets the method by its full descriptor:

```
renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V
```

FancyMenu 3.9.x changed that method to `renderBackground(GuiGraphics, float)`. Since Satisfying Buttons' mixin config sets `defaultRequire: 1`, the injector fails validation and the game crashes when FancyMenu first loads `ExtendedButton`:

```
Mixin [mixins.satisfying_buttons.json:FancyMenuButtonMixin from mod satisfying_buttons] from phase [DEFAULT] in config [mixins.satisfying_buttons.json] FAILED during APPLY
InvalidInjectionException: Critical injection failure: @WrapOperation annotation on render could not find any targets matching 'renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V' in de/keksuccino/fancymenu/util/rendering/ui/widget/button/ExtendedButton.
```

The rest of the mixin is still compatible: `renderWidget(GuiGraphics, int, int, float)` is unchanged, the new `renderBackground` still calls `WidgetSprites.get(ZZ)`, and the FancyMenu methods Satisfying Buttons calls (`getCustomBackgroundNormalFancyMenu`, `isNineSliceCustomBackgroundTexture_FancyMenu`, `getBackgroundColorNormal`) still exist.

## Solution

A [MixinSquared](https://github.com/Bawnorton/MixinSquared) annotation adjuster rewrites the `@WrapOperation` method selector from the old full descriptor to the bare name `renderBackground`. `ExtendedButton` declares exactly one `renderBackground` method in both older and newer FancyMenu versions, so the injector finds its target and the fade-in texture feature keeps working.

A regular mixin can't fix this, because the broken selector is in another mod's mixin annotation and not in a target class.

## Implementation Details

**File**: `FancyMenuButtonMixinAdjuster.java`  
**Package**: `io.mcmaster.monkeypatches.adjuster.satisfying_buttons.GH7`  
**Registration**: `src/main/resources/META-INF/services/com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster`

### Target Mixin
- `toni.satisfyingbuttons.mixin.FancyMenuButtonMixin` (from `mixins.satisfying_buttons.json`)

### Adjustment
- Only `@WrapOperation` annotations on that mixin are touched
- A `method` entry is only rewritten if it exactly matches `renderBackground(Lnet/minecraft/client/gui/GuiGraphics;)V`, so the adjuster becomes a no-op if Satisfying Buttons changes the selector upstream

### Dependencies
- MixinSquared 0.3.4 (`mixinsquared-neoforge`), bundled via jarJar

## Configuration

No configuration option is available. Annotation adjusters run while mixin configs load, before the config system initializes.
