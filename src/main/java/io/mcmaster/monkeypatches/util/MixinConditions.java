package io.mcmaster.monkeypatches.util;

import io.mcmaster.monkeypatches.Config;
import net.neoforged.fml.ModList;

/**
 * Utility class for checking mixin conditions based on configuration and mod
 * loading.
 * This allows mixins to be conditionally disabled based on user configuration.
 * 
 * Note: Conditional Mixin library handles mod loading checks at mixin
 * application time,
 * but these methods provide runtime checks for additional safety.
 */
public class MixinConditions {
    /**
     * Returns whether KubeJS GH972 patches should be applied.
     * Used in @Redirect mixins to conditionally apply redirects.
     * 
     * Checks both mod loading and configuration.
     * These patches are not applied if kubejstweaks is installed, as it provides
     * its own fixes for this issue.
     */
    public static boolean shouldApplyKubeJSGH972() {
        // The @Restriction annotation should prevent this mixin from loading if kubejs
        // is not present or if kubejstweaks is present,
        // but we can add an additional runtime check for safety
        return ModList.get().isLoaded("kubejs") &&
                !ModList.get().isLoaded("kubejstweaks") &&
                Config.PatchConfig.isKubeJSGH972Enabled();
    }

    /**
     * Returns whether PortableTanks GH12 patches should be applied.
     * Used in @Redirect mixins to conditionally apply redirects.
     * 
     * Checks both mod loading and configuration.
     */
    public static boolean shouldApplyPortableTanksGH12() {
        // The @Restriction annotation should prevent this mixin from loading if
        // portabletanks
        // is not present, but we can add an additional runtime check for safety
        return ModList.get().isLoaded("portabletanks") && Config.PatchConfig.isPortableTanksGH12Enabled();
    }

    /**
     * Returns whether JustEnoughResources GH547 patches should be applied.
     * Used in @Inject mixins to conditionally apply new logic.
     * 
     * Checks both mod loading and configuration.
     */
    public static boolean shouldApplyJustEnoughResourcesGH547() {
        return ModList.get().isLoaded("jeresources") &&
                Config.PatchConfig.isJustEnoughResourcesGH547Enabled();
    }

    /**
     * Returns whether Patchouli GH790 patches should be applied.
     * Used in @Redirect mixins to conditionally apply redirects.
     * 
     * Checks both mod loading and configuration.
     */
    public static boolean shouldApplyPatchouliGH790() {
        return ModList.get().isLoaded("patchouli") &&
                Config.PatchConfig.isPatchouliGH790Enabled();
    }

    // Future condition methods can be added here
    // Example:
    // public static boolean shouldApplySomeOtherModIssue() {
    // return ModList.get().isLoaded("someothermod") &&
    // Config.PatchConfig.isSomeOtherModIssueEnabled();
    // }
}
