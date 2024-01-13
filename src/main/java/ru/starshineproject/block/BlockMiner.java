package ru.starshineproject.block;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.config.IC2AdditionsConfig;
import ru.starshineproject.gui.GuiMiner;
import ru.starshineproject.tile.TileEntityMiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class BlockMiner extends Block implements IWrenchable {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool WORKING = PropertyBool.create("working");

    IC2AdditionsConfig.Miner config;

    @Override
    protected @Nonnull BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, WORKING, FACING);
    }

    public BlockMiner(IC2AdditionsConfig.Miner config) {
        super(Material.IRON);
        this.setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(WORKING, false));
        this.config = config;
    }

    @ParametersAreNonnullByDefault
    public @Nonnull IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(WORKING, false);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityMiner(this.config);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMiner) {
            playerIn.openGui(IC2Additions.instance, GuiMiner.id, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nonnull IBlockState getStateFromMeta(int meta) {
        boolean thirdBitIsSet = (meta & 0b0100) > 0;
        int rotationBits = (meta & 0b0011);
        return this.getDefaultState()
                .withProperty(WORKING, thirdBitIsSet)
                .withProperty(FACING, EnumFacing.byHorizontalIndex(rotationBits));
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        int i = 0;
        if (state.getValue(WORKING)) i |= 0b0100;
        i = i | state.getValue(FACING).getHorizontalIndex();
        return i;
    }

    @Override
    public EnumFacing getFacing(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos).getValue(FACING);
    }

    @Override
    public boolean setFacing(World world, BlockPos pos, EnumFacing enumFacing, EntityPlayer entityPlayer) {
        if (enumFacing.getAxis() == EnumFacing.Axis.Y) return false;

        return world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, enumFacing));
    }

    @Override
    public boolean wrenchCanRemove(World world, BlockPos blockPos, EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos blockPos, IBlockState iBlockState, TileEntity tileEntity, EntityPlayer entityPlayer, int i) {
        return null;
    }

}
