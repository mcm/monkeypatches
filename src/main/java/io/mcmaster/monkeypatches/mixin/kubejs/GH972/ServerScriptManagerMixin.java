package io.mcmaster.monkeypatches.mixin.kubejs.GH972;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import io.mcmaster.monkeypatches.util.MixinConditions;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;

import java.util.Iterator;

/**
 * Mixin for ServerScriptManager to fix assumptions about concrete builder
 * types.
 * 
 * This fix addresses the issue where the code assumed all objects in ITEM
 * were ItemBuilder instances, which isn't always true due to
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
@Mixin(value = ServerScriptManager.class, remap = false)
public class ServerScriptManagerMixin {

    /**
     * Redirects the iterator() call to filter out non-ItemBuilder instances.
     * This prevents ClassCastException when CustomBuilderObject instances are
     * present.
     * 
     * Only applies the redirect if the KubeJS GH972 patches are enabled in
     * configuration.
     */
    @Redirect(method = "loadAdditional", at = @At(value = "INVOKE", target = "Ldev/latvian/mods/kubejs/registry/RegistryObjectStorage;iterator()Ljava/util/Iterator;"), remap = false)
    private Iterator<?> filterItemBuilders(RegistryObjectStorage<?> storage) {
        // Check if the patch is enabled, if not, return the original iterator
        if (!MixinConditions.shouldApplyKubeJSGH972()) {
            return storage.iterator();
        }

        // Apply the patch: filter out non-ItemBuilder instances
        return storage.objects.values().stream()
                .filter(ItemBuilder.class::isInstance)
                .iterator();
    }
}
