package ru.starshineproject.tile;

import com.mojang.authlib.GameProfile;
import ic2.api.energy.prefab.BasicSink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.config.IC2AConfig;
import ru.starshineproject.container.ContainerMiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class TileEntityMiner extends TileEntityLockableLoot implements ITickable {
    public static final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
    public NonNullList<ItemStack> inventory;
    public BasicSink ic2EnergySink;
    public IC2AConfig.Miner config;
    @Nullable public UUID ownerUUID;
    @Nullable public String ownerName;
    @Nullable public FakePlayer breaker = null;
    public TileEntityMiner.Status status;
    public int ticks;
    public int cursorX = 0;
    public int cursorY = 0;
    public int cursorZ = 0;
    public long lastActiveSecond = Instant.now().getEpochSecond();

    public enum Status {
        DISABLED_CONFIG("miner.status.disabled_config", true),
        DISABLED_DIM("miner.status.disabled_config_dim", true),
        IN_PROGRESS("miner.status.in_progress"),
        NO_ENERGY("miner.status.no_energy"),
        FINISHED("miner.status.finished", true),
        FULL_INVENTORY("miner.status.no_empty_slot"),
        NULL("miner.status.null");

        public final String langKey;
        public final boolean shutdown;

        Status(String langKey) {
            this.langKey = langKey;
            this.shutdown = false;
        }

        Status(String langKey, boolean shutdown) {
            this.langKey = langKey;
            this.shutdown = shutdown;
        }

        public static Status fromByte(byte b) {
            switch (b) {
                case 0:     return DISABLED_CONFIG;
                case 1:     return DISABLED_DIM;
                case 2:     return IN_PROGRESS;
                case 3:     return NO_ENERGY;
                case 4:     return FINISHED;
                case 5:     return FULL_INVENTORY;
                default:    return NULL;
            }
        }
        public byte toByte() {
            switch (this) {
                case DISABLED_CONFIG:   return 0;
                case DISABLED_DIM:      return 1;
                case IN_PROGRESS:       return 2;
                case NO_ENERGY:         return 3;
                case FINISHED:          return 4;
                case FULL_INVENTORY:    return 5;
                default:                return 15;
            }
        }
    }

    @SuppressWarnings("unused") // default constructor for minecraft internals
    public TileEntityMiner() {
        this.ic2EnergySink = new BasicSink(this, IC2AConfig.MINER_1.capacity, IC2AConfig.MINER_1.tier);
        this.config = IC2AConfig.MINER_1;
        this.inventory = NonNullList.withSize(18, ItemStack.EMPTY);
        this.status = Status.NULL;
    }

    public TileEntityMiner(IC2AConfig.Miner config) {
        this.ic2EnergySink = new BasicSink(this, config.capacity, config.tier);
        this.config = config;
        this.inventory = NonNullList.withSize(18, ItemStack.EMPTY);
        this.status = config.enabled ? Status.NULL : Status.DISABLED_CONFIG;
    }

    public boolean canBeUsedBy(EntityPlayer player) {
        if (!IC2AConfig.ownershipEnabled) return true;
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
        if(status.shutdown) return;

        if (!this.config.enabled){
            status = Status.DISABLED_CONFIG;
            return;
        }

        if(!IC2AConfig.AVAILABLE_DIMS.contains(world.provider.getDimension())){
            status = Status.DISABLED_DIM;
            return;
        }

        if(!ic2EnergySink.canUseEnergy(config.energyToBlock)) return;   //Status NO_ENERGY
        if(isInventoryFull()) return;                                   //Status FULL INVENTORY

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
                if(isInventoryFull())
                    break;
                if(canInsertToInventory(getCursorItem()) == inventory.size())
                    break;
            }

            //SHIFT ONLY IF IN PROCESS!!!!
            status = Status.IN_PROGRESS;
            shiftPos();
            if(isCursorOutOfWorld())
                continue;
            if(!isCursorInFinish()){
                status = Status.FINISHED;
                break;
            }
            ic2EnergySink.useEnergy(config.energyToBlock);
            iterationNumber++;
            ItemStack mined = getCursorItem();
            int slotIdToPush = canInsertToInventory(mined);
            if(slotIdToPush == inventory.size()){
                status = Status.FULL_INVENTORY;
                break;
            }
            if(slotIdToPush != -1){
                if(canMine()){
                    mine();
                    insertToInventory(mined, slotIdToPush);
                }
            }

            deltaTicks -= config.ticksForEachBlock;
            if(iterationNumber >= IC2AConfig.maxIterationPerTick){
                break;
            }
        }
        lastActiveSecond = updateSec - deltaTicks/20;
    }

    //Return slot id for push item. -1 = not push. inventory size = full
    private int canInsertToInventory(@Nullable ItemStack itemStack){
        if (itemStack == null)
            return -1;
        for (int id = 0; id < inventory.size(); id++) {
            ItemStack invStack = inventory.get(id);
            if(invStack.isEmpty())
                return id;
            if(invStack.getItem() != itemStack.getItem())
                continue;
            if(invStack.getMetadata() != itemStack.getMetadata())
                continue;
            if(invStack.getCount() + itemStack.getCount() <= getInventoryStackLimit())
                return id;
        }
        return inventory.size();
    }

    private void insertToInventory(ItemStack itemStack, int slotId){
        if(itemStack==null)
            return;
        ItemStack invStack = inventory.get(slotId);
        if(invStack.getItem() == itemStack.getItem()){
            inventory.get(slotId).grow(itemStack.getCount());
            return;
        }
        inventory.set(slotId,itemStack);
    }

    @SuppressWarnings("deprecation")
    private @Nullable ItemStack getCursorItem(){
        IBlockState state = world.getBlockState(cursor);
        Block block = state.getBlock();

        if(block instanceof BlockLiquid) return null;
        if(block.hasTileEntity(state)) return null;

        ItemStack stack = block.getItem(world,cursor,state);
        if(stack == ItemStack.EMPTY) return null;

        Set<Integer> metas = IC2AConfig.MINED_ORES.getOrDefault(stack.getItem(), null);
        if(metas == null) return null;
        if(!metas.contains(stack.getMetadata())) return null;

        return stack;
    }

    private boolean canMine(){
        if(!IC2AConfig.ownershipEnabled)
            return true;
        if(breaker == null)
            return false;
        int expDrop =  ForgeHooks.onBlockBreakEvent(this.world, GameType.SURVIVAL, breaker, cursor);
        if(expDrop == -1)
            return false;
        return true;
    }

    private void mine(){
        IBlockState state = world.getBlockState(cursor);
        if(breaker != null)
            state.getBlock().onBlockHarvested(world, cursor, state, breaker);
        world.setBlockState(cursor, Blocks.COBBLESTONE.getDefaultState());
    }

    private boolean isInventoryFull(){
        for (ItemStack stack: inventory)
            if(stack.isEmpty() || stack.getCount() < getInventoryStackLimit())
                return false;
        return true;
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

    private boolean isCursorOutOfWorld(){
        WorldBorder border = world.getWorldBorder();
        int bmx = (int) (border.getCenterX()-border.getDiameter()/2);
        int bMx = (int) (border.getCenterX()+border.getDiameter()/2);
        int bmz = (int) (border.getCenterZ()-border.getDiameter()/2);
        int bMz = (int) (border.getCenterZ()+border.getDiameter()/2);
        return cursor.getX() < bmx || cursor.getX() > bMx || cursor.getZ() < bmz || cursor.getZ() > bMz;
    }

    private boolean isCursorInFinish(){
        cursor.setPos(this.pos.getX() + cursorX,this.pos.getY() + cursorY,this.pos.getZ() + cursorZ);
        return cursor.getY() > 0;
    }

    private boolean moveCursorToStartIfInvalid() {
        int radius = config.radius;
        if (cursorX < -radius || cursorX > radius || cursorZ < -radius || cursorZ > radius || cursorY >= 0) {
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
        initBreaker();
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
        config = IC2AConfig.getMinerByNumber(tag.getByte("minerTier"));
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
        this.config = IC2AConfig.getMinerByNumber(tag.getByte("minerTier"));
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
        if(!world.isRemote) initBreaker();
        this.markDirty();
    }

    private void initBreaker(){
        if(ownerName == null || ownerUUID == null) return;
        breaker = new FakePlayer((WorldServer) world,new GameProfile(ownerUUID,ownerName));
        breaker.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), breaker) {
            @Override public void sendPacket(@Nonnull Packet packetIn) {}
            @Override public void update() {}
        };
    }

}
