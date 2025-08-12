package io.mcmaster.monkeypatches.mixin.jeresources.GH547;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.mcmaster.monkeypatches.util.MixinConditions;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

@Restriction(require = @Condition("jeresources"))
@Mixin(targets = "jeresources.api.util.BiomeHelper", remap = false)
public class BiomeHelperMixin {
    /*
     * Original methods from JER are embedded here since I can't figure out how to
     * call static methods from the original class when you overwrite them. But I
     * have to overwrite them since they're static.
     */
    private static List<Biome> super$getAllBiomes() {
        List<Biome> biomes = new ArrayList<>();
        VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME).listElements().map(Holder.Reference::value)
                .forEach(biomes::add);
        return biomes;
    }

    private static Biome super$getBiome(ResourceKey<Biome> key) {
        return VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME).getOrThrow(key).value();
    }

    private static List<Biome> super$getBiomes(ResourceKey<Biome> category) {
        List<Biome> biomes = new ArrayList<>();
        VanillaRegistries.createLookup().lookupOrThrow(Registries.BIOME).listElements().forEach(
                biome_entry -> {
                    if (biome_entry.key().equals(category)) {
                        biomes.add(biome_entry.value());
                    }
                });
        return biomes;
    }

    @Overwrite
    public static List<Biome> getAllBiomes() {
        // Check if the patch is enabled, if not, return the original stream
        if (!MixinConditions.shouldApplyJustEnoughResourcesGH547())
            return super$getAllBiomes();

        List<Biome> biomes = new ArrayList<>();
        Level level = Minecraft.getInstance().level;
        if (level == null)
            return super$getAllBiomes();
        Optional<Registry<Biome>> registry = level.registryAccess().registry(Registries.BIOME);
        if (registry.isEmpty())
            return super$getAllBiomes();

        registry.get().forEach(biomes::add);
        return biomes;
    }

    @Overwrite
    public static Biome getBiome(ResourceKey<Biome> key) {
        // Check if the patch is enabled, if not, return the original stream
        if (!MixinConditions.shouldApplyJustEnoughResourcesGH547())
            return super$getBiome(key);

        Level level = Minecraft.getInstance().level;
        if (level == null)
            return super$getBiome(key);
        Optional<Registry<Biome>> registry = level.registryAccess().registry(Registries.BIOME);
        if (registry.isEmpty())
            return super$getBiome(key);

        Biome biome = registry.get().get(key);
        return biome;
    }

    @Overwrite
    public static List<Biome> getBiomes(ResourceKey<Biome> category) {
        // Check if the patch is enabled, if not, return the original stream
        if (!MixinConditions.shouldApplyJustEnoughResourcesGH547())
            return super$getBiomes(category);

        List<Biome> biomes = new ArrayList<>();
        Level level = Minecraft.getInstance().level;
        if (level == null)
            return super$getBiomes(category);
        Optional<Registry<Biome>> registry = level.registryAccess().registry(Registries.BIOME);
        if (registry.isEmpty())
            return super$getBiomes(category);
        registry.get().asLookup().listElements().forEach(
                biome_entry -> {
                    if (biome_entry.key().equals(category)) {
                        biomes.add(biome_entry.value());
                    }
                });
        return biomes;
    }
}
