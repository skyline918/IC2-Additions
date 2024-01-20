package ru.starshineproject.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockTankCasing extends Block {
    public static final IItemColor CASING_ITEM_COLOR = ((stack, tintIndex) -> Casing.getAsMeta(stack.getItemDamage()).getColor());
    public static final IBlockColor CASING_BLOCK_COLOR = ((IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)-> Casing.getAsMeta(state.getBlock().getMetaFromState(state)).getColor());

    public static final PropertyEnum<Casing> TYPE = PropertyEnum.create("type",Casing.class);
    public BlockTankCasing(){
        super(Material.IRON);
        this.setDefaultState(getBlockState().getBaseState().withProperty(TYPE,Casing.STEEL));
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Casing casing: Casing.values()) {
            items.add(new ItemStack(this,1,casing.getMeta()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE,Casing.getAsMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    public enum Casing implements IStringSerializable {
        STEEL("steel", 0, 0x394147),
        ALUMINIUM("aluminium", 1, 0x74bec3),
        STAINLESS_STEEL("stainless_steel", 2, 0xabccdc),
        TITAN("titan", 3, 0xc892df),
        TUNGSTEN_STEEL("tungsten_steel", 4, 0x443488);

        public static final Casing[] CASINGS = new Casing[Casing.values().length];
        public final String name;
        public final int meta;
        public final int color;

        Casing(String name, int meta, int color) {
            this.name = name;
            this.meta = meta;
            this.color = color;
        }

        public static Casing getAsMeta(int meta){
            if(meta >= CASINGS.length || meta < 0)
                return STEEL;
            return CASINGS[meta];
        }

        public int getMeta() {
            return meta;
        }

        public int getColor(){
            return color;
        }

        @Override
        public String getName() {
            return this.name;
        }

        //feel array
        static {
            for (Casing casing:Casing.values()) {
                CASINGS[casing.meta] = casing;
            }
        }
    }
}
