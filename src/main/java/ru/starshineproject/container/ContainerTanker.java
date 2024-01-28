package ru.starshineproject.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import ru.starshineproject.tile.tanker.TileEntityTankController;

import javax.annotation.Nonnull;

public class ContainerTanker extends Container {
    private final TileEntityTankController tankController;
    private Fluid lastFluid = null;
    private int lastAmount = -1;
    private TileEntityTankController.Status lastStatus = TileEntityTankController.Status.NULL;
    public ContainerTanker(InventoryPlayer playerInv, TileEntityTankController tankController) {
        this.tankController = tankController;

        int playerInventoryOffset = 2 * 18;

        this.addSlotToContainer(new Slot(this.tankController.basicInventory, 0, 113, 104));
        this.addSlotToContainer(new Slot(this.tankController.basicInventory, 1, 149, 104));


        // player inventory and hotbar
        for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                this.addSlotToContainer(new Slot(playerInv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + playerInventoryOffset));

        for (int i1 = 0; i1 < 9; ++i1)
            this.addSlotToContainer(new Slot(playerInv, i1, 8 + i1 * 18, 161 + playerInventoryOffset));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (anyFieldChanged()) {
            lastStatus = tankController.status;
            FluidTank tank = tankController.getCurrentTank();
            if(tank == null){
                lastFluid = null;
                lastAmount = -1;
            }else {
                FluidStack fluidStack = tank.getFluid();
                if(fluidStack == null){
                    lastFluid = null;
                    lastAmount = -1;
                }else {
                    lastFluid = fluidStack.getFluid();
                    lastAmount = fluidStack.amount;
                }
            }

            SPacketUpdateTileEntity spacketupdatetileentity = tankController.getUpdatePacket();
            if (spacketupdatetileentity == null) return;

            for (IContainerListener listener : listeners) {
                if (!(listener instanceof EntityPlayerMP)) continue;
                EntityPlayerMP player = ((EntityPlayerMP) listener);
                player.connection.sendPacket(spacketupdatetileentity);
            }
        }
    }

    public boolean anyFieldChanged(){
        if(lastStatus != tankController.status) return true;
        FluidTank tank = tankController.getCurrentTank();
        if(tank == null) return true;
        FluidStack stack = tank.getFluid();
        if(lastFluid != null && stack != null){
            if(lastFluid != stack.getFluid()) return true;
            return lastAmount != stack.amount;
        }
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 2)
            {
                if (!this.mergeItemStack(itemstack1, 2, 54, true))
                    return ItemStack.EMPTY;
            }
            else if (!this.mergeItemStack(itemstack1, 0, 1, false))
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
