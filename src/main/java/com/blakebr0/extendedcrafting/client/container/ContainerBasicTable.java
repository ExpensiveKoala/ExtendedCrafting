package com.blakebr0.extendedcrafting.client.container;

import javax.annotation.Nullable;

import com.blakebr0.extendedcrafting.crafting.table.TableCraftResult;
import com.blakebr0.extendedcrafting.crafting.table.TableCrafting;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.blakebr0.extendedcrafting.crafting.table.TableResultHandler;
import com.blakebr0.extendedcrafting.crafting.table.TableStackHandler;
import com.blakebr0.extendedcrafting.tile.TileBasicCraftingTable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBasicTable extends Container {

	public InventoryCrafting matrix;
	public IInventory result;
	public TileBasicCraftingTable tile;
	public IItemHandler handler;

	public ContainerBasicTable(InventoryPlayer player, TileBasicCraftingTable tile, World world) {
		this.tile = tile;
		this.handler = tile.getMatrix();
		this.matrix = new TableCrafting(this, tile);
		this.result = new TableCraftResult(tile);
		
		this.addSlotToContainer(new TableResultHandler(this.matrix, this.result, 0, 124, 36));
		
		int wy, ex;
		for (wy = 0; wy < 3; ++wy) {
			for (ex = 0; ex < 3; ++ex) {
				this.addSlotToContainer(new SlotItemHandler(this.handler, ex + wy * 3, 32 + ex * 18, 18 + wy * 18));
			}
		}

		for (wy = 0; wy < 3; ++wy) {
			for (ex = 0; ex < 9; ++ex) {
				this.addSlotToContainer(new Slot(player, ex + wy * 9 + 9, 8 + ex * 18, 88 + wy * 18));
			}
		}

		for (ex = 0; ex < 9; ++ex) {
			this.addSlotToContainer(new Slot(player, ex, 8 + ex * 18, 146));
		}

		this.onCraftMatrixChanged(this.matrix);
		((TableStackHandler) this.handler).crafting = this.matrix;
	}

	public void onCraftMatrixChanged(IInventory matrix) {
		this.result.setInventorySlotContents(0, TableRecipeManager.getInstance().findMatchingRecipe(this.matrix, this.tile.getWorld()));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.tile.isUseableByPlayer(player);
	}

	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotNumber);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotNumber == 0) {
				if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (slotNumber >= 10 && slotNumber < 46) {
				if (!this.mergeItemStack(itemstack1, 1, 10, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
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
			this.onCraftMatrixChanged(this.matrix);
		}

		return itemstack;
	}
}