package ru.starshineproject.tile;

import net.minecraft.inventory.ItemStackHelper;
import ru.starshineproject.config.IC2AdditionsConfig;
import ic2.api.energy.prefab.BasicSink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import ru.starshineproject.container.ContainerMiner;

import javax.annotation.Nonnull;

public class TileEntityMiner extends TileEntityLockableLoot implements ITickable {

    NonNullList<ItemStack> inventory;
    BasicSink ic2EnergySink;
    IC2AdditionsConfig.Miner config;

    @SuppressWarnings("unused") // default constructor for minecraft
    public TileEntityMiner() {
        this.ic2EnergySink = new BasicSink(this, IC2AdditionsConfig.miner_1.capacity, IC2AdditionsConfig.miner_1.tier);
        this.config = IC2AdditionsConfig.miner_1;
        this.inventory = NonNullList.withSize(18, ItemStack.EMPTY);
    }

    @Override
    protected @Nonnull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    public TileEntityMiner(IC2AdditionsConfig.Miner config) {
        this.ic2EnergySink = new BasicSink(this, config.capacity, config.tier);
        this.config = config;
        this.inventory = NonNullList.withSize(18, ItemStack.EMPTY);
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

        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(tag))
            ItemStackHelper.loadAllItems(tag, this.inventory);
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        ic2EnergySink.writeToNBT(tag);
        if (!this.checkLootAndWrite(tag)) ItemStackHelper.saveAllItems(tag, this.inventory);
        return tag;
    }

    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public @Nonnull Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return new ContainerMiner(playerInventory, this);
    }

    @Override
    public @Nonnull String getGuiID() {
        return "gui.miner" + config.tier;
    }

    @Override
    public @Nonnull String getName() {
        return "container.miner" + config.tier;
    }
}
