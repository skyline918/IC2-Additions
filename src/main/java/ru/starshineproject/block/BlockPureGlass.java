package ru.starshineproject.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BlockPureGlass extends Block implements IPropertyValueName {
    public static final IItemColor PURE_GLASS_ITEM_COLOR = ((stack, tintIndex) -> EnumDyeColor.byMetadata(stack.getItemDamage()).getColorValue());
    public static final IBlockColor PURE_GLASS_BLOCK_COLOR = ((IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)-> EnumDyeColor.byMetadata(state.getBlock().getMetaFromState(state)).getColorValue());
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
    public BlockPureGlass(){
        super(Material.GLASS);
        this.setDefaultState(getBlockState().getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        for (EnumDyeColor enumdyecolor : EnumDyeColor.values())
        {
            items.add(new ItemStack(this, 1, enumdyecolor.getMetadata()));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.getBlockColor((EnumDyeColor)state.getValue(COLOR));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
    protected boolean canSilkHarvest()
    {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumDyeColor)state.getValue(COLOR)).getMetadata();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    @Override
    @MethodsReturnNonnullByDefault
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, COLOR);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();

        if (blockState != iblockstate) return true;
        if (block == this) return false;

        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Override
    public String getValueName(ItemStack stack) {
        return EnumDyeColor.byMetadata(stack.getMetadata()).getTranslationKey();
    }
}
