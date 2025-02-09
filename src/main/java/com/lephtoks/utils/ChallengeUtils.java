package com.lephtoks.utils;

import com.lephtoks.TaintboundMod;
import com.lephtoks.challenges.ChallengeTypeE1;
import com.lephtoks.challenges.EntityKillChallengeType;
import com.lephtoks.challenges.ItemChallengeType;
import com.lephtoks.components.ChallengeComponent;
import com.lephtoks.components.TaintedEnchantmentsDataComponentTypes;
import com.lephtoks.enchantments.TaintedEnchantments;
import com.lephtoks.items.TaintedItems;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.fabricmc.loader.impl.lib.tinyremapper.extension.mixin.common.data.CommonData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.collection.WeightedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChallengeUtils {
    private static final Map<RegistryKey<Enchantment>, WeightedList<ChallengeComponent.Builder>> POOL = new HashMap<>();
    private static void add(RegistryKey<Enchantment> key, ChallengeComponent.Builder challenge, int weight) {
        POOL.computeIfAbsent(key, (k) -> new WeightedList<>()).add(challenge, weight);
    }
    static {
//        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.fromTag(ItemTags.OP)), 1, -1), 2);
//
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.DIAMOND)), 1, -1), 2);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.COBBLESTONE)), 4*64, -1), 4);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.EMERALD)), 4, -1), 2);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.OBSIDIAN)), 6, -1), 2);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.TNT)), 1.5f, -1), 1);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.fromTag(ConventionalItemTags.RAW_MATERIALS)), 11f, -1), 3);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(TaintedItems.POSITIVE_CRYSTAL)), 2, -1), 1);

        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.ROTTEN_FLESH)), 6f, -1), 3);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.ANVIL)), 0.3f, -1), 1);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.PUFFERFISH)), 0.4f, -1), 1);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new EntityKillChallengeType(EntityType.ZOMBIE), 7f, -1), 3);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new EntityKillChallengeType(EntityType.ENDERMAN), 1.45f, -1), 3);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new EntityKillChallengeType(EntityType.WARDEN), 0.1f, -1), 1);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new EntityKillChallengeType(EntityType.IRON_GOLEM), 0.4f, -1), 2);

        add(TaintedEnchantments.SWEEPING_EDGE, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.IRON_HOE)), 0.75f, -1), 2);
        add(TaintedEnchantments.SWEEPING_EDGE, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.GOLDEN_SWORD)), 0.75f, -1), 2);
        add(TaintedEnchantments.SWEEPING_EDGE, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.FEATHER)), 5f, -1), 4);
        add(TaintedEnchantments.SWEEPING_EDGE, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.PRISMARINE)), 3f, -1), 1);
        add(TaintedEnchantments.SWEEPING_EDGE, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.SUGAR)), 10f, -1), 4);
        add(TaintedEnchantments.SWEEPING_EDGE, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.WIND_CHARGE)), 1.5f, -1), 1);
    }
    public static ChallengeComponent.Builder getFor(RegistryEntry<Enchantment> enchantment) {
        var o = enchantment.getKey();
        if (o.isPresent()) {
            RegistryKey<Enchantment> key = o.get();
            if (POOL.containsKey(key)) {
                return POOL.get(key).shuffle().iterator().next();
            } else {
                TaintboundMod.LOGGER.error("Can not get challenge type for {} because it not set", enchantment.getType().name());
                return ChallengeComponent.Builder.EMPTY;
            }
        } else {
            TaintboundMod.LOGGER.error("Can not get challenge type for {}", enchantment.getType().name());
            return ChallengeComponent.Builder.EMPTY;
        }
    }

    public static <C extends ChallengeTypeE1<T>, T> boolean addForAllComponents(PlayerEntity player, Class<C> cClass, Supplier<T> createEntry, Function<T, Float> createRemain, BiConsumer<T, Float> onSuccess) {
        List<ItemStack> challenges = ChallengeComponent.getActiveComponents(player);
        if (!challenges.isEmpty()) {
            T entry = createEntry.get();
            float remain = createRemain.apply(entry);
            for (ItemStack challengeItem : challenges) {
                ChallengeComponent component = challengeItem.get(TaintedEnchantmentsDataComponentTypes.CHALLENGE);


                //noinspection DataFlowIssue
                if (cClass.isInstance(component.type())) {
                    C type = cClass.cast(component.type());
                    float before = component.current();
                    if (type.trigger(challengeItem, component, entry, remain)) {
                        if (challengeItem.contains(TaintedEnchantmentsDataComponentTypes.CHALLENGE)) {
                            ChallengeComponent component2 = challengeItem.get(TaintedEnchantmentsDataComponentTypes.CHALLENGE);
                            //noinspection DataFlowIssue
                            remain -= component2.current() - before;
                        } else {
                            remain -= component.max() - before;
                            break;
                        }
                    }
                }
            }
            onSuccess.accept(entry, remain);
            return true;
        }
        return false;
    }
    public static <C extends ChallengeTypeE1<T>, T> boolean addForAllComponents(PlayerEntity player, Class<C> cClass, Supplier<T> createEntry, Function<T, Float> createRemain) {
        List<ItemStack> challenges = ChallengeComponent.getActiveComponents(player);
        if (!challenges.isEmpty()) {
            T entry = createEntry.get();
            float remain = createRemain.apply(entry);
            for (ItemStack challengeItem : challenges) {
                ChallengeComponent component = challengeItem.get(TaintedEnchantmentsDataComponentTypes.CHALLENGE);


                //noinspection DataFlowIssue
                if (cClass.isInstance(component.type())) {
                    C type = cClass.cast(component.type());
                    float before = component.current();
                    if (type.trigger(challengeItem, component, entry, remain)) {
                        if (challengeItem.contains(TaintedEnchantmentsDataComponentTypes.CHALLENGE)) {
                            ChallengeComponent component2 = challengeItem.get(TaintedEnchantmentsDataComponentTypes.CHALLENGE);
                            //noinspection DataFlowIssue
                            remain -= component2.current() - before;
                        } else {
                            remain -= component.max() - before;
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
