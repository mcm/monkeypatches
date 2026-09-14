package io.mcmaster.monkeypatches.mixin.physicsmod.vine_unload;

import java.lang.invoke.MethodHandles;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.mcmaster.monkeypatches.fixes.physicsmod.vine_unload.LinkedListGuard;
import io.mcmaster.monkeypatches.util.MixinConditions;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;

/**
 * Mixin for Physics Mod's DoublyLinkedList to fix a crash when vine physics
 * objects are removed after their physics world was destroyed.
 *
 * Problem: Vine physics objects (ragdolls) are tracked in
 * {@code PhysicsWorld.ragdolls}. Each chunk section keeps a
 * RenderSectionPhysicsData holding its PhysicsWorld and ragdolls, and removes
 * them when Sodium deletes the section (e.g. at the end of a resource pack
 * reload). {@code DoublyLinkedList.clear()}, called from
 * {@code PhysicsWorld.destroy()}, drops head/tail but leaves nodes linked and
 * stored on their elements. Removing such an element later takes the
 * "middle node" branch of {@code removeNode} and throws
 * {@code NullPointerException: Cannot assign field "next" because "node.prev" is null}.
 * The exception escapes Sodium's {@code RenderSectionManager.destroy()}, so the
 * next teardown fails with {@code IllegalStateException: Worker threads are not running}.
 *
 * Solution: {@code clear()} detaches every node and clears it from its
 * element, so later removals of those elements are no-ops. As a second guard,
 * {@code removeNode} is skipped for nodes whose neighbours don't point back at
 * them (see {@link LinkedListGuard#isLinked}), which covers stale nodes that
 * were left behind before this patch applied.
 *
 * Only applies to Physics Mod 3.0.32 (Pro v185), the version this was verified
 * against, since the field lookups depend on the list's internal structure.
 */
@Restriction(require = @Condition(value = "physicsmod", versionPredicates = "3.0.32"))
@Mixin(targets = "net.diebuddies.util.DoublyLinkedList", remap = false)
public class DoublyLinkedListMixin {
    @Unique
    private static final LinkedListGuard monkeypatches$guard = LinkedListGuard.create(MethodHandles.lookup());

    @Inject(method = "removeNode", at = @At("HEAD"), cancellable = true)
    private void monkeypatches$skipUnlinkedNode(@Coerce Object node, CallbackInfo ci) {
        if (MixinConditions.shouldApplyPhysicsModVineUnload() && !monkeypatches$guard.isLinked(this, node)) {
            ci.cancel();
        }
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void monkeypatches$detachNodes(CallbackInfo ci) {
        if (MixinConditions.shouldApplyPhysicsModVineUnload()) {
            monkeypatches$guard.detachAll(this);
        }
    }
}
