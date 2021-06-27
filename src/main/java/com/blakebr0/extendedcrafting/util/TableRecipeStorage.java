package com.blakebr0.extendedcrafting.util;

import com.blakebr0.cucumber.helper.RecipeHelper;
import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.api.crafting.RecipeTypes;
import com.blakebr0.extendedcrafting.container.inventory.ExtendedCraftingInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class TableRecipeStorage {
    private final SavedRecipe<?, ?>[] recipes;
    private int selected = -1;

    public TableRecipeStorage() {
        this.recipes = new SavedRecipe[3];
    }

    public int getSelected() {
        return this.selected;
    }

    public void setSelected(int selected) {
        if (selected == this.selected || selected < -1 || selected > 2)
            selected = -1;

        this.selected = selected;
    }

    public SavedRecipe<?, ?>[] getRecipes() {
        return this.recipes;
    }

    public SavedRecipe<?, ?> getRecipeAt(int index) {
        if (index < 0 || index >= 3)
            return null;

        return this.recipes[index];
    }

    public boolean hasRecipeAt(int index) {
        if (index < 0 || index >= 3)
            return false;

        return this.recipes[index] != null;
    }

    public void setRecipeAt(int index, IRecipe<?> recipe) {
        this.recipes[index] = new SavedRecipe(recipe.getType(), recipe.getId(), false);
    }

    public void removeRecipeAt(int index) {
        if (index < 0 || index >= 3)
            return;

        this.recipes[index] = null;

        if (index == this.selected)
            this.selected = -1;
    }

    public SavedRecipe getSelectedRecipe() {
        if (this.selected < 0 || this.selected > 3)
            return null;

        return this.recipes[this.selected];
    }

    public CompoundNBT serializeNBT() {
        ListNBT recipes = new ListNBT();
        for (int i = 0; i < 3; i++) {
            SavedRecipe recipe = this.recipes[i];
            if (recipe != null) {
                CompoundNBT recipeNBT = new CompoundNBT();
                recipeNBT.putString("type", recipe.getType().toString());
                recipeNBT.putString("id", recipe.getId().toString());

                recipes.add(recipeNBT);
            }
        }

        CompoundNBT tag = new CompoundNBT();
        tag.put("Recipes", recipes);
        tag.putInt("Selected", this.selected);

        return tag;
    }

    public void deserializeNBT(CompoundNBT tag) {
        ListNBT recipes = tag.getList("Recipes", Constants.NBT.TAG_COMPOUND);

        // TODO: 1.17: remove
        if (tag.contains("Size")) {
            for (int i = 0; i < recipes.size(); i++) {
                BaseItemStackHandler savedRecipeInventory = new BaseItemStackHandler(tag.getInt("Size"));
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                ITableRecipe recipe;

                savedRecipeInventory.deserializeNBT(recipes.getCompound(i));

                BaseItemStackHandler recipeInventory = new BaseItemStackHandler(savedRecipeInventory.getSlots() - 1);

                for (int j = 0; j < recipeInventory.getSlots(); j++) {
                    recipeInventory.setStackInSlot(j, savedRecipeInventory.getStackInSlot(i));
                }

                if (server != null) {
                    recipe = RecipeHelper.getRecipeManager().getRecipe(RecipeTypes.TABLE, recipeInventory.toIInventory(), server.getWorld(World.OVERWORLD)).orElse(null);
                } else {
                    recipe = RecipeHelper.getRecipeManager().getRecipe(RecipeTypes.TABLE, recipeInventory.toIInventory(), Minecraft.getInstance().world).orElse(null);
                }

                if (recipe != null) {
                    this.recipes[i] = new SavedRecipe(recipe.getType(), recipe.getId(), false);
                } else {
                    ExtendedCraftingInventory craftingInventory = new ExtendedCraftingInventory(recipeInventory, 3);
                    ICraftingRecipe vanilla;

                    if (server != null) {
                        vanilla = RecipeHelper.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInventory, server.getWorld(World.OVERWORLD)).orElse(null);
                    } else {
                        vanilla = RecipeHelper.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInventory, Minecraft.getInstance().world).orElse(null);
                    }

                    if (vanilla != null) {
                        this.recipes[i] = new SavedRecipe(vanilla.getType(), vanilla.getId(), false);
                    }
                }

                tag.remove("Size");
            }
        } else {
            for (int i = 0; i < recipes.size(); i++) {
                CompoundNBT recipe = recipes.getCompound(i);
                IRecipeType<?> type = Registry.RECIPE_TYPE.getOrDefault(new ResourceLocation(recipe.getString("type")));

                this.recipes[i] = new SavedRecipe(type, new ResourceLocation(recipe.getString("id")), type == null);
            }
        }

        this.selected = tag.getInt("Selected");
    }

}
