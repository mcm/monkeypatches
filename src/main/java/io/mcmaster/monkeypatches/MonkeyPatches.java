package io.mcmaster.monkeypatches;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.mcmaster.monkeypatches.fixes.createstuffadditions.fluid_capability.FluidCapability;
import net.mcreator.createstuffadditions.init.CreateSaModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(MonkeyPatches.MODID)
public class MonkeyPatches {
    public static final String MODID = "monkeypatches";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MonkeyPatches(IEventBus modEventBus, ModContainer modContainer) {
        // modEventBus.addListener(this::commonSetup);
        // modEventBus.addListener(this::registerCapabilities);
        modEventBus.register(this);

        // NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    private void commonSetup(FMLCommonSetupEvent event) {
        io.mcmaster.monkeypatches.util.PatchInfo.logPatchStatus();
    }

    @SubscribeEvent
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        /**
         * Register Create Stuff & Additions fluid handler capabilities
         * 
         * Checks whether Create Stuff 'N Additions CF59 capabilities should be
         * applied.
         */

        if (ModList.get().isLoaded("create_sa") && Config.PatchConfig.isCreateStuffAdditionsCF59Enabled()) {
            // CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE
            FluidCapability.registerCapability(event, CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE.get());
            // CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE
            FluidCapability.registerCapability(event, CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE.get());
            // CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE
            FluidCapability.registerCapability(event, CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE.get());
            // CreateSaModItems.COPPER_JETPACK_CHESTPLATE
            FluidCapability.registerCapability(event, CreateSaModItems.COPPER_JETPACK_CHESTPLATE.get());
            // CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE
            FluidCapability.registerCapability(event, CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE.get());
            // CreateSaModItems.BRASS_JETPACK_CHESTPLATE
            FluidCapability.registerCapability(event, CreateSaModItems.BRASS_JETPACK_CHESTPLATE.get());
            // CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE
            FluidCapability.registerCapability(event, CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE.get());
            // CreateSaModItems.SMALL_FILLING_TANK
            FluidCapability.registerCapability(event, CreateSaModItems.SMALL_FILLING_TANK.get());
            // CreateSaModItems.SMALL_FUELING_TANK
            FluidCapability.registerCapability(event, CreateSaModItems.SMALL_FUELING_TANK.get());
            // CreateSaModItems.MEDIUM_FILLING_TANK
            FluidCapability.registerCapability(event, CreateSaModItems.MEDIUM_FILLING_TANK.get());
            // CreateSaModItems.MEDIUM_FUELING_TANK
            FluidCapability.registerCapability(event, CreateSaModItems.MEDIUM_FUELING_TANK.get());
            // CreateSaModItems.LARGE_FILLING_TANK
            FluidCapability.registerCapability(event, CreateSaModItems.LARGE_FILLING_TANK.get());
            // CreateSaModItems.LARGE_FUELING_TANK
            FluidCapability.registerCapability(event, CreateSaModItems.LARGE_FUELING_TANK.get());
            // CreateSaModItems.BLOCK_PICKER
            FluidCapability.registerCapability(event, CreateSaModItems.BLOCK_PICKER.get());
            // CreateSaModItems.FLAMETHROWER
            FluidCapability.registerCapability(event, CreateSaModItems.FLAMETHROWER.get());
            // CreateSaModItems.GRAPPLIN_WHISK
            FluidCapability.registerCapability(event, CreateSaModItems.GRAPPLIN_WHISK.get());
            // CreateSaModItems.PORTABLE_DRILL
            FluidCapability.registerCapability(event, CreateSaModItems.PORTABLE_DRILL.get());
        }
    }
}
