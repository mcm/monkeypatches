package io.mcmaster.monkeypatches;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(MonkeyPatches.MODID)
public class MonkeyPatches {
    public static final String MODID = "monkeypatches";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MonkeyPatches(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        io.mcmaster.monkeypatches.util.PatchInfo.logPatchStatus();
    }
}
