package ru.starshineproject.proxy;

import net.minecraft.client.Minecraft;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.block.BlockPureGlass;

public class ClientProxy extends CommonProxy{
    public void preInit(){}
    public void init(){
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(BlockPureGlass.PURE_GLASS_BLOCK_COLOR, IC2Additions.Blocks.pure_glass);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(BlockPureGlass.PURE_GLASS_ITEM_COLOR, IC2Additions.Blocks.pure_glass);
    }
    public void postInit(){}
}
