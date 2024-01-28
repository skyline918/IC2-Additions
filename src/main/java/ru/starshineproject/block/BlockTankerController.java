package ru.starshineproject.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.gui.GuiMiner;
import ru.starshineproject.gui.GuiTanker;
import ru.starshineproject.tile.tanker.TileEntityTankController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockTankerController extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockTankerController(){
        super(Material.IRON);
        this.setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @ParametersAreNonnullByDefault
    public @Nonnull IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityTankController();
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityTankController)) return true;
        TileEntityTankController tank = (TileEntityTankController) tile;
        if(world.isRemote){
            player.sendMessage(new TextComponentString(tank.status.langKey));
            return true;
        }
        player.openGui(IC2Additions.instance, GuiTanker.id, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING,EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,FACING);
    }
}
