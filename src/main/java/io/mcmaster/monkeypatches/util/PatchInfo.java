package io.mcmaster.monkeypatches.util;

import io.mcmaster.monkeypatches.MonkeyPatches;
import net.neoforged.fml.ModList;

/**
 * Utility class for managing patch information and organization.
 */
public class PatchInfo {

    /**
     * Information about a patch group.
     */
    public static class PatchGroup {
        public final String modName;
        public final String modId;
        public final String issueId;
        public final String description;
        public final boolean configEnabled;
        public final boolean modLoaded;

        public PatchGroup(String modName, String modId, String issueId, String description, boolean configEnabled) {
            this.modName = modName;
            this.modId = modId;
            this.issueId = issueId;
            this.description = description;
            this.configEnabled = configEnabled;
            this.modLoaded = ModList.get().isLoaded(modId);
        }

        @Override
        public String toString() {
            String status;
            if (!modLoaded) {
                status = "MOD NOT LOADED";
            } else if (!configEnabled) {
                status = "DISABLED";
            } else {
                status = "ENABLED";
            }
            return String.format("%s %s: %s [%s]", modName, issueId, description, status);
        }

        /**
         * Returns true if this patch group is effectively active (mod loaded and config
         * enabled).
         */
        public boolean isActive() {
            return modLoaded && configEnabled;
        }
    }

    /**
     * Log information about all registered patches.
     */
    public static void logPatchStatus() {
        MonkeyPatches.LOGGER.info("=== Monkey Patches Status ===");

        // KubeJS patches
        logPatchGroup(new PatchGroup(
                "KubeJS",
                "kubejs",
                "GH972",
                "Fixes assumptions about concrete builder types",
                io.mcmaster.monkeypatches.Config.PatchConfig.isKubeJSGH972Enabled()));

        // PortableTanks patches
        logPatchGroup(new PatchGroup(
                "PortableTanks",
                "portabletanks",
                "GH12",
                "Fixes NullPointerException in fluid handling",
                io.mcmaster.monkeypatches.Config.PatchConfig.isPortableTanksGH12Enabled()));

        // PortableTanks patches
        logPatchGroup(new PatchGroup(
                "Create Stuff 'N Additions",
                "create_sa",
                "CF59",
                "Adds fluid handler capabilities to gadgets and tanks",
                io.mcmaster.monkeypatches.Config.PatchConfig.isCreateStuffAdditionsCF59Enabled()));

        // PortableTanks patches
        logPatchGroup(new PatchGroup(
                "Just Enough Resources",
                "jeresources",
                "GH547",
                "Prevents missing JER tabs when using modded biomes",
                io.mcmaster.monkeypatches.Config.PatchConfig.isJustEnoughResourcesGH547Enabled()));

        // Future patches can be logged here

        MonkeyPatches.LOGGER.info("=== End Patch Status ===");
    }

    private static void logPatchGroup(PatchGroup group) {
        MonkeyPatches.LOGGER.info("  {}", group);
    }
}
