package ru.starshineproject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.starshineproject.client.MinerRangeRadius;
import ru.starshineproject.proxy.ClientProxy;
import ru.starshineproject.tile.TileEntityMiner;

import java.awt.*;

@Mod.EventBusSubscriber
public class ClientEvents {

    static Color COLOR_MINER_RANGE = new Color(81, 205, 196);

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerStateMapper(ModelRegistryEvent event){
        ModelLoader.setCustomStateMapper(IC2Additions.Blocks.pure_glass, ClientProxy.normalStateMapper);
        ModelLoader.setCustomStateMapper(IC2Additions.Blocks.tank_casing, ClientProxy.normalStateMapper);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderAfterWorld(RenderWorldLastEvent event) {
        WorldClient worldClient = Minecraft.getMinecraft().world;

        for (TileEntity tileEntity : worldClient.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntityMiner)) continue;

            if (((TileEntityMiner) tileEntity).needToRender) {
                AxisAlignedBB aabb = ((TileEntityMiner) tileEntity).getRangeAABB();

                if (aabb == null) continue;
                MinerRangeRadius.renderBox(aabb, COLOR_MINER_RANGE);
            }
        }
    }

}
