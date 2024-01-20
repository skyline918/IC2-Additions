package ru.starshineproject.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockTankCasing extends Block implements IPropertyValueName{

    public static final PropertyEnum<Casing> TYPE = PropertyEnum.create("type",Casing.class);
    public BlockTankCasing(){
        super(Material.IRON);
        this.setDefaultState(getBlockState().getBaseState().withProperty(TYPE,Casing.STEEL));
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
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public String getValueName(ItemStack stack) {
        return Casing.getAsMeta(stack.getMetadata()).getName();
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
        @Nonnull
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
