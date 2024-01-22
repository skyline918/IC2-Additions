package ru.starshineproject.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import ru.starshineproject.IC2Additions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static ru.starshineproject.block.BlocksProperties.*;

public class ClientProxy extends CommonProxy{

    public static final IItemColor PURE_GLASS_ITEM_COLOR = ((stack, tintIndex) -> EnumDyeColor.byMetadata(stack.getItemDamage()).getColorValue());
    public static final IBlockColor PURE_GLASS_BLOCK_COLOR = ((IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)-> EnumDyeColor.byMetadata(state.getBlock().getMetaFromState(state)).getColorValue());
    public static final IItemColor CASING_ITEM_COLOR = ((stack, tintIndex) -> Casing.getAsMeta(stack.getItemDamage()).getColor());
    public static final IBlockColor CASING_BLOCK_COLOR = ((IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)-> Casing.getAsMeta(state.getBlock().getMetaFromState(state)).getColor());

    public static final IStateMapper normalStateMapper = new StateMapperBase() {
        @Override
        protected @Nonnull ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()), "normal");
        }
    };

    public static final IStateMapper connectedStateMapper = new StateMapperBase() {
        @Override
        @Nonnull
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            int type = state.getProperties().containsKey(NORTH) && state.getValue(NORTH) ? 1 : 0;
            type |= state.getProperties().containsKey(EAST) && state.getValue(EAST) ? 1 << 1 : 0;
            type |= state.getProperties().containsKey(SOUTH) && state.getValue(SOUTH) ? 1 << 2 : 0;
            type |= state.getProperties().containsKey(WEST) && state.getValue(WEST) ? 1 << 3 : 0;
            type |= state.getProperties().containsKey(TOP) && state.getValue(TOP) ? 1 << 4 : 0;
            type |= state.getProperties().containsKey(BOTTOM) && state.getValue(BOTTOM) ? 1 << 5 : 0;
            String typeName = type == 0 ? "normal" : "type="+type;
            return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()), typeName);
        }
    };

    public void preInit(){
        super.preInit();
    }
    public void init(){
        super.init();
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(PURE_GLASS_BLOCK_COLOR, IC2Additions.Blocks.pure_glass);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(PURE_GLASS_ITEM_COLOR, IC2Additions.Blocks.pure_glass);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(CASING_BLOCK_COLOR, IC2Additions.Blocks.tank_casing);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(CASING_ITEM_COLOR, IC2Additions.Blocks.tank_casing);
    }
    public void postInit(){
        super.postInit();
    }

}
