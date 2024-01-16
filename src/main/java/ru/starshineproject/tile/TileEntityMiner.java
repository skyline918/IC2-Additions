package ru.starshineproject.tile;

import ic2.api.energy.prefab.BasicSink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.config.IC2AdditionsConfig;
import ru.starshineproject.container.ContainerMiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
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
    public int cursorX = 0;
    public int cursorY = 0;
    public int cursorZ = 0;
    public long lastActiveSecond = Instant.now().getEpochSecond();

    public enum Status {
        DISABLED_CONFIG("miner.status.disabled_config"),
        IN_PROGRESS("miner.status.in_progress"),
        NO_ENERGY("miner.status.no_energy"),
        FINISHED("miner.status.finished"),
        FULL_INVENTORY("miner.status.no_empty_slot");

        public final String langKey;

        Status(String langKey) {
            this.langKey = langKey;
        }

        public static Status fromByte(byte b) {
            switch (b) {
                case 1: return IN_PROGRESS;
                case 2: return NO_ENERGY;
                case 3: return FINISHED;
                case 4: return FULL_INVENTORY;
                default: return DISABLED_CONFIG;
            }
        }
        public byte toByte() {
            switch (this) {
                case IN_PROGRESS:
                    return 1;
                case NO_ENERGY:
                    return 2;
                case FINISHED:
                    return 3;
                case FULL_INVENTORY:
                    return 4;
                default:
                    return 0;
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
        if(world.isRemote) return;
        ticks++;
        if (ticks % 5 != 0) return;
        if (!this.config.enabled) return;
        if(status == Status.FINISHED) return;
        if(!ic2EnergySink.canUseEnergy(config.energyToBlock)) return; //Status NO_ENERGY
        if(isInventoryFull()) return;

        long updateSec = Instant.now().getEpochSecond();
        int deltaTicks = Math.toIntExact(updateSec-lastActiveSecond) * 20;
        int iterationNumber = 0;
        while (deltaTicks > config.ticksForEachBlock){
            //Energy check
            if(ic2EnergySink.getEnergyStored() < config.energyToBlock){
                status = Status.NO_ENERGY;
                break;
            }
            //Check inventory status after mining
            if(status == Status.FULL_INVENTORY){
                if(isInventoryFull()){

                    break;
                }
            }
            shiftPos();
            if(!isChangedCursorValid()){
                status = Status.FINISHED;
                break;
            }
            status = Status.IN_PROGRESS;
            ic2EnergySink.useEnergy(config.energyToBlock);
            iterationNumber++;
            this.world.setBlockToAir(cursor); //TODO DEBUG, REMOVE IT ON RELEASE

            //TODO Scan

            deltaTicks -= config.ticksForEachBlock;
            if(iterationNumber >= IC2AdditionsConfig.maxIterationPerTick){
                break;
            }
        }
        lastActiveSecond = updateSec - deltaTicks/20;
    }

    private boolean isInventoryFull(){
        for (ItemStack stack: inventory)
            if(stack.isEmpty() || stack.getCount() < getInventoryStackLimit())
                return false;
        return true;
    }

    private Item getCursorBlockItem(){
        isChangedCursorValid();
        return Item.getItemFromBlock(world.getBlockState(cursor).getBlock());
    }

    private boolean isOre(Item item){
        return false;
    }

    private void shiftPos() {
        if(moveCursorToStartIfInvalid())
            return;
        int radius = config.radius;
        cursorX++;
        if (cursorX == radius){
            cursorX = -radius;
            cursorZ++;
        }
        if (cursorZ == radius){
            cursorZ = -radius;
            cursorX = -radius;
            cursorY--;
        }
    }

    private boolean isChangedCursorValid(){
        cursor.setPos(this.pos.getX() + cursorX,this.pos.getY() + cursorY,this.pos.getZ() + cursorZ);
        return cursor.getY() > 0;
    }

    private boolean moveCursorToStartIfInvalid() {
        int radius = config.radius;
        if (cursorX < -radius || cursorX > radius || cursorZ < -radius || cursorZ > radius || cursorY >= 0) {
            IC2Additions.logger.info("mctsii {} {} {}", cursorX, cursorY, cursorZ);
            setCursorToStart();
            return true;
        }
        return false;
    }

    private void setCursorToStart() {
        cursorX = -config.radius;
        cursorY = -1;
        cursorZ = -config.radius;
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
        //req data
        ownerUUID = tag.hasUniqueId("ownerUUID") ? tag.getUniqueId("ownerUUID") : null;
        ownerName = tag.hasKey("ownerName") ? tag.getString("ownerName") : null;
        config = IC2AdditionsConfig.getMinerConfig(tag.getByte("minerTier"));
        //energy
        ic2EnergySink.readFromNBT(tag);
        ic2EnergySink.setCapacity(config.capacity);
        ic2EnergySink.setSinkTier(config.tier);
        //work info
        cursorX = tag.getInteger("cursorX");
        cursorY = tag.getInteger("cursorY");
        cursorZ = tag.getInteger("cursorZ");
        lastActiveSecond = tag.hasKey("lastActiveSecond") ? tag.getLong("lastActiveSecond") : Instant.now().getEpochSecond();
        //inventory
        inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(tag)) ItemStackHelper.loadAllItems(tag, this.inventory);
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        //req data
        if (this.ownerUUID != null) tag.setUniqueId("ownerUUID", this.ownerUUID);
        if (this.ownerName != null) tag.setString("ownerName", this.ownerName);
        tag.setByte("minerTier", (byte) config.tier);
        //energy
        ic2EnergySink.writeToNBT(tag);
        //work info
        tag.setInteger("cursorX", cursorX);
        tag.setInteger("cursorY", cursorY);
        tag.setInteger("cursorZ", cursorZ);
        tag.setLong("lastActiveSecond", lastActiveSecond);
        //inventory
        if (!this.checkLootAndWrite(tag)) ItemStackHelper.saveAllItems(tag, this.inventory);
        return tag;
    }

    /**
     * Called on chunk sync
     * **/
    @Override
    public @Nonnull NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        ic2EnergySink.writeToNBT(tag);
        tag.setByte("minerTier", (byte) config.tier);
        if (this.ownerName != null) tag.setString("ownerName", this.ownerName);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.ownerName = tag.hasKey("ownerName") ? tag.getString("ownerName") : null;
        this.config = IC2AdditionsConfig.getMinerConfig(tag.getByte("minerTier"));
    }

    /**
     * Called on gui sync
     */
    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        ic2EnergySink.writeToNBT(tag);
         tag.setByte("mineStatus", status.toByte());
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
        ic2EnergySink.readFromNBT(pkt.getNbtCompound());
        status = Status.fromByte(pkt.getNbtCompound().getByte("mineStatus"));
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
