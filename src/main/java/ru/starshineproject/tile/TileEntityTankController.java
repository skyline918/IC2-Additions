package ru.starshineproject.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityTankController extends TileEntity implements ITickable {

    int width; //x
    int height; //y
    int length; //z

    public TileEntityTankController(){
    }
    @Override
    public void update() {

    }

    @Override
    public void onLoad() {

    }

    static BlockPos.MutableBlockPos mutBP = new BlockPos.MutableBlockPos();
    private void initializeTanker(){

    }
}
