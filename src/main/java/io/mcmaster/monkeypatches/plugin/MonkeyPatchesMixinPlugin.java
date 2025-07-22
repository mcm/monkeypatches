package io.mcmaster.monkeypatches.plugin;

import java.util.List;
import java.util.Set;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;

/**
 * Custom mixin configuration plugin that uses conditional mixin to control
 * mixin application based on mod loading and configuration.
 */
public class MonkeyPatchesMixinPlugin extends RestrictiveMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return super.shouldApplyMixin(targetClassName, mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Default implementation - no additional targets to accept
    }

    @Override
    public List<String> getMixins() {
        return null;
    }
}
