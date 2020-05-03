package com.blakebr0.extendedcrafting.container;

import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.api.crafting.RecipeTypes;
import com.blakebr0.extendedcrafting.container.inventory.ExtendedCraftingInventory;
import com.blakebr0.extendedcrafting.container.slot.TableOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;
import java.util.function.Function;

public class UltimateTableContainer extends Container {
	private final Function<PlayerEntity, Boolean> isUsableByPlayer;
	private final World world;
	private final IItemHandlerModifiable result;

	private UltimateTableContainer(ContainerType<?> type, int id, PlayerInventory playerInventory) {
		this(type, id, playerInventory, p -> false, new BaseItemStackHandler(81));
	}

	private UltimateTableContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, Function<PlayerEntity, Boolean> isUsableByPlayer, BaseItemStackHandler inventory) {
		super(type, id);
		this.isUsableByPlayer = isUsableByPlayer;
		this.world = playerInventory.player.world;
		this.result = new ItemStackHandler();
		IInventory matrix = new ExtendedCraftingInventory(this, inventory);

		this.addSlot(new TableOutputSlot(this, matrix, this.result, 0, 206, 89));
		
		int i, j;
		for (i = 0; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				this.addSlot(new Slot(matrix, j + i * 9, 8 + j * 18, 18 + i * 18));
			}
		}

		for (i = 0; i < 3; i++) {
			for (j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 39 + j * 18, 196 + i * 18));
			}
		}

		for (j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 39 + j * 18, 254));
		}

		this.onCraftMatrixChanged(matrix);
	}

	@Override
	public void onCraftMatrixChanged(IInventory matrix) {
		Optional<ITableRecipe> recipe = this.world.getRecipeManager().getRecipe(RecipeTypes.TABLE, matrix, this.world);
		if (recipe.isPresent()) {
			ItemStack result = recipe.get().getCraftingResult(matrix);
			this.result.setStackInSlot(0, result);
		} else {
			this.result.setStackInSlot(0, ItemStack.EMPTY);
		}

		super.onCraftMatrixChanged(matrix);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return this.isUsableByPlayer.apply(player);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotNumber) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotNumber);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotNumber == 0) {
				if (!this.mergeItemStack(itemstack1, 82, 118, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (slotNumber >= 82 && slotNumber < 118) {
				if (!this.mergeItemStack(itemstack1, 1, 82, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 82, 118, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	public static UltimateTableContainer create(int windowId, PlayerInventory playerInventory) {
		return new UltimateTableContainer(ModContainerTypes.ULTIMATE_TABLE.get(), windowId, playerInventory);
	}

	public static UltimateTableContainer create(int windowId, PlayerInventory playerInventory, Function<PlayerEntity, Boolean> isUsableByPlayer, BaseItemStackHandler inventory) {
		return new UltimateTableContainer(ModContainerTypes.ULTIMATE_TABLE.get(), windowId, playerInventory, isUsableByPlayer, inventory);
	}
}