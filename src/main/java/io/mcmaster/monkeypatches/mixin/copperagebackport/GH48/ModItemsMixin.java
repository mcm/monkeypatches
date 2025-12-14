package io.mcmaster.monkeypatches.mixin.copperagebackport.GH48;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes copper armor having infinite durability by adding explicit durability
 * values during item registration.
 * 
 * GitHub PR: https://github.com/Smallinger/Copper-Age-Backport/pull/54
 * GitHub Issue: https://github.com/Smallinger/Copper-Age-Backport/issues/48
 * 
 * The issue occurs in version 1.21.1 where copper armor items (helmet,
 * chestplate, leggings, boots) are registered without calling .durability() on
 * their Item.Properties. This causes the armor to never break when damaged.
 * 
 * The fix adds durability values calculated using
 * ArmorItem.Type.getDurability(11), where 11 is the durability multiplier for
 * copper armor material.
 * 
 * Note: This patch only applies to Copper Age Backport version 0.1.4 and
 * earlier, as PR #54 was merged and will be included in future releases.
 */
@Restriction(require = @Condition(value = "copperagebackport", versionPredicates = "[,0.1.4]"))
@Mixin(targets = "com.github.smallinger.copperagebackport.registry.ModItems", remap = false)
public class ModItemsMixin {

    /**
     * Redirects the stacksTo() call for copper helmet registration to add
     * durability.
     * 
     * This targets the specific lambda method (lambda$register$74) that creates the
     * copper helmet item. By redirecting the stacksTo() call, we can chain the
     * durability() method onto the properties before they're passed to the
     * ArmorItem
     * constructor.
     * 
     * Note: This patch cannot be disabled via configuration as mixins are applied
     * during early class loading, before the mod configuration system is
     * initialized.
     */
    @Redirect(method = "lambda$register$74", at = @At(value = "INVOKE", target = "net/minecraft/world/item/Item$Properties.stacksTo(I)Lnet/minecraft/world/item/Item$Properties;"), remap = false)
    private static Item.Properties setCopperHelmetDurability(Item.Properties props, int pMaxStackSize) {
        return props.stacksTo(pMaxStackSize).durability(ArmorItem.Type.HELMET.getDurability(11));
    }

    /**
     * Redirects the stacksTo() call for copper chestplate registration to add
     * durability.
     * 
     * Targets lambda$register$75 which creates the copper chestplate.
     */
    @Redirect(method = "lambda$register$75", at = @At(value = "INVOKE", target = "net/minecraft/world/item/Item$Properties.stacksTo(I)Lnet/minecraft/world/item/Item$Properties;"), remap = false)
    private static Item.Properties setCopperChestplateDurability(Item.Properties props, int pMaxStackSize) {
        return props.stacksTo(pMaxStackSize).durability(ArmorItem.Type.CHESTPLATE.getDurability(11));
    }

    /**
     * Redirects the stacksTo() call for copper leggings registration to add
     * durability.
     * 
     * Targets lambda$register$76 which creates the copper leggings.
     */
    @Redirect(method = "lambda$register$76", at = @At(value = "INVOKE", target = "net/minecraft/world/item/Item$Properties.stacksTo(I)Lnet/minecraft/world/item/Item$Properties;"), remap = false)
    private static Item.Properties setCopperLeggingsDurability(Item.Properties props, int pMaxStackSize) {
        return props.stacksTo(pMaxStackSize).durability(ArmorItem.Type.LEGGINGS.getDurability(11));
    }

    /**
     * Redirects the stacksTo() call for copper boots registration to add
     * durability.
     * 
     * Targets lambda$register$77 which creates the copper boots.
     */
    @Redirect(method = "lambda$register$77", at = @At(value = "INVOKE", target = "net/minecraft/world/item/Item$Properties.stacksTo(I)Lnet/minecraft/world/item/Item$Properties;"), remap = false)
    private static Item.Properties setCopperBootsDurability(Item.Properties props, int pMaxStackSize) {
        return props.stacksTo(pMaxStackSize).durability(ArmorItem.Type.BOOTS.getDurability(11));
    }
}
