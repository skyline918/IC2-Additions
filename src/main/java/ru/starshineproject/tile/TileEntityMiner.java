package ru.starshineproject.tile;

import com.mojang.authlib.GameProfile;
import ic2.api.energy.prefab.BasicSink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import ru.starshineproject.block.BlockMiner;
import ru.starshineproject.config.IC2AdditionsConfig;
import ru.starshineproject.container.ContainerMiner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.world.GameType.SURVIVAL;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class TileEntityMiner extends TileEntityLockableLoot implements ITickable {

    public static final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
    public static final UUID DEFAULT_UUID = new UUID(0,10);
    public static final String DEFAULT_NAME = "none";
    public static final HashMap<Item, Integer> VALID_ORES = new HashMap<>();

    public NonNullList<ItemStack> inventory;
    public BasicSink ic2EnergySink;
    public IC2AdditionsConfig.Miner config;
    public UUID ownerUUID = DEFAULT_UUID;
    public String ownerName = DEFAULT_NAME;
    public TileEntityMiner.Status status;
    public int ticks;
    public int cursorX;
    public int cursorY = 200;
    public int cursorZ;
    public long lastUpdated = Instant.now().toEpochMilli();
    private FakePlayer fakePlayer;
    public int totalMined;
    public int totalScanned;
    private AxisAlignedBB rangeAABB;
    private boolean readyForRender = false;
    public boolean needToRender = false;

    public enum Status {
        DISABLED_CONFIG("tile.miner.status.disabled_config", new Color(255, 100, 100).getRGB()),
        IN_PROGRESS("tile.miner.status.in_progress", new Color(150, 200, 255).getRGB()),
        NO_ENERGY("tile.miner.status.no_energy", new Color(255, 100, 100).getRGB()),
        FINISHED("tile.miner.status.finished", new Color(255, 100, 100).getRGB()),
        INVENTORY_IS_FULL("tile.miner.status.inventory_is_full", new Color(255, 100, 100).getRGB()),
        BLACKLIST_DIMENSION("tile.miner.status.blacklist-dim", new Color(255, 100, 100).getRGB());

        public final String langKey;
        public final int color;
        public String langCache;

        Status(String langKey, int color) {
            this.langKey = langKey;
            this.color = color;
        }

        public String toLocalizedString() {
            if (langCache == null) {
                langCache = I18n.format(this.langKey);
            }
            return langCache;
        }

        public static Status fromByte(byte b) {
            switch (b) {
                case 0: return DISABLED_CONFIG;
                case 1: return IN_PROGRESS;
                case 2: return NO_ENERGY;
                case 3: return FINISHED;
                case 4: return INVENTORY_IS_FULL;
                case 5: return BLACKLIST_DIMENSION;
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
                case INVENTORY_IS_FULL:
                    return 4;
                case BLACKLIST_DIMENSION:
                    return 5;
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


    public @Nullable AxisAlignedBB getRangeAABB() {
        if (!this.readyForRender) return null;
        if (this.rangeAABB == null) {
            this.rangeAABB = new AxisAlignedBB(
                    this.pos.getX() - config.radius,
                    this.pos.getY() - 1,
                    this.pos.getZ() - config.radius,
                    this.pos.getX() + config.radius,
                    0,
                    this.pos.getZ() + config.radius
            );
        }
        return this.rangeAABB;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        if (oldState.getBlock() == newState.getBlock()) return false; // if state changed we do not recreate tile (from forge doc)

        return super.shouldRefresh(world, pos, oldState, newState);
    }

    @Override
    public void setPos(@Nonnull BlockPos posIn) {
        super.setPos(posIn);
        this.readyForRender = true;
    }

    private boolean checkBlacklisted(World world) {
        if (IC2AdditionsConfig.dimIsBlacklisted(world.provider.getDimension())) {
            this.status = TileEntityMiner.Status.BLACKLIST_DIMENSION;
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        ticks++;
        if (world.isRemote) return;
        if (ticks % 20 != 0) return; // no frequent updates.
        if (!this.config.enabled) return;
        if (this.status == Status.FINISHED) return;
        if (!world.isAreaLoaded(pos, config.radius)) return;
        if (this.status == Status.BLACKLIST_DIMENSION) return;
        if (checkBlacklisted(world)) return;

        moveCursorToStartIfInvalid();

        int updates = 0;
        long now = Instant.now().toEpochMilli();

        while (updates < config.maxScansPerUpdate) {
            updates++;

            if (!ic2EnergySink.canUseEnergy(config.requiredEnergyToScan + config.requiredEnergyToMine)) {
                this.lastUpdated = Instant.now().toEpochMilli();
                this.setStatus(Status.NO_ENERGY);
                return;
            }
            if (lastUpdated + (config.msToScan + config.msToMine) >= now) {
                return;
            }

            cursor.setPos(pos.getX() + cursorX, pos.getY() + cursorY, pos.getZ() + cursorZ);
            if (!world.getWorldBorder().contains(cursor)) {
                if (moveNextOrFinish()) break;
                continue;
            }

            ic2EnergySink.useEnergy(config.requiredEnergyToScan);
            lastUpdated += config.requiredEnergyToScan;

            IBlockState state = world.getBlockState(cursor);
            Block block = state.getBlock();

            ItemStack result = canMine(block, state);
            if (result == null) {
                totalScanned++;
                if (moveNextOrFinish()) break;
                continue;
            }

            IItemHandler itemHandler = this.getCapability(ITEM_HANDLER_CAPABILITY, null);
            boolean pushed = pushStack(result, itemHandler);
            if (pushed) {
                totalScanned++;
                totalMined++;
                lastUpdated += config.msToMine;
                ic2EnergySink.useEnergy(config.requiredEnergyToMine);
                mine(block, state);
                if (moveNextOrFinish()) break;
            } else {
                this.lastUpdated = Instant.now().toEpochMilli();
                setStatus(Status.INVENTORY_IS_FULL);
                break;
            }
        }

    }

    void setStatus(TileEntityMiner.Status status) {
        if (this.status == status) return;

        this.status = status;
        if (!world.getBlockState(this.pos).getProperties().containsKey(BlockMiner.WORKING)) return;

        if (status == Status.IN_PROGRESS)
            world.setBlockState(this.pos, world.getBlockState(this.pos).withProperty(BlockMiner.WORKING, true));
        else
            world.setBlockState(this.pos, world.getBlockState(this.pos).withProperty(BlockMiner.WORKING, false));
    }

    /**
     * @return true if there is no blocks left to scan (i.e miner finished)
     * **/
    private boolean moveNextOrFinish() {
        boolean hasNext = shiftPos();
        if (!hasNext) {
            this.lastUpdated = Instant.now().toEpochMilli();
            setStatus(Status.FINISHED);
            return true;
        }
        setStatus(Status.IN_PROGRESS);
        return false;
    }

    /**
     * @return true if stack is pushed, false if inventory is full
     * **/
    private boolean pushStack(ItemStack passStack, @Nullable IItemHandler handler) {
        if (handler == null) return false;
        int slot = -1;
        for (int j = 0; j < handler.getSlots(); j++) {
            if (handler.insertItem(j, passStack, true).isEmpty()) {
                slot = j;
                break;
            }
        }

        if (slot != -1) {
            handler.insertItem(slot, passStack, false);
            return true;
        }

        return false;
    }

    /**
     * @return null if scanned block is invalid, ItemStack if is valid
     * **/
    private @Nullable ItemStack canMine(Block block, IBlockState state) {

        if (block.isAir(state, world, cursor)) return null;
        if (block instanceof BlockLiquid) return null;

        int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, SURVIVAL, fakePlayer, cursor);
        if (exp == -1) return null;

        // get getSilkTouchDrop is better, but it is protected (ATs can't help because of overrides)
        @SuppressWarnings("deprecation") ItemStack itemstack = block.getItem(world, pos, state);
        if (itemstack.isEmpty()) return null;

        Integer meta = VALID_ORES.get(itemstack.getItem());
        if (meta == null) return null;

        if (meta == itemstack.getMetadata()) {
            return itemstack;
        }
        return null;
    }

    /**
     * mines the block on cursor (changes state to placeholder block). It is safe because we check events before
     * **/
    void mine(Block block, IBlockState state) {
        block.onBlockHarvested(world, cursor, state, fakePlayer);
        world.setBlockState(cursor, Blocks.COBBLESTONE.getDefaultState(), 3);
    }

    protected void initFakePlayer() {
        FakePlayer player = FakePlayerFactory.get((WorldServer) world, new GameProfile(this.ownerUUID, this.ownerName));
        player.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), player) {
            @Override
            public void sendPacket(@Nonnull Packet packetIn) {}

            @Override
            public void update() {}
        };
        fakePlayer = player;
    }

    private boolean shiftPos() {
        if (moveCursorToStartIfInvalid()) return true;
        if (xEnd() && yEnd() && zEnd()) return false;

        if (xEnd() && zEnd()) {
            cursorY--;
            cursorX = -config.radius;
            cursorZ = -config.radius;
            return true;
        }

        if (xEnd()) {
            cursorX = -config.radius;
            cursorZ++;
            return true;
        }

        cursorX++;
        return true;
    }

    private boolean xEnd() {
        return cursorX == config.radius;
    }

    private boolean yEnd() {
        return this.pos.getY() + cursorY == 0;
    }

    private boolean zEnd() {
        return cursorZ == config.radius;
    }

    private boolean moveCursorToStartIfInvalid() {
        boolean cursorXIsOutsideRadius = cursorX < -config.radius || cursorX > config.radius;
        boolean cursorYIsOutsideRadius = this.pos.getY() + cursorY < 0 || cursorY >= 0;
        boolean cursorZIsOutsideRadius = cursorZ < -config.radius || cursorZ > config.radius;
        if (cursorXIsOutsideRadius || cursorYIsOutsideRadius || cursorZIsOutsideRadius) {
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
        if (!world.isRemote) initFakePlayer();
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
        this.readyForRender = true;
        config = IC2AdditionsConfig.getConfigFromLevel(tag.getShort("minerLevel"));
        ic2EnergySink.readFromNBT(tag);
        ic2EnergySink.setSinkTier(config.tier);
        ic2EnergySink.setCapacity(config.capacity);

        ownerUUID = tag.hasUniqueId("ownerUUID") ? tag.getUniqueId("ownerUUID") : DEFAULT_UUID;
        ownerName = tag.hasKey("ownerName") ? tag.getString("ownerName") : DEFAULT_NAME;
        inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        cursorX = tag.getInteger("cursorX");
        cursorY = tag.getInteger("cursorY");
        cursorZ = tag.getInteger("cursorZ");
        lastUpdated = tag.getLong("lastUpdated");
        totalMined = tag.getInteger("totalMined");
        totalScanned = tag.getInteger("totalScanned");

        if (!this.checkLootAndRead(tag))
            ItemStackHelper.loadAllItems(tag, this.inventory);
    }

    @Override
    public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        super.writeToNBT(tag);
        ic2EnergySink.writeToNBT(tag);
        if (!this.checkLootAndWrite(tag)) ItemStackHelper.saveAllItems(tag, this.inventory);

        tag.setUniqueId("ownerUUID", this.ownerUUID);
        tag.setString("ownerName", this.ownerName);
        tag.setInteger("cursorX", cursorX);
        tag.setInteger("cursorY", cursorY);
        tag.setInteger("cursorZ", cursorZ);
        tag.setLong("lastUpdated", lastUpdated);
        tag.setShort("minerLevel", (short) config.level);
        tag.setInteger("totalMined", totalMined);
        tag.setInteger("totalScanned", totalScanned);

        return tag;
    }

    /**
     * Called on chunk sync
     * **/
    @Override
    public @Nonnull NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        ic2EnergySink.writeToNBT(tag);
        tag.setShort("minerLevel", (short) config.level);
        if (this.ownerName != null) tag.setString("ownerName", this.ownerName);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readyForRender = true; // for client rendering
        this.config = IC2AdditionsConfig.getConfigFromLevel(tag.getShort("minerLevel"));
        this.ic2EnergySink.setCapacity(config.capacity);
        this.ic2EnergySink.setSinkTier(config.tier);
        this.ownerName = tag.hasKey("ownerName") ? tag.getString("ownerName") : DEFAULT_NAME;
    }

    /**
     * Called in container sync
     * **/
    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        ic2EnergySink.writeToNBT(tag);
        tag.setByte("status", this.status.toByte());
        tag.setLong("lastUpdated", lastUpdated);
        tag.setInteger("cursorY", cursorY);
        tag.setInteger("totalMined", totalMined);
        tag.setInteger("totalScanned", totalScanned);
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        this.ic2EnergySink.readFromNBT(tag);
        this.status = Status.fromByte(tag.getByte("status"));
        this.lastUpdated = tag.getLong("lastUpdated");
        this.cursorY = tag.getInteger("cursorY");
        this.totalMined = tag.getInteger("totalMined");
        this.totalScanned = tag.getInteger("totalScanned");
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
