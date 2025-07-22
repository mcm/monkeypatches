package io.mcmaster.monkeypatches;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration class for Monkey Patches mod.
 * Controls which patches are enabled and other mod settings.
 */
public class Config {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        // Patch toggles section
        static {
                BUILDER.comment("Patch Settings")
                                .comment("Control which patches are enabled or disabled")
                                .push("patches");
        }

        // KubeJS patches
        static {
                BUILDER.comment("KubeJS Patches")
                                .comment("Patches for the KubeJS mod")
                                .push("kubejs");
        }

        public static final ModConfigSpec.BooleanValue KUBEJS_GH972_ENABLED = BUILDER
                        .comment("Enable KubeJS issue #972 patches")
                        .comment("Fixes assumptions about concrete builder types that could cause ClassCastException")
                        .comment("Includes ServerScriptManagerMixin and KubeJSModEventHandlerMixin")
                        .define("gh972_enabled", true);

        static {
                BUILDER.pop(); // kubejs
        }

        // PortableTanks patches
        static {
                BUILDER.comment("PortableTanks Patches")
                                .comment("Patches for the PortableTanks mod")
                                .push("portabletanks");
        }

        public static final ModConfigSpec.BooleanValue PORTABLETANKS_GH12_ENABLED = BUILDER
                        .comment("Enable PortableTanks issue #12 patches")
                        .comment("Fixes NullPointerException in PortableTankItem$ItemFluidHandler.getFluid()")
                        .comment("Prevents crashes when accessing fluid data from ItemStacks without proper NBT data")
                        .define("gh12_enabled", true);

        static {
                BUILDER.pop(); // portabletanks
        }

        // Future mod patches can be added here
        // Example:
        // static {
        // BUILDER.comment("SomeOtherMod Patches")
        // .comment("Patches for SomeOtherMod")
        // .push("someothermod");
        // }
        //
        // public static final ModConfigSpec.BooleanValue SOMEOTHERMOD_ISSUE_ENABLED =
        // BUILDER
        // .comment("Enable SomeOtherMod issue patches")
        // .define("issue_enabled", true);
        //
        // static {
        // BUILDER.pop(); // someothermod
        // }

        static {
                BUILDER.pop(); // patches
        }

        static final ModConfigSpec SPEC = BUILDER.build();

        /**
         * Utility methods for checking if patches are enabled
         */
        public static class PatchConfig {

                /**
                 * Check if KubeJS issue #972 patches are enabled
                 */
                public static boolean isKubeJSGH972Enabled() {
                        return KUBEJS_GH972_ENABLED.get();
                }

                /**
                 * Check if PortableTanks issue #12 patches are enabled
                 */
                public static boolean isPortableTanksGH12Enabled() {
                        return PORTABLETANKS_GH12_ENABLED.get();
                }

                // Future patch check methods can be added here
                // Example:
                // public static boolean isSomeOtherModIssueEnabled() {
                // return SOMEOTHERMOD_ISSUE_ENABLED.get();
                // }
        }
}
