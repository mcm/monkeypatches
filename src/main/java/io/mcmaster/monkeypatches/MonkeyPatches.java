package io.mcmaster.monkeypatches;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.mcmaster.monkeypatches.fixes.createstuffadditions.fluid_capability.FluidCapability;
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
            FluidCapability.registerAll(event);
        }
    }
}
