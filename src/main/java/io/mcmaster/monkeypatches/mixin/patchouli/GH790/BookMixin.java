package io.mcmaster.monkeypatches.mixin.patchouli.GH790;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.mcmaster.monkeypatches.util.MixinConditions;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.Level;

/**
 * Mixin for Patchouli's Book class to fix registry access issues during book
 * loading.
 * 
 * Fixes: https://github.com/VazkiiMods/Patchouli/issues/790
 * 
 * Problem: Patchouli fails to load custom book items on startup with
 * CommandSyntaxException when the target mod hasn't finished loading yet. This
 * happens because the Book constructor attempts to parse custom book items
 * using registry-based vanilla logic before the book's mod has registered its
 * items.
 * 
 * Solution: Redirect the VanillaRegistries.createLookup() call to use the
 * current level's registry access when available, falling back to vanilla
 * registries when level is not available.
 * 
 * This is an expansion on the fix in the Github issue, which still encounters
 * issues in certain cases.
 */
@Restriction(require = @Condition("patchouli"))
@Mixin(targets = "vazkii.patchouli.common.book.Book")
public class BookMixin {
    /**
     * Redirects the VanillaRegistries.createLookup() call to use the current
     * level's registry when available, preventing registry access issues during mod
     * loading.
     * 
     * @return HolderLookup.Provider that uses current level registry or falls back
     *         to vanilla
     */
    @Redirect(method = "lambda$new$1(Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lvazkii/patchouli/xplat/XplatModContainer;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/registries/VanillaRegistries;createLookup()Lnet/minecraft/core/HolderLookup$Provider;"))
    private static HolderLookup.Provider createLookup() {
        // Check if the patch is enabled, if not, return the original stream
        if (!MixinConditions.shouldApplyPatchouliGH790())
            return VanillaRegistries.createLookup();

        Level level = Minecraft.getInstance().level;
        if (level == null)
            return VanillaRegistries.createLookup();

        RegistryAccess registry = level.registryAccess();
        return new RegistryAccess.ImmutableRegistryAccess(registry.registries());
    }
}
