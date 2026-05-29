package com.github.no_name_provided.potion_fruit.common.recipes;

import com.github.no_name_provided.potion_fruit.Config;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class InfuseFruit extends CustomRecipe {
    
    private final Ingredient fruit;
    
    public InfuseFruit(Ingredient fruit) {
        this.fruit = fruit;
    }
    
    /**
     * Does the recipe input match our recipe? Should we handle this craft?
     */
    @Override @ParametersAreNonnullByDefault
    public boolean matches(CraftingInput cInput, Level level) {
        
        if (cInput.size() != 2) {
            
            return false;
        }
        
        ArrayList<ItemStack> orderedInputs = new ArrayList<>(List.of(
                ItemStack.EMPTY,
                ItemStack.EMPTY
        ));
        
        for (ItemStack input : cInput.items()) {
            if (fruit.test(input)) {
                orderedInputs.set(0, input);
            } else if (input.getItem() instanceof PotionItem) {
                orderedInputs.set(1, input);
            }
        }
        
        return !(orderedInputs.getFirst().isEmpty() || orderedInputs.getLast().isEmpty());
    }
    
    /**
     * Return actual output item - which may be a copy of the result.
     */
    @Override @ParametersAreNonnullByDefault
    public @NonNull ItemStack assemble(CraftingInput cInput) {
        ItemStack potion = cInput.items().stream().filter(input -> !fruit.test(input))
                .toList().getFirst().copyWithCount(1);
        ItemStack infusedFruit = cInput.items().stream().filter(fruit)
                .toList().getFirst().copyWithCount(1);
        
        PotionContents contents = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        Effects effects = new Effects();
        contents.getAllEffects().forEach(effects::add);
        Consumable oldConsumable = infusedFruit.getOrDefault(DataComponents.CONSUMABLE, Consumables.defaultFood().build());
        Consumable.Builder newConsumableBuilder = Consumable.builder()
                .consumeSeconds(oldConsumable.consumeSeconds())
                .hasConsumeParticles(oldConsumable.hasConsumeParticles())
                .animation(oldConsumable.animation())
                .soundAfterConsume(oldConsumable.sound())
                .onConsume(effects.get());
        // This method adds effects (rather than overwriting them, as is usually the case with builders in this codebase)
        oldConsumable.onConsumeEffects().forEach(consumeEffect -> {
                    if (consumeEffect instanceof ApplyStatusEffectsConsumeEffect mobEffect) {
                        // Running these through our subclass removes duplicates and adds tooltips
                        effects.addAll(mobEffect.effects());
                    } else {
                        // We may want to special case these later - for now, they quietly carry over
                        newConsumableBuilder.onConsume(consumeEffect);
                    }
                }
        );
        infusedFruit.set(DataComponents.CONSUMABLE, newConsumableBuilder.build());
        
        FoodProperties oldProps = infusedFruit.getOrDefault(DataComponents.FOOD, new FoodProperties(
                        0,
                        0,
                        true
                )
        );
        FoodProperties.Builder newPropsBuilder = new FoodProperties.Builder()
                .nutrition(oldProps.nutrition())
                .saturationModifier(oldProps.saturation())
                .alwaysEdible();
        infusedFruit.set(DataComponents.FOOD, newPropsBuilder.build());
        
        if (Config.addGlint) {
            infusedFruit.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        if (Config.addLore) {
            List<Component> lore = new ArrayList<>();
            // In vanilla, ticks per second appears to target a hardcoded value in an interface. This happens to be
            // the same as the shared TICKS_PER_SECOND constant, so I'm using that. It does not seem to reference
            // the actual tick rate, which can be changed from this default. Doing this without a level reference would
            // require siloed code.
            PotionContents.addPotionTooltip(effects.getRaw(), lore::add, 1.0f, TICKS_PER_SECOND);
            infusedFruit.set(DataComponents.LORE, new ItemLore(lore));
        }
        
        return infusedFruit;
    }
    
    /**
     * This class exists to wrap the #add method in ArrayList and filter out redundant effects.
     */
    private static class Effects {
        private final ArrayList<MobEffectInstance> effects = new ArrayList<>();
        
        /**
         * Checks for duplicates and ensures only the dominant effect is added. Makes no difference mechanically, but
         * filters bad information out of tooltips.
         */
        public void add(MobEffectInstance newEffect) {
            // It's either this or dealing with a possible null value in the TagKey. That feels less clean.
            @SuppressWarnings("deprecation")
            List<MobEffectInstance> oldEffects = effects.stream().filter(
                    effect -> effect.getEffect().is(newEffect.getEffect())
            ).toList();
            
            if (oldEffects.isEmpty()) {
                effects.add(newEffect);
            } else {
                // There should never be more than one effect of the same type...
                MobEffectInstance oldEffect = oldEffects.getFirst();
                
                if (oldEffect.getAmplifier() < newEffect.getAmplifier()) {
                    effects.replaceAll((effect) -> effect.equals(oldEffect) ? newEffect : effect);
                } else if (oldEffect.getAmplifier() == newEffect.getAmplifier() && oldEffect.getDuration() < newEffect.getDuration()) {
                    effects.replaceAll((effect) -> effect.equals(oldEffect) ? newEffect : effect);
                }
            }
        }
        
        public void addAll(List<MobEffectInstance> newEffects) {
            newEffects.forEach(this::add);
        }
        
        public ArrayList<MobEffectInstance> getRaw() {
            
            return effects;
        }
        
        public ConsumeEffect get() {
            
            return new ApplyStatusEffectsConsumeEffect(effects);
        }
    }
    
    /**
     * The codec set to use for saving, reading, & streaming.
     */
    @Override @NotNull
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        
        return Registry.INFUSE_FRUIT_SERIALIZER.get();
    }
    
    /**
     * Not necessary, but makes the serializer less finicky.
     */
    public Ingredient getFruit() {
        
        return fruit;
    }
    
}
