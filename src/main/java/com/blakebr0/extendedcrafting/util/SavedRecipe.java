package com.blakebr0.extendedcrafting.util;

import com.blakebr0.cucumber.helper.RecipeHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

public class SavedRecipe<C extends IInventory, T extends IRecipe<C>> {
    private final IRecipeType<T> type;
    private final ResourceLocation id;
    private final boolean invalid;

    public SavedRecipe(IRecipeType<T> type, ResourceLocation id, boolean invalid) {
        this.type = type;
        this.id = id;
        this.invalid = invalid;
    }

    public IRecipeType<?> getType() {
        return this.type;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    public IRecipe<C> getRecipe() {
        return this.invalid ? null : RecipeHelper.getRecipeManager().getRecipes(this.type).getOrDefault(this.id, null);
    }
}
