package com.blakebr0.extendedcrafting.block;

import com.blakebr0.cucumber.block.BaseTileEntityBlock;
import com.blakebr0.cucumber.iface.IEnableable;
import com.blakebr0.cucumber.util.VoxelShapeBuilder;
import com.blakebr0.extendedcrafting.config.ModConfigs;
import com.blakebr0.extendedcrafting.lib.ModTooltips;
import com.blakebr0.extendedcrafting.tileentity.UltimateTableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import java.util.List;

public class UltimateTableBlock extends BaseTileEntityBlock implements IEnableable {
	public static final VoxelShape ULTIMATE_TABLE_SHAPE = new VoxelShapeBuilder()
			.cuboid(14, 2, 14, 2, 0, 2)
			.cuboid(5, 10, 5, 3, 2, 3)
			.cuboid(13, 10, 13, 11, 2, 11)
			.cuboid(13, 10, 5, 11, 2, 3)
			.cuboid(5, 10, 13, 3, 2, 11)
			.cuboid(16, 16, 16, 0, 10, 0)
			.build();

	public UltimateTableBlock() {
		super(Material.METAL, SoundType.METAL, 5.0F, 10.0F, ToolType.PICKAXE);
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return new UltimateTableTileEntity();
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
		if (!world.isClientSide()) {
			BlockEntity tile = world.getBlockEntity(pos);

			if (tile instanceof UltimateTableTileEntity)
				player.openMenu((UltimateTableTileEntity) tile);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile instanceof UltimateTableTileEntity) {
			UltimateTableTileEntity table = (UltimateTableTileEntity) tile;
			Containers.dropContents(world, pos, table.getInventory().getStacks());
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return ULTIMATE_TABLE_SHAPE;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(ModTooltips.TIER.args(4).build());
	}

	@Override
	public boolean isEnabled() {
		return ModConfigs.ENABLE_TABLES.get();
	}
}
