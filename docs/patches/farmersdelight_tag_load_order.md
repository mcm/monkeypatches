# Farmers Delight Tag Load Order

[Link to downstream issue (FungiDelight #8)](https://github.com/MehdiNoui/FungiDelight/issues/8)<br>
[Link to upstream fix (FD commit `5f84f22`)](https://github.com/vectorwing/FarmersDelight/commit/5f84f22)

## Overview

Fixes an occasional `NullPointerException` in Farmers Delight 1.3.1 when any code calls `BlockState.is(ModTags.X)` (or the item / entity equivalents) on a deprecated top-level tag reference such as `ModTags.MUSHROOM_COLONY_GROWABLE_ON`. The bug surfaces in addons like FungiDelight at random tick time, but the root cause is in Farmers Delight's `ModTags` class.

## Problem

In Farmers Delight 1.3.1, the deprecated top-level tag references in `vectorwing.farmersdelight.common.tag.ModTags` are declared as references to the inner `Blocks` / `Items` / `EntityTypes` static fields:

```java
@Deprecated(forRemoval = true)
public static final TagKey<Block> MUSHROOM_COLONY_GROWABLE_ON = Blocks.MUSHROOM_COLONY_GROWABLE_ON;
```

This sets up a class-init load-order race. If the first access path in a session goes through an inner-class field, the JVM begins initializing `ModTags$Blocks`. Its assignments call `ModTags.modBlockTag(...)`, which forces the outer `ModTags.<clinit>` to run before `Blocks.<clinit>` finishes. The outer initializer then evaluates `Blocks.MUSHROOM_COLONY_GROWABLE_ON` — but `Blocks` is already mid-initialization on the same thread. Per JLS §12.4.2, that recursive request returns immediately with the field's current value, which is `null`. The outer `static final` field permanently captures `null`.

Symptom (from FungiDelight #8):

```
java.lang.NullPointerException
    at java.base/java.util.Objects.requireNonNull(Objects.java:233)
    at java.base/java.util.ImmutableCollections$SetN.contains(ImmutableCollections.java:944)
    at minecraft@1.21.1/net.minecraft.core.Holder$Reference.is(Holder.java:169)
    at minecraft@1.21.1/net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase.is(BlockBehaviour.java:808)
    at fungidelight@1.3/.../MorelMushroomBlock.randomTick(MorelMushroomBlock.java:31)
```

The NPE comes from `ImmutableSet.contains(null)` — i.e. the `TagKey` argument was `null`.

The bug only exists in Farmers Delight 1.3.1: 1.3.0 removed the deprecated outer fields entirely, 1.3.1 re-introduced them as references to the inner fields, and the upstream fix commit `5f84f22` (slated for 1.3.2) makes each deprecated outer field independent again by calling `modBlockTag` / `modItemTag` / `modEntityTag` directly.

## Solution

`@Inject` at `RETURN` of `ModTags.<clinit>`. By the time the static initializer is about to return, the inner classes are guaranteed to be fully initialized (the recursive call has returned and the inner `<clinit>` has completed). At that point we walk every deprecated outer field and, if any captured `null` due to the race, reconstruct the matching `TagKey` from its known namespace and path.

Reassigning a non-null field is unnecessary and is skipped — the patch is a no-op when the bug didn't fire.

## Implementation Details

- `@Mixin(targets = "vectorwing.farmersdelight.common.tag.ModTags", remap = false)` targets the FD class by string so no compile-time dependency on Farmers Delight is required.
- `@Mutable @Shadow public static TagKey<...> X` declarations remove `final` at mixin-application time so the outer fields can be reassigned from outside `<clinit>`.
- `@Inject(method = "<clinit>", at = @At("RETURN"))` schedules the repair at the end of the static initializer.
- The repair method only reassigns fields that are currently `null`, minimizing behavioral change.
- New `TagKey` instances are created via `BlockTags.create` / `ItemTags.create` / `TagKey.create(Registries.ENTITY_TYPE, ...)` with the same `farmersdelight:<path>` resource location the inner-class field would have produced. `TagKey` is a record-like type that uses value equality, so a freshly-constructed key with the right resource location is interchangeable with the original.

## Configuration

This patch cannot be disabled via configuration. The injected code runs during `ModTags`'s static initializer, which executes during early class loading — well before the mod configuration system is available.

The patch is scoped to Farmers Delight `[1.3.1, 1.3.2)` via the `@Restriction` annotation:

- It does **not** apply on FD 1.3.0 (the deprecated outer fields don't exist there, so the `@Shadow` declarations would fail).
- It does **not** apply on FD 1.3.2 or later (those releases include the upstream fix).
- It does **not** load at all when Farmers Delight is absent.
