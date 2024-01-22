package ru.starshineproject.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static ru.starshineproject.block.BlocksProperties.*;

public class BlockTankCasing extends Block implements IPropertyValueName{
    public BlockTankCasing(){
        super(Material.IRON);
        this.setDefaultState(getBlockState().getBaseState()
                .withProperty(TYPE,Casing.STEEL)
                .withProperty(NORTH, false)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(TOP, false)
                .withProperty(BOTTOM, false)
        );
    }
    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Casing casing: Casing.values()) {
            items.add(new ItemStack(this,1,casing.getMeta()));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE,Casing.getAsMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean[] connection = getConnections(worldIn, pos);
        state = state.withProperty(NORTH, connection[0]);
        state = state.withProperty(EAST, connection[1]);
        state = state.withProperty(SOUTH, connection[2]);
        state = state.withProperty(WEST, connection[3]);
        state = state.withProperty(TOP, connection[4]);
        state = state.withProperty(BOTTOM, connection[5]);
        return state;
    }

    public boolean[] getConnections(IBlockAccess world, BlockPos pos){
        boolean[] connections = new boolean[]{false,false,false,false,false,false};
        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
        IBlockState neighborState;
        IBlockState curBlockState = world.getBlockState(pos);
        Casing casingType = curBlockState.getValue(TYPE);

        //North
        neighborPos.setPos(pos.getX(), pos.getY(), pos.getZ()-1);
        if((neighborState = world.getBlockState(neighborPos)).getBlock() == this)
            connections[0] = neighborState.getValue(TYPE) == casingType;

        //EAST
        neighborPos.setPos(pos.getX()+1, pos.getY(), pos.getZ());
        if((neighborState = world.getBlockState(neighborPos)).getBlock() == this)
            connections[1] = neighborState.getValue(TYPE) == casingType;

        //SOUTH
        neighborPos.setPos(pos.getX(), pos.getY(), pos.getZ()+1);
        if((neighborState = world.getBlockState(neighborPos)).getBlock() == this)
            connections[2] = neighborState.getValue(TYPE) == casingType;

        //WEST
        neighborPos.setPos(pos.getX()-1, pos.getY(), pos.getZ());
        if((neighborState = world.getBlockState(neighborPos)).getBlock() == this)
            connections[3] = neighborState.getValue(TYPE) == casingType;

        //TOP
        neighborPos.setPos(pos.getX(), pos.getY()+1, pos.getZ());
        if((neighborState = world.getBlockState(neighborPos)).getBlock() == this)
            connections[4] = neighborState.getValue(TYPE) == casingType;

        //BOTTOM
        neighborPos.setPos(pos.getX(), pos.getY()-1, pos.getZ());
        if((neighborState = world.getBlockState(neighborPos)).getBlock() == this)
            connections[5] = neighborState.getValue(TYPE) == casingType;


        return connections;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, NORTH, EAST, SOUTH, WEST, TOP, BOTTOM);
    }

    @Override
    public String getValueName(ItemStack stack) {
        return Casing.getAsMeta(stack.getMetadata()).getName();
    }


}
