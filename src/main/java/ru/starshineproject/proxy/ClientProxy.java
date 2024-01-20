package ru.starshineproject.proxy;

import net.minecraft.client.Minecraft;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.block.BlockPureGlass;
import ru.starshineproject.block.BlockTankCasing;

public class ClientProxy extends CommonProxy{
    public void preInit(){
        super.preInit();
    }
    public void init(){
        super.init();
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(BlockPureGlass.PURE_GLASS_BLOCK_COLOR, IC2Additions.Blocks.pure_glass);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(BlockPureGlass.PURE_GLASS_ITEM_COLOR, IC2Additions.Blocks.pure_glass);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(BlockTankCasing.CASING_BLOCK_COLOR, IC2Additions.Blocks.tank_casing);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(BlockTankCasing.CASING_ITEM_COLOR, IC2Additions.Blocks.tank_casing);
    }
    public void postInit(){
        super.postInit();
    }
}
