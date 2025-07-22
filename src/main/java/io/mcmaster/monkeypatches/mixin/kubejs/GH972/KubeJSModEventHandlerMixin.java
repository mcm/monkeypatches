package io.mcmaster.monkeypatches.mixin.kubejs.GH972;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.latvian.mods.kubejs.KubeJSModEventHandler;
import io.mcmaster.monkeypatches.util.MixinConditions;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;

import java.util.stream.Stream;

/**
 * Mixin for KubeJSModEventHandler to fix assumptions about concrete builder
 * types.
 * 
 * This fix addresses the issue where the code assumed all objects in
 * BLOCK_ENTITY
 * were BlockEntityBuilder instances, which isn't always true due to
 * CustomBuilderObject.
 * 
 * Related to KubeJS issue #972
 * Based on commit:
 * https://github.com/KubeJS-Mods/KubeJS/commit/c4f0afa53516ba778ddb40e4b8045890f3f1aa5c
 * 
 * This mixin is disabled when kubejstweaks is installed, as it provides its own
 * fixes for this issue.
 */
@Restriction(require = @Condition("kubejs"), conflict = @Condition("kubejstweaks"))
@Mixin(value = KubeJSModEventHandler.class, remap = false)
public class KubeJSModEventHandlerMixin {

    /**
     * Redirects the stream() call in registerCapabilities to add proper filtering
     * for BlockEntityBuilder instances before attempting to cast.
     * 
     * Only applies the redirect if the KubeJS GH972 patches are enabled in
     * configuration.
     */
    @Redirect(method = "registerCapabilities", at = @At(value = "INVOKE", target = "Ljava/util/Collection;stream()Ljava/util/stream/Stream;"), remap = false)
    private static Stream<Object> addBlockEntityBuilderFilter(java.util.Collection<Object> collection) {
        // Check if the patch is enabled, if not, return the original stream
        if (!MixinConditions.shouldApplyKubeJSGH972()) {
            return collection.stream();
        }

        // Apply the patch: filter for BlockEntityBuilder instances
        return collection.stream()
                .filter(dev.latvian.mods.kubejs.block.entity.BlockEntityBuilder.class::isInstance);
    }
}
