package tile;

import config.IC2AdditionsConfig;
import ic2.api.energy.prefab.BasicSink;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnull;

public class TileEntityMiner extends TileEntity implements ITickable {

    BasicSink ic2EnergySink;
    IC2AdditionsConfig.Miner config;

    @SuppressWarnings("unused") // default constructor for minecraft
    public TileEntityMiner() {
        this.ic2EnergySink = new BasicSink(this, IC2AdditionsConfig.miner_1.capacity, IC2AdditionsConfig.miner_1.tier);
        this.config = IC2AdditionsConfig.miner_1;
    }

    public TileEntityMiner(IC2AdditionsConfig.Miner config) {
        this.ic2EnergySink = new BasicSink(this, config.capacity, config.tier);
        this.config = config;
    }

    @Override
    public void update() {

    }

    @Override
    public void onLoad() {
        ic2EnergySink.onLoad();
    }

    @Override
    public void invalidate() {
        ic2EnergySink.invalidate();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        ic2EnergySink.onChunkUnload();

    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound tag) {
        super.readFromNBT(tag);
        ic2EnergySink.readFromNBT(tag);
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        ic2EnergySink.writeToNBT(tag);
        return tag;
    }
}
