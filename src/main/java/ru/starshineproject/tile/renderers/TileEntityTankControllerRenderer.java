package ru.starshineproject.tile.renderers;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import ru.starshineproject.client.TankerFluidRender;
import ru.starshineproject.tile.tanker.TileEntityTankController;

import javax.annotation.ParametersAreNonnullByDefault;

public class TileEntityTankControllerRenderer extends TileEntitySpecialRenderer<TileEntityTankController> {
    @Override
    @ParametersAreNonnullByDefault
    public void render(TileEntityTankController tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(tile.isTankBroken())
            return;
        FluidTank tank = tile.getCurrentTank();
        if(tank == null)
            return;
        FluidStack stack = tank.getFluid();
        if(stack == null)
            return;
        float fillingPercent = (float) stack.amount / tank.getCapacity();
        if(fillingPercent == 0)
            return;
        boolean isGaseous = stack.getFluid().isGaseous();
        AxisAlignedBB aabb = tile.getTankerAABB(isGaseous,fillingPercent);
        if(aabb == null)
            return;
        float gasAlpha = -1;
        if(isGaseous)
            gasAlpha = fillingPercent;
        TankerFluidRender.render(aabb,gasAlpha,stack.getFluid(),getWorld().getCombinedLight(tile.getPos(), stack.getFluid().getLuminosity()));
    }
}
