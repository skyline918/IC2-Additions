package block;

import config.IC2AdditionsConfig;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tile.TileEntityMiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockMiner extends Block implements IWrenchable {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool WORKING = PropertyBool.create("working");

    IC2AdditionsConfig.Miner config;

    public BlockMiner(IC2AdditionsConfig.Miner config) {
        super(Material.IRON);
        this.setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(WORKING, false));
        this.config = config;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityMiner(this.config);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nonnull IBlockState getStateFromMeta(int meta) {
        boolean fourthBitIsSet = (meta & 4) > 0;
        int firstThreeBits = (meta & 3);
        return this.getDefaultState()
                .withProperty(WORKING, fourthBitIsSet)
                .withProperty(FACING, EnumFacing.byIndex(5 - firstThreeBits));
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        int i = 0;
        if (state.getValue(WORKING)) i |= 4;
        i = i | (5 - state.getValue(FACING).getIndex());
        return i;
    }

    @Override
    public EnumFacing getFacing(World world, BlockPos blockPos) {
        return null;
    }

    @Override
    public boolean setFacing(World world, BlockPos blockPos, EnumFacing enumFacing, EntityPlayer entityPlayer) {
        return false;
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
