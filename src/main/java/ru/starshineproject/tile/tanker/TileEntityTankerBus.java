package ru.starshineproject.tile.tanker;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import ru.starshineproject.tile.IColored;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class TileEntityTankerBus extends TileEntity implements IFluidHandler, IColored {
    TileEntityTankController controller;

    public TileEntityTankerBus() {
    }

    public void setController(TileEntityTankController controller) {
        this.controller = controller;
        IBlockState state = this.world.getBlockState(this.pos);
        world.notifyBlockUpdate(this.pos,state,state,2);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("hasController", controller != null);
        if (controller != null) {
            BlockPos headPos = controller.getPos();
            tag.setInteger("headX", headPos.getX());
            tag.setInteger("headY", headPos.getY());
            tag.setInteger("headZ", headPos.getZ());
        }
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        if (tag.getBoolean("hasController")) {
            BlockPos controllerPos = new BlockPos(
                    tag.getInteger("headX"),
                    tag.getInteger("headY"),
                    tag.getInteger("headZ")
            );
            TileEntity tile = this.world.getTileEntity(controllerPos);
            if(tile instanceof TileEntityTankController){
                setController((TileEntityTankController) tile);
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && controller != null || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (controller == null)
                return null;
            return (T) controller.fluidTank;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        if (controller == null)
            return new IFluidTankProperties[0];
        return controller.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (controller == null)
            return 0;
        return controller.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (controller == null)
            return null;
        return controller.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (controller == null)
            return null;
        return controller.drain(maxDrain, doDrain);
    }

    @Override
    public int getColor() {
        if (controller == null)
            return 0xffffffff;
        return controller.getColor();
    }
}
