package ru.starshineproject.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ru.starshineproject.container.ContainerMiner;
import ru.starshineproject.container.ContainerTanker;
import ru.starshineproject.tile.TileEntityMiner;
import ru.starshineproject.tile.tanker.TileEntityTankController;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        pos.setPos(x,y,z);
        TileEntity tile = world.getTileEntity(pos);
//        if (!(tile instanceof IInventory)) return null;

        switch (ID) {
            case GuiMiner.id: return tile instanceof TileEntityMiner ? new ContainerMiner(player.inventory, (TileEntityMiner) tile) : null;
            case GuiTanker.id: return tile instanceof TileEntityTankController ? new ContainerTanker(player.inventory, (TileEntityTankController) tile) : null;
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        pos.setPos(x,y,z);
        TileEntity tile = world.getTileEntity(pos);
//        if (!(tile instanceof IInventory)) return null;

        switch (ID) {
            case GuiMiner.id: return tile instanceof TileEntityMiner ? new GuiMiner(player.inventory, (TileEntityMiner) tile) : null;
            case GuiTanker.id: return tile instanceof TileEntityTankController ? new GuiTanker(player.inventory, (TileEntityTankController) tile) : null;
        }
        return null;
    }
}
