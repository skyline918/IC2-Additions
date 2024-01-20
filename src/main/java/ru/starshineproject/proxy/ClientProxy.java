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
import ru.starshineproject.block.BlockTankCasing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientProxy extends CommonProxy{

    public static final IItemColor PURE_GLASS_ITEM_COLOR = ((stack, tintIndex) -> EnumDyeColor.byMetadata(stack.getItemDamage()).getColorValue());
    public static final IBlockColor PURE_GLASS_BLOCK_COLOR = ((IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)-> EnumDyeColor.byMetadata(state.getBlock().getMetaFromState(state)).getColorValue());
    public static final IItemColor CASING_ITEM_COLOR = ((stack, tintIndex) -> BlockTankCasing.Casing.getAsMeta(stack.getItemDamage()).getColor());
    public static final IBlockColor CASING_BLOCK_COLOR = ((IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)-> BlockTankCasing.Casing.getAsMeta(state.getBlock().getMetaFromState(state)).getColor());

    public static final IStateMapper normalStateMapper = new StateMapperBase() {
        @Override
        protected @Nonnull ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()), "normal");
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
