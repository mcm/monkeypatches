package io.mcmaster.monkeypatches.mixin.farmersdelight.tag_load_order;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes a class-init load-order race in Farmers Delight 1.3.1's ModTags.
 *
 * GitHub Issue (downstream symptom): https://github.com/MehdiNoui/FungiDelight/issues/8
 * Upstream fix (unreleased as of FD 1.3.1):
 * https://github.com/vectorwing/FarmersDelight/commit/5f84f22
 * FD changelog (1.3.2): "(1.21+) Fixed occasional crash with a tag reference load order"
 *
 * In FD 1.3.1, the deprecated top-level tag references in ModTags are declared
 * as references to the inner ModTags$Blocks / ModTags$Items / ModTags$EntityTypes
 * static fields, e.g.:
 *
 *     public static final TagKey&lt;Block&gt; MUSHROOM_COLONY_GROWABLE_ON =
 *         Blocks.MUSHROOM_COLONY_GROWABLE_ON;
 *
 * If the first access path goes through an inner-class field, the JVM begins
 * initializing the inner class. Its assignments call ModTags.modBlockTag(...),
 * which forces ModTags's outer &lt;clinit&gt; to run. The outer &lt;clinit&gt; then
 * re-references Blocks.X — but Blocks is already mid-initialization on the
 * same thread. Per JLS &#167;12.4.2, that recursive request returns immediately
 * with the field's current (null) value, which is permanently captured into
 * the static-final outer field.
 *
 * Symptom: at random tick, BlockState.is(ModTags.X) passes a null TagKey into
 * ImmutableSet.contains, which throws NullPointerException via Objects.requireNonNull.
 *
 * Fix: at the end of ModTags.&lt;clinit&gt;, walk every deprecated outer field and,
 * if any captured null, reconstruct the matching TagKey. By the time &lt;clinit&gt;
 * returns, the inner classes are fully initialized, so we can safely produce
 * equivalent TagKeys for any field that was lost to the race.
 *
 * Note: This patch cannot be disabled via configuration — the injected code
 * runs during ModTags's static initializer, which is invoked during early class
 * loading before the mod configuration system is available.
 */
@Restriction(require = @Condition(value = "farmersdelight", versionPredicates = "[1.3.1,1.3.2)"))
@Mixin(targets = "vectorwing.farmersdelight.common.tag.ModTags", remap = false)
public class ModTagsMixin {
    private static final String FD_NAMESPACE = "farmersdelight";

    @Mutable @Shadow public static TagKey<Block> CAMPFIRE_SIGNAL_SMOKE;
    @Mutable @Shadow public static TagKey<Block> COMPOST_ACTIVATORS;
    @Mutable @Shadow public static TagKey<Block> DROPS_CAKE_SLICE;
    @Mutable @Shadow public static TagKey<Block> HEAT_CONDUCTORS;
    @Mutable @Shadow public static TagKey<Block> HEAT_SOURCES;
    @Mutable @Shadow public static TagKey<Block> TRAY_HEAT_SOURCES;
    @Mutable @Shadow public static TagKey<Block> MUSHROOM_COLONY_GROWABLE_ON;
    @Mutable @Shadow public static TagKey<Block> MINEABLE_WITH_KNIFE;
    @Mutable @Shadow public static TagKey<Block> TERRAIN;
    @Mutable @Shadow public static TagKey<Block> STRAW_BLOCKS;
    @Mutable @Shadow public static TagKey<Block> WILD_CROPS;
    @Mutable @Shadow public static TagKey<Block> CABINETS;
    @Mutable @Shadow public static TagKey<Block> WOODEN_CABINETS;
    @Mutable @Shadow public static TagKey<Block> MUSHROOM_COLONIES;
    @Mutable @Shadow public static TagKey<Block> ROPES;
    @Mutable @Shadow public static TagKey<Block> UNAFFECTED_BY_RICH_SOIL;

    @Mutable @Shadow public static TagKey<Item> MEALS;
    @Mutable @Shadow public static TagKey<Item> DRINKS;
    @Mutable @Shadow public static TagKey<Item> FEASTS;
    @Mutable @Shadow public static TagKey<Item> WILD_CROPS_ITEM;
    @Mutable @Shadow public static TagKey<Item> STRAW_HARVESTERS;
    @Mutable @Shadow public static TagKey<Item> KNIVES;
    @Mutable @Shadow public static TagKey<Item> CANVAS_SIGNS;
    @Mutable @Shadow public static TagKey<Item> HANGING_CANVAS_SIGNS;
    @Mutable @Shadow public static TagKey<Item> WOODEN_CABINET_ITEMS;
    @Mutable @Shadow public static TagKey<Item> CABINET_ITEMS;
    @Mutable @Shadow public static TagKey<Item> MUSHROOM_COLONY_ITEMS;
    @Mutable @Shadow public static TagKey<Item> SERVING_CONTAINERS;
    @Mutable @Shadow public static TagKey<Item> FLAT_ON_CUTTING_BOARD;

    @Mutable @Shadow public static TagKey<EntityType<?>> DOG_FOOD_USERS;
    @Mutable @Shadow public static TagKey<EntityType<?>> HORSE_FEED_USERS;
    @Mutable @Shadow public static TagKey<EntityType<?>> HORSE_FEED_TEMPTED;

    @Inject(method = "<clinit>", at = @At("RETURN"), remap = false)
    private static void monkeypatches$repairTagLoadOrder(CallbackInfo ci) {
        if (CAMPFIRE_SIGNAL_SMOKE == null) CAMPFIRE_SIGNAL_SMOKE = blockTag("campfire_signal_smoke");
        if (COMPOST_ACTIVATORS == null) COMPOST_ACTIVATORS = blockTag("compost_activators");
        if (DROPS_CAKE_SLICE == null) DROPS_CAKE_SLICE = blockTag("drops_cake_slice");
        if (HEAT_CONDUCTORS == null) HEAT_CONDUCTORS = blockTag("heat_conductors");
        if (HEAT_SOURCES == null) HEAT_SOURCES = blockTag("heat_sources");
        if (TRAY_HEAT_SOURCES == null) TRAY_HEAT_SOURCES = blockTag("tray_heat_sources");
        if (MUSHROOM_COLONY_GROWABLE_ON == null) MUSHROOM_COLONY_GROWABLE_ON = blockTag("mushroom_colony_growable_on");
        if (MINEABLE_WITH_KNIFE == null) MINEABLE_WITH_KNIFE = blockTag("mineable/knife");
        if (TERRAIN == null) TERRAIN = blockTag("terrain");
        if (STRAW_BLOCKS == null) STRAW_BLOCKS = blockTag("straw_blocks");
        if (WILD_CROPS == null) WILD_CROPS = blockTag("wild_crops");
        if (CABINETS == null) CABINETS = blockTag("cabinets");
        if (WOODEN_CABINETS == null) WOODEN_CABINETS = blockTag("cabinets/wooden");
        if (MUSHROOM_COLONIES == null) MUSHROOM_COLONIES = blockTag("mushroom_colonies");
        if (ROPES == null) ROPES = blockTag("ropes");
        if (UNAFFECTED_BY_RICH_SOIL == null) UNAFFECTED_BY_RICH_SOIL = blockTag("unaffected_by_rich_soil");

        if (MEALS == null) MEALS = itemTag("meals");
        if (DRINKS == null) DRINKS = itemTag("drinks");
        if (FEASTS == null) FEASTS = itemTag("feasts");
        if (WILD_CROPS_ITEM == null) WILD_CROPS_ITEM = itemTag("wild_crops");
        if (STRAW_HARVESTERS == null) STRAW_HARVESTERS = itemTag("straw_harvesters");
        if (KNIVES == null) KNIVES = itemTag("tools/knives");
        if (CANVAS_SIGNS == null) CANVAS_SIGNS = itemTag("canvas_signs");
        if (HANGING_CANVAS_SIGNS == null) HANGING_CANVAS_SIGNS = itemTag("hanging_canvas_signs");
        if (WOODEN_CABINET_ITEMS == null) WOODEN_CABINET_ITEMS = itemTag("cabinets/wooden");
        if (CABINET_ITEMS == null) CABINET_ITEMS = itemTag("cabinets");
        if (MUSHROOM_COLONY_ITEMS == null) MUSHROOM_COLONY_ITEMS = itemTag("mushroom_colonies");
        if (SERVING_CONTAINERS == null) SERVING_CONTAINERS = itemTag("serving_containers");
        if (FLAT_ON_CUTTING_BOARD == null) FLAT_ON_CUTTING_BOARD = itemTag("flat_on_cutting_board");

        if (DOG_FOOD_USERS == null) DOG_FOOD_USERS = entityTag("dog_food_users");
        if (HORSE_FEED_USERS == null) HORSE_FEED_USERS = entityTag("horse_feed_users");
        if (HORSE_FEED_TEMPTED == null) HORSE_FEED_TEMPTED = entityTag("horse_feed_tempted");
    }

    private static TagKey<Block> blockTag(String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(FD_NAMESPACE, path));
    }

    private static TagKey<Item> itemTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(FD_NAMESPACE, path));
    }

    private static TagKey<EntityType<?>> entityTag(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FD_NAMESPACE, path));
    }
}
