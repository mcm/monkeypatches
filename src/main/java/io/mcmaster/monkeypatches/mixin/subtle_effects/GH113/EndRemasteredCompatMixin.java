package io.mcmaster.monkeypatches.mixin.subtle_effects.GH113;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Mixin for EndRemasteredCompat to fix NeoForge crashing with End Remastered
 * installed.
 * 
 * This fix addresses the issue where Subtle Effects was trying to access End
 * Remastered's
 * CommonBlockRegistry.ANCIENT_PORTAL_FRAME during initialization, causing a
 * "Registry is already frozen" exception.
 * 
 * The fix changes from using ModBlockTickers.REGISTERED.put with a specific
 * block to using ModBlockTickers.REGISTERED_SPECIAL.put with a predicate that
 * checks if a block state matches the ancient portal frame.
 * 
 * Note: This patch only applies to Subtle Effects version 1.11.0 specifically,
 * as the issue only exists in that version.
 * 
 * Related to Subtle Effects issue #113
 * Based on commit:
 * https://github.com/MincraftEinstein/SubtleEffects/commit/b73e58ca65f37ec8577508e9cf00d148dcfea4eb
 */
// @Restriction(require = @Condition(value = "subtle_effects"))
@Restriction(require = @Condition(value = "subtle_effects", versionPredicates = "1.11.0"))
@Mixin(targets = "einstein.subtle_effects.compat.EndRemasteredCompat", remap = false)
public class EndRemasteredCompatMixin {

    /**
     * Redirects the Map.put call in the init() method to use REGISTERED_SPECIAL
     * instead of REGISTERED when Subtle Effects version 1.11.0 is detected.
     * 
     * This prevents the crash by avoiding direct access to the block registry
     * during initialization and instead using a predicate-based approach.
     * 
     * Includes runtime version checking to ensure it only applies to version
     * 1.11.0.
     */
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), remap = false, require = 0)
    private static Object redirectMapPut(Map<Object, Object> map, Object key, Object value) {
        // Apply the patch: Use REGISTERED_SPECIAL with a predicate instead
        // Note: This patch applies when subtle_effects version 1.11.0 is loaded
        try {
            // Get the REGISTERED_SPECIAL map using reflection
            Class<?> modBlockTickersClass = Class.forName("einstein.subtle_effects.init.ModBlockTickers");
            java.lang.reflect.Field registeredSpecialField = modBlockTickersClass.getField("REGISTERED_SPECIAL");
            @SuppressWarnings("unchecked")
            Map<Predicate<BlockState>, Object> registeredSpecial = (Map<Predicate<BlockState>, Object>) registeredSpecialField
                    .get(null);

            // Create a predicate that checks if the block state is an ancient portal frame
            Predicate<BlockState> ancientPortalFramePredicate = state -> {
                try {
                    Class<?> commonBlockRegistryClass = Class
                            .forName("com.teamremastered.endrem.registry.CommonBlockRegistry");
                    java.lang.reflect.Field ancientPortalFrameField = commonBlockRegistryClass
                            .getField("ANCIENT_PORTAL_FRAME");
                    Object ancientPortalFrame = ancientPortalFrameField.get(null);
                    return state.is((net.minecraft.world.level.block.Block) ancientPortalFrame);
                } catch (Exception e) {
                    // If we can't access the End Remastered block, fall back to false
                    return false;
                }
            };

            // Put the predicate and ticker into REGISTERED_SPECIAL
            return registeredSpecial.put(ancientPortalFramePredicate, value);
        } catch (Exception e) {
            // If reflection fails, fall back to original behavior
            return map.put(key, value);
        }
    }
}
