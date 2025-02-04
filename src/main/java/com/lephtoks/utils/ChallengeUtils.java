package com.lephtoks.utils;

import com.lephtoks.TaintboundMod;
import com.lephtoks.challenges.ChallengeTypeE1;
import com.lephtoks.challenges.EntityKillChallengeType;
import com.lephtoks.challenges.ItemChallengeType;
import com.lephtoks.components.ChallengeComponent;
import com.lephtoks.components.TaintedEnchantmentsDataComponentTypes;
import com.lephtoks.enchantments.TaintedEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
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
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.COBBLESTONE)), 4*64, -1), 1);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.EMERALD)), 5, -1), 1);
        add(TaintedEnchantments.EFFICIENCY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.OBSIDIAN)), 6, -1), 1);

        add(TaintedEnchantments.INFINITY, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.TOTEM_OF_UNDYING)), 0.25f, -1), 1);

        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.ROTTEN_FLESH)), 12f, -1), 3);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new EntityKillChallengeType(EntityType.ZOMBIE), 7f, -1), 3);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new EntityKillChallengeType(EntityType.WARDEN), 0.1f, -1), 1);
        add(TaintedEnchantments.SHARPNESS, new ChallengeComponent.Builder(new EntityKillChallengeType(EntityType.IRON_GOLEM), 1f, -1), 2);

        add(TaintedEnchantments.SWEEPING_EDGE, new ChallengeComponent.Builder(new ItemChallengeType(Ingredient.ofItems(Items.IRON_HOE)), 2f, -1), 1);
    }
    public static ChallengeComponent.Builder getFor(RegistryEntry<Enchantment> enchantment) {
        var o = enchantment.getKey();
        if (o.isPresent()) {
            RegistryKey<Enchantment> key = o.get();
            if (POOL.containsKey(key)) {
                return POOL.get(key).shuffle().shuffle().iterator().next();
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
