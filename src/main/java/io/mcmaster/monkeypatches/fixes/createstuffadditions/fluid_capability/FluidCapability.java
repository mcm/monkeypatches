package io.mcmaster.monkeypatches.fixes.createstuffadditions.fluid_capability;

import net.mcreator.createstuffadditions.configuration.CreateSaConfigConfiguration;
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

import javax.annotation.Nonnull;

/**
 * Adds IFluidHandlerItem capability to items from Create Stuff 'N Additions.
 * 
 * This enhances the fuel/water system by exposing it through the standard
 * NeoForge fluid capability API, allowing other mods to interact with the
 * fuel system.
 * 
 * Gadgets store fuel in custom data under the `tagFuel` key, and water under
 * the `tagWater` key. Tanks store their respective fluid under the `tagStock`
 * key. This implementation converts between the NBT fuel system and FluidStack
 * with the same conversion rate used when filling by natively supported means.
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

        if (isGadget())
            return (int) (CreateSaConfigConfiguration.GADGETCAPACITY.get() * 10);
        if ((isSmallTank()))
            return (int) (CreateSaConfigConfiguration.SMALLTANKCAPACITY.get() * 10);
        if ((isMediumTank()))
            return (int) (CreateSaConfigConfiguration.MEDIUMTANKCAPACITY.get() * 10);
        if ((isLargeTank()))
            return (int) (CreateSaConfigConfiguration.LARGETANKCAPACITY.get() * 10);

        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank == 0 && stack.getFluid() == Fluids.LAVA;
    }

    private boolean isGadget() {
        return (container.is(CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE) ||
                container.is(CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE) ||
                container.is(CreateSaModItems.COPPER_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.BLOCK_PICKER) ||
                container.is(CreateSaModItems.FLAMETHROWER) ||
                container.is(CreateSaModItems.GRAPPLIN_WHISK) ||
                container.is(CreateSaModItems.PORTABLE_DRILL));
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
        return (container.is(CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE) ||
                container.is(CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.SMALL_FUELING_TANK) ||
                container.is(CreateSaModItems.MEDIUM_FUELING_TANK) ||
                container.is(CreateSaModItems.LARGE_FUELING_TANK) ||
                container.is(CreateSaModItems.FLAMETHROWER) ||
                container.is(CreateSaModItems.GRAPPLIN_WHISK) ||
                container.is(CreateSaModItems.PORTABLE_DRILL));
    }

    private boolean isWaterValid() {
        return (container.is(CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE) ||
                container.is(CreateSaModItems.COPPER_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE) ||
                container.is(CreateSaModItems.BRASS_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE) ||
                container.is(CreateSaModItems.SMALL_FILLING_TANK) ||
                container.is(CreateSaModItems.MEDIUM_FILLING_TANK) ||
                container.is(CreateSaModItems.LARGE_FILLING_TANK) ||
                container.is(CreateSaModItems.BLOCK_PICKER) ||
                container.is(CreateSaModItems.PORTABLE_DRILL));
    }

    @Override
    public int fill(@Nonnull FluidStack resource, @Nonnull IFluidHandler.FluidAction action) {
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
        if (!isGadget() && !isTank())
            return 0.0;

        if (tank == 0 && isWaterValid()) {
            tagName = isGadget() ? "tagWater" : "tagStock";
        } else if (tank == 1 && isFuelValid()) {
            tagName = isGadget() ? "tagFuel" : "tagStock";
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
        if (!isGadget() && !isTank())
            return;

        if (tank == 0 && isWaterValid()) {
            tagName = isGadget() ? "tagWater" : "tagStock";
        } else if (tank == 1 && isFuelValid()) {
            tagName = isGadget() ? "tagFuel" : "tagStock";
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

    public static void registerAll(RegisterCapabilitiesEvent event) {
        // CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE
        registerCapability(event, CreateSaModItems.ANDESITE_EXOSKELETON_CHESTPLATE.get());
        // CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE
        registerCapability(event, CreateSaModItems.ANDESITE_JETPACK_CHESTPLATE.get());
        // CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE
        registerCapability(event, CreateSaModItems.COPPER_EXOSKELETON_CHESTPLATE.get());
        // CreateSaModItems.COPPER_JETPACK_CHESTPLATE
        registerCapability(event, CreateSaModItems.COPPER_JETPACK_CHESTPLATE.get());
        // CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE
        registerCapability(event, CreateSaModItems.BRASS_EXOSKELETON_CHESTPLATE.get());
        // CreateSaModItems.BRASS_JETPACK_CHESTPLATE
        registerCapability(event, CreateSaModItems.BRASS_JETPACK_CHESTPLATE.get());
        // CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE
        registerCapability(event, CreateSaModItems.NETHERITE_JETPACK_CHESTPLATE.get());
        // CreateSaModItems.SMALL_FILLING_TANK
        registerCapability(event, CreateSaModItems.SMALL_FILLING_TANK.get());
        // CreateSaModItems.SMALL_FUELING_TANK
        registerCapability(event, CreateSaModItems.SMALL_FUELING_TANK.get());
        // CreateSaModItems.MEDIUM_FILLING_TANK
        registerCapability(event, CreateSaModItems.MEDIUM_FILLING_TANK.get());
        // CreateSaModItems.MEDIUM_FUELING_TANK
        registerCapability(event, CreateSaModItems.MEDIUM_FUELING_TANK.get());
        // CreateSaModItems.LARGE_FILLING_TANK
        registerCapability(event, CreateSaModItems.LARGE_FILLING_TANK.get());
        // CreateSaModItems.LARGE_FUELING_TANK
        registerCapability(event, CreateSaModItems.LARGE_FUELING_TANK.get());
        // CreateSaModItems.BLOCK_PICKER
        registerCapability(event, CreateSaModItems.BLOCK_PICKER.get());
        // CreateSaModItems.FLAMETHROWER
        registerCapability(event, CreateSaModItems.FLAMETHROWER.get());
        // CreateSaModItems.GRAPPLIN_WHISK
        registerCapability(event, CreateSaModItems.GRAPPLIN_WHISK.get());
        // CreateSaModItems.PORTABLE_DRILL
        registerCapability(event, CreateSaModItems.PORTABLE_DRILL.get());
    }
}