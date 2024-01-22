package ru.starshineproject.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public class BlocksProperties {

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyBool BOTTOM = PropertyBool.create("bottom");

    public static final PropertyEnum<Casing> TYPE = PropertyEnum.create("type", Casing.class);

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
