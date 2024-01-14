package ru.starshineproject.tile;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import ru.starshineproject.config.IC2AdditionsConfig;
import ru.starshineproject.container.ContainerMiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityMiner extends TileEntityLockableLoot implements ITickable {

    public static final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

    public NonNullList<ItemStack> inventory;
    public BasicSink ic2EnergySink;
    public IC2AdditionsConfig.Miner config;
    @Nullable public UUID ownerUUID;
    @Nullable public String ownerName;
    public TileEntityMiner.Status status;
    public int ticks;
    public int cursorX;
    public int cursorY = -1;
    public int cursorZ;

    public enum Status {
        DISABLED_CONFIG("miner.status.disabled_config"),
        IN_PROGRESS("miner.status.in_progress"),
        NO_ENERGY("miner.status.no_energy"),
        FINISHED("miner.status.finished");

        public final String langKey;

        Status(String langKey) {
            this.langKey = langKey;
        }

        public static Status fromByte(byte b) {
            switch (b) {
                case 0: return DISABLED_CONFIG;
                case 1: return IN_PROGRESS;
                case 2: return NO_ENERGY;
                case 3: return FINISHED;
                default: return DISABLED_CONFIG;
            }
        }
        public byte toByte() {
            switch (this) {
                case DISABLED_CONFIG:
                    return 0;
                case IN_PROGRESS:
                    return 1;
                case NO_ENERGY:
                    return 2;
                case FINISHED:
                    return 3;
                default:
                    return 4;
            }
        }
    }

    @SuppressWarnings("unused") // default constructor for minecraft internals
    public TileEntityMiner() {
        this.ic2EnergySink = new BasicSink(this, IC2AdditionsConfig.miner_1.capacity, IC2AdditionsConfig.miner_1.tier);
        this.config = IC2AdditionsConfig.miner_1;
        this.inventory = NonNullList.withSize(18, ItemStack.EMPTY);
        this.status = Status.DISABLED_CONFIG;
    }

    public TileEntityMiner(IC2AdditionsConfig.Miner config) {
        this.ic2EnergySink = new BasicSink(this, config.capacity, config.tier);
        this.config = config;
        this.inventory = NonNullList.withSize(18, ItemStack.EMPTY);
        this.status = config.enabled ? Status.NO_ENERGY : Status.DISABLED_CONFIG;
    }

    public boolean canBeUsedBy(EntityPlayer player) {
        if (!IC2AdditionsConfig.ownershipEnabled) return true;
        if (this.ownerUUID == null) return true;
        return this.ownerUUID.equals(player.getUniqueID());
    }

    @Override
    protected @Nonnull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void update() {
        ticks++;
        if (ticks % 5 != 0) return;
        if (!this.config.enabled) return;


    }

    private void shiftPos() {
        moveCursorToStartIfInvalid();


    }

    private void moveCursorToStartIfInvalid() {
        if (cursorX < -config.radius || cursorX > config.radius) setCursorToStart();
        else if (cursorY >= 0) setCursorToStart();
        else if (cursorZ < -config.radius || cursorZ > config.radius) setCursorToStart();
    }

    private void setCursorToStart() {
        cursorX = -config.radius;
        cursorY = -1;
        cursorZ = -config.radius;
    }

    private void updateStatus() {

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
        ownerUUID = tag.hasUniqueId("ownerUUID") ? tag.getUniqueId("ownerUUID") : null;
        ownerName = tag.hasKey("ownerName") ? tag.getString("ownerName") : null;
        inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        cursorX = tag.getInteger("cursorX");
        cursorY = tag.getInteger("cursorY");
        cursorZ = tag.getInteger("cursorZ");
        if (!this.checkLootAndRead(tag))
            ItemStackHelper.loadAllItems(tag, this.inventory);
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        ic2EnergySink.writeToNBT(tag);
        if (this.ownerUUID != null) tag.setUniqueId("ownerUUID", this.ownerUUID);
        if (this.ownerName != null) tag.setString("ownerName", this.ownerName);
        if (!this.checkLootAndWrite(tag)) ItemStackHelper.saveAllItems(tag, this.inventory);
        tag.setInteger("cursorX", cursorX);
        tag.setInteger("cursorY", cursorX);
        tag.setInteger("cursorZ", cursorX);
        return tag;
    }

    /**
     * Called on chunk sync
     * **/
    @Override
    public @Nonnull NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        ic2EnergySink.writeToNBT(tag);
        if (this.ownerName != null) tag.setString("ownerName", this.ownerName);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.ownerName = tag.hasKey("ownerName") ? tag.getString("ownerName") : null;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        ic2EnergySink.writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
        ic2EnergySink.readFromNBT(pkt.getNbtCompound());
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

    public void setOwner(EntityPlayer placer) {
        this.ownerName = placer.getName();
        this.ownerUUID = placer.getUniqueID();
        this.markDirty();
    }
}
