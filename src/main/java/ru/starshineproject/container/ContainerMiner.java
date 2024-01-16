package ru.starshineproject.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import ru.starshineproject.tile.TileEntityMiner;

import javax.annotation.Nonnull;

public class ContainerMiner extends Container {
    private final TileEntityMiner miner;
    private double lastEnergy = 0;
    private TileEntityMiner.Status lastStatus = TileEntityMiner.Status.DISABLED_CONFIG;

    public ContainerMiner(IInventory playerInventory, TileEntityMiner miner) {
        this.miner = miner;
        int numRows = 6;
        int playerInventoryOffset = (numRows - 4) * 18;

        int index = 0;
        for (int j = 0; j < numRows; ++j)
            for (int k = 6; k < 9; ++k) {
                this.addSlotToContainer(new Slot(this.miner, index, 8 + k * 18, 18 + j * 18));
                index++;
            }

        // player inventory and hotbar
        for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + playerInventoryOffset));

        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + playerInventoryOffset));

    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return miner.isUsableByPlayer(playerIn);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (energyChanged() || updateStatus()) {
            lastEnergy = miner.ic2EnergySink.getEnergyStored();
            lastStatus = miner.status;
            SPacketUpdateTileEntity spacketupdatetileentity = miner.getUpdatePacket();
            if (spacketupdatetileentity == null) return;

            for (IContainerListener listener : listeners) {
                if (!(listener instanceof EntityPlayerMP)) continue;
                EntityPlayerMP player = ((EntityPlayerMP) listener);
                player.connection.sendPacket(spacketupdatetileentity);
            }
        }
    }

    private boolean energyChanged() {
        return lastEnergy != miner.ic2EnergySink.getEnergyStored();
    }

    private boolean updateStatus(){
        return lastStatus != miner.status;
    }

    public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 18)
            {
                if (!this.mergeItemStack(itemstack1, 18, 54, true))
                    return ItemStack.EMPTY;
            }
            else if (!this.mergeItemStack(itemstack1, 0, 18, false))
                return ItemStack.EMPTY;

            if (itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
