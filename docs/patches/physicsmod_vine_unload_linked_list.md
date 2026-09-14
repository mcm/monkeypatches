# Physics Mod Vine Unload - Linked List Fix

Link to issue: none (not reported upstream; Physics Mod Pro is closed source)<br>
Link to fix: none upstream yet

## Overview

Fixes a crash in Physics Mod 3.0.32 (Pro v185, NeoForge 1.21.1) when Sodium deletes chunk sections that still track vine physics objects, most visibly at the end of a resource pack reload.

## Problem

Physics Mod tracks vine physics objects (ragdolls) in `PhysicsWorld.ragdolls`, a custom `net.diebuddies.util.DoublyLinkedList`. Each element stores its own list node. Physics Mod's mixin on Sodium's `RenderSection.delete` calls `RenderSectionPhysicsData.remove`, which calls `PhysicsWorld.removeRagdoll` for the vines in that section, using the `PhysicsWorld` captured when the section was loaded.

`DoublyLinkedList.clear()`, called from `PhysicsWorld.destroy()`, only resets `head`, `tail` and `size`. The removed nodes stay linked to each other and stay stored on their elements. When one of those elements is removed later, `removeNode` finds the node is neither `head` nor `tail`, treats it as a middle node, and for the node that used to be first throws:

```
java.lang.NullPointerException: Cannot assign field "next" because "node.prev" is null
	at net.diebuddies.util.DoublyLinkedList.removeNode(DoublyLinkedList.java:67)
	at net.diebuddies.util.DoublyLinkedList.remove(DoublyLinkedList.java:45)
	at net.diebuddies.physics.PhysicsWorld.lambda$removeRagdoll$4(PhysicsWorld.java:1098)
	...
	at net.diebuddies.physics.vines.RenderSectionPhysicsData.remove(RenderSectionPhysicsData.java:28)
	at net.caffeinemc.mods.sodium.client.render.chunk.RenderSection.delete(RenderSection.java)
	at net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager.destroy(RenderSectionManager.java:724)
```

The exception escapes Sodium's `RenderSectionManager.destroy()` after it has already stopped its chunk builder threads, but before the manager is released. Minecraft then disables all resource packs and leaves the world, which calls `destroy()` again on the same manager, so the crash report shows a second, misleading error:

```
java.lang.IllegalStateException: Worker threads are not running
```

The original exception only appears under "Last reload → Recovery reason" in the crash report.

Physics Mod destroys a `PhysicsWorld` when leaving a world, when changing some settings, and from `GameRenderer.render` when a physics world is no longer active. This was reproduced against the real 3.0.32 `DoublyLinkedList` class: removing the first element after `clear()` throws exactly this exception.

## Solution

The mixin makes the list safe to remove from after `clear()`:

1. At the head of `clear`, every node reachable from `head` is unlinked, and cleared from its element if the element still points at it. Later `remove` calls for those elements see no node and return `false`, matching Physics Mod's normal "not in list" path.
2. At the head of `removeNode`, the node must look linked into this list: it is the `head` or `tail`, or both neighbours point back at it. Otherwise the removal is skipped instead of throwing or relinking unrelated nodes. This is an O(1) neighbour check rather than a full membership test (the list is also used for rigid bodies, so walking it on every removal would be costly). A middle node of some other intact chain still passes, but with step 1 in place `clear()` no longer leaves such chains behind, and Physics Mod doesn't move elements between lists.

Both steps were tested against the real 3.0.32 `DoublyLinkedList` class: normal removals are unaffected, removing any element after `clear()` is a no-op, and the list can be reused afterwards.

## Implementation Details

**Files**: `DoublyLinkedListMixin.java`, `LinkedListGuard.java`  
**Packages**: `io.mcmaster.monkeypatches.mixin.physicsmod.vine_unload`, `io.mcmaster.monkeypatches.fixes.physicsmod.vine_unload`

### Target Class
- `net.diebuddies.util.DoublyLinkedList`

### Mixin Type
- `@Inject` at `HEAD` of `removeNode` (cancellable), with the `Node` parameter taken as `@Coerce Object`
- `@Inject` at `HEAD` of `clear`

Physics Mod isn't available at compile time, so `LinkedListGuard` reaches the list's private fields and the nested `Node` / `NodeStorage` types through a `MethodHandles.Lookup` created in the mixin's static initializer, which runs inside the target class.

### Version Restriction
- Only loads for Physics Mod `3.0.32`, since the field lookups depend on the list's internal structure

## Configuration

- **Config Option**: `patches.physicsmod.vine_unload_fix_enabled` (default: true)
- **Condition Method**: `MixinConditions.shouldApplyPhysicsModVineUnload()`
