package io.mcmaster.monkeypatches.mixin.portabletanks.GH12;

import io.mcmaster.monkeypatches.util.MixinConditions;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes NullPointerException in PortableTankItem$ItemFluidHandler.getFluid()
 * when compound is null.
 * 
 * GitHub Issue: https://github.com/Dremoline/PortableTanks/issues/12
 * 
 * The issue occurs when getFluid() tries to call compound.contains("fluid")
 * without checking if compound is null first.
 */
@Restriction(require = @Condition("portabletanks"))
@Mixin(targets = "com.dremoline.portabletanks.PortableTankItem$ItemFluidHandler")
public class PortableTankItemMixin {

    /**
     * Redirects the compound.contains("fluid") call to safely handle null
     * compounds.
     * Only applies the redirect if the PortableTanks GH12 patches are enabled in
     * configuration.
     */
    @Redirect(method = "getFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;)Z"))
    private boolean safeContainsFluid(CompoundTag compound, String key) {
        // Check if the patch is enabled, if not, return the original behavior
        if (!MixinConditions.shouldApplyPortableTanksGH12()) {
            return compound.contains(key);
        }

        // Apply the patch: safe null check
        return compound != null && compound.contains(key);
    }

    /**
     * Redirects the compound.getCompound("fluid") call to safely handle null
     * compounds.
     * Only applies the redirect if the PortableTanks GH12 patches are enabled in
     * configuration.
     */
    @Redirect(method = "getFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getCompound(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag safeGetCompound(CompoundTag compound, String key) {
        // Check if the patch is enabled, if not, return the original behavior
        if (!MixinConditions.shouldApplyPortableTanksGH12()) {
            return compound.getCompound(key);
        }

        // Apply the patch: safe null check
        return compound != null ? compound.getCompound(key) : new CompoundTag();
    }
}
