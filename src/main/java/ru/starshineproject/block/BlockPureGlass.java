package ru.starshineproject.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BlockPureGlass extends Block implements IPropertyValueName {

    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
    public BlockPureGlass(){
        super(Material.GLASS);
        this.setDefaultState(getBlockState().getBaseState().withProperty(COLOR, EnumDyeColor.WHITE));
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(COLOR).getMetadata();
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

    @SuppressWarnings("deprecation")
    @Override
    public @Nonnull MapColor getMapColor(IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos)
    {
        return MapColor.getBlockColor(state.getValue(COLOR));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public @Nonnull BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean canSilkHarvest()
    {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(@Nonnull IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nonnull IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(COLOR)).getMetadata();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state)
    {
        return false;
    }
    @Override
    protected @Nonnull BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, COLOR);
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(@Nonnull IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, @Nonnull EnumFacing side)
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
