package io.mcmaster.monkeypatches.fixes.createstuffadditions.fluid_capability;

import io.mcmaster.monkeypatches.util.MixinConditions;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.mcreator.createstuffadditions.init.CreateSaModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

/**
 * Adds IFluidHandlerItem capability to the Andesite Jetpack from Create Stuff &
 * Additions.
 * 
 * This mixin enhances the jetpack's fuel system by exposing it through the
 * standard NeoForge
 * fluid capability API, allowing other mods to interact with the jetpack's fuel
 * system.
 * 
 * The jetpack stores fuel as a double value in NBT under the "tagFuel" key.
 * This implementation
 * converts between the NBT fuel system and FluidStack for lava (the jetpack's
 * fuel type).
 * 
 * Implementation details:
 * - Uses lava as the fluid type (jetpack accepts lava buckets)
 * - Converts fuel units to/from mB (1 fuel unit = 250 mB of lava)
 * - Respects the jetpack's capacity limits from CreateSaModVariables
 */

/**
 * Custom fluid handler implementation for the Andesite Jetpack.
 * 
 * This handler bridges between the jetpack's NBT-based fuel system and the
 * standard NeoForge fluid API. It treats the jetpack as a single-tank fluid
 * container that can only hold lava.
 */

public class FluidCapability implements IFluidHandlerItem {
    private final ItemStack container;

    public FluidCapability(ItemStack container) {
        this.container = container;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 2;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank < 0 || tank >= 2)
            return FluidStack.EMPTY;

        double amount = getAmount((tank));

        if (amount <= 0)
            return FluidStack.EMPTY;

        // Convert fuel units to mB of lava
        int fluidAmount = (int) (amount * 10);
        return new FluidStack(tank == 0 ? Fluids.WATER : Fluids.LAVA, fluidAmount);
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank < 0 || tank >= 2)
            return 0;
        if ((tank == 0 && !isWaterValid()) || (tank == 1 && !isFuelValid()))
            return 0;

        if (isJetpack())
            return 16000;
        if ((isSmallTank()))
            return 8000;
        if ((isMediumTank()))
            return 16000;
        if ((isLargeTank()))
            return 32000;

        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank == 0 && stack.getFluid() == Fluids.LAVA;
    }

    private boolean isJetpack() {
        return (container.is(CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.COPPER_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE));
    }

    private boolean isTank() {
        return (container.is(CreateSaModItems.SMALL_FILLING_TANK) ||
                container.is(CreateSaModItems.SMALL_FUELING_TANK) ||
                container.is(CreateSaModItems.MEDIUM_FILLING_TANK) ||
                container.is(CreateSaModItems.MEDIUM_FUELING_TANK) ||
                container.is(CreateSaModItems.LARGE_FILLING_TANK) ||
                container.is(CreateSaModItems.LARGE_FUELING_TANK));
    }

    private boolean isSmallTank() {
        return (container.is(CreateSaModItems.SMALL_FILLING_TANK) ||
                container.is(CreateSaModItems.SMALL_FUELING_TANK));
    }

    private boolean isMediumTank() {
        return (container.is(CreateSaModItems.MEDIUM_FILLING_TANK) ||
                container.is(CreateSaModItems.MEDIUM_FUELING_TANK));
    }

    private boolean isLargeTank() {
        return (container.is(CreateSaModItems.LARGE_FILLING_TANK) ||
                container.is(CreateSaModItems.LARGE_FUELING_TANK));
    }

    private boolean isFuelValid() {
        return (container.is(CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.SMALL_FUELING_TANK) ||
                container.is(CreateSaModItems.MEDIUM_FUELING_TANK) ||
                container.is(CreateSaModItems.LARGE_FUELING_TANK));
    }

    private boolean isWaterValid() {
        return (container.is(CreateSaModItems.COPPER_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.SMALL_FILLING_TANK) ||
                container.is(CreateSaModItems.MEDIUM_FILLING_TANK) ||
                container.is(CreateSaModItems.LARGE_FILLING_TANK));
    }

    @Override
    public int fill(@Nonnull FluidStack resource, @Nonnull IFluidHandler.FluidAction action) {
        // if (resource.isEmpty() || (resource.getFluid() != Fluids.LAVA &&
        // resource.getFluid() != Fluids.WATER)) {
        if (resource.isEmpty())
            return 0;

        int tank;
        if (resource.getFluid() == Fluids.WATER) {
            if (!isWaterValid())
                return 0;

            tank = 0;
        } else if (resource.getFluid() == Fluids.LAVA) {
            if (!isFuelValid())
                return 0;

            tank = 1;
        } else {
            return 0;
        }

        int current = (int) getAmount(tank) * 10;
        int capacity = getTankCapacity(tank);
        int filled = Math.min(resource.getAmount(), capacity - current);

        if (action == IFluidHandler.FluidAction.EXECUTE) {
            setAmount(tank, (double) (current + filled) / 10.0);
        }

        return filled;
    }

    public @NotNull FluidStack drain(int tank, int maxDrain, @Nonnull IFluidHandler.FluidAction action) {
        if (tank < 0 || tank >= 2)
            return FluidStack.EMPTY;

        int current = (int) getAmount(tank) * 10;
        int drained = Math.min(current, maxDrain);

        if (action == IFluidHandler.FluidAction.EXECUTE) {
            setAmount(tank, (double) (current - drained) / 10.0);
        }

        return new FluidStack(tank == 0 ? Fluids.WATER : Fluids.LAVA, drained);
    }

    @Override
    public @NotNull FluidStack drain(@Nonnull FluidStack resource, @Nonnull IFluidHandler.FluidAction action) {
        int tank;

        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        } else if (resource.getFluid() == Fluids.WATER && isWaterValid()) {
            tank = 0;
        } else if (resource.getFluid() == Fluids.LAVA && isFuelValid()) {
            tank = 1;
        } else {
            return FluidStack.EMPTY;
        }

        return drain(tank, resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @Nonnull IFluidHandler.FluidAction action) {
        if (maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        int tank;
        if (isWaterValid() && getAmount(0) > 0) {
            tank = 0;
        } else if (isFuelValid() && getAmount(1) > 0) {
            tank = 1;
        } else {
            return FluidStack.EMPTY;
        }

        return drain(tank, maxDrain, action);
    }

    /**
     * Gets the current fluid amount from the NBT data.
     */
    private double getAmount(int tank) {
        String tagName;

        if (tank < 0 || tank >= 2)
            return 0.0;
        if (!isJetpack() && !isTank())
            return 0.0;

        if (tank == 0 && isWaterValid()) {
            tagName = isJetpack() ? "tagWater" : "tagStock";
        } else if (tank == 1 && isFuelValid()) {
            tagName = isJetpack() ? "tagFuel" : "tagStock";
        } else {
            return 0.0;
        }

        return container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getDouble(tagName);
    }

    /**
     * Sets the fluid amount in the NBT data.
     */
    private void setAmount(int tank, double amount) {
        String tagName;

        if (tank < 0 || tank >= 2)
            return;
        if (!isJetpack() && !isTank())
            return;

        if (tank == 0 && isWaterValid()) {
            tagName = isJetpack() ? "tagWater" : "tagStock";
        } else if (tank == 1 && isFuelValid()) {
            tagName = isJetpack() ? "tagFuel" : "tagStock";
        } else {
            return;
        }

        CustomData.update(DataComponents.CUSTOM_DATA, container, tag -> {
            tag.putDouble(tagName, Math.max(0.0, Math.min(amount, getTankCapacity(tank))));
        });
    }

    public static void registerCapability(RegisterCapabilitiesEvent event, Item item) {
        if (!event.isItemRegistered(Capabilities.FluidHandler.ITEM, item)) {
            event.registerItem(
                    Capabilities.FluidHandler.ITEM,
                    (stack, context) -> new FluidCapability(stack),
                    item);
        }
    }
}

/**
 * Registers the fluid capability for the Andesite Jetpack.
 * This method should be called during capability registration.
 */
// public static void registerCapability(RegisterCapabilitiesEvent event) {
// // Check if the patch is enabled, if not, skip registration
// if (!MixinConditions.shouldApplyCSAAndesiteJetpackFluidCapability()) {
// return;
// }

// // Register the fluid handler capability for the andesite jetpack chestplate
// try {
// // Get the item from Create Stuff & Additions
// Class<?> jetpackItemClass =
// Class.forName("net.mcreator.createstuffadditions.init.CreateSaModItems");
// Object andesiteJetpackChestplate =
// jetpackItemClass.getField("ANDESITE_JETPACK_CHESTPLATE").get(null);

// if (andesiteJetpackChestplate instanceof
// net.neoforged.neoforge.registries.DeferredHolder) {
// @SuppressWarnings("rawtypes")
// net.neoforged.neoforge.registries.DeferredHolder holder =
// (net.neoforged.neoforge.registries.DeferredHolder) andesiteJetpackChestplate;

// Object item = holder.get();
// if (item instanceof net.minecraft.world.item.Item) {
// event.registerItem(
// Capabilities.FluidHandler.ITEM,
// (stack, context) -> new JetpackFluidHandler(stack),
// (net.minecraft.world.item.Item) item);
// }
// }
// } catch (Exception e) {
// // Log the error but don't crash - this is a compatibility feature
// System.err.println("Failed to register Andesite Jetpack fluid capability: " +
// e.getMessage());
// }
// }