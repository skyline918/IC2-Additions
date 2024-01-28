package ru.starshineproject.tile.tanker;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.block.BlocksProperties;
import ru.starshineproject.config.IC2AdditionsConfig;
import ru.starshineproject.tile.IColored;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TileEntityTankController extends TileEntity implements ITickable, IFluidHandler, IColored {
    public final IInventory basicInventory = new InventoryBasic("",false,2);
    public int tier = -1;
    public Status status = Status.NULL;
    protected FluidTank fluidTank;
    protected int minX = 0, minZ = 0, minY = 0;
    protected int maxX = 0, maxZ = 0, maxY = 0;

    Set<TileEntityTankerBus> buses = new HashSet<>();

    public TileEntityTankController(){
    }

    int ticks = 1;
    @Override
    public void update() {
        ticks++;
        if(world.isRemote){
            //UPDATE BLOCK COLOR
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(this.pos,state,state,2);
            return;
        }
        if(ticks%40!=0) return;
        validateTanker();
    }

    @Override
    public void onLoad() {
        if(world.isRemote) return;
        validateTanker();
    }

    private void validateTanker(){
        IBlockState state = world.getBlockState(pos);
        if(!initTankSize()){
            status = Status.NULL;
            resetTank(state);
            return;
        }
        if(!hasFrame()){
            status = Status.BROKEN_FRAME;
            resetTank(state);
            return;
        }
        if(!isHermetic()){
            status = Status.NOT_HERMETIC;
            resetTank(state);
            return;
        }
        if(!isTankBroken()) initFluidTank();
        status = Status.INITIALIZED;
        world.notifyBlockUpdate(this.pos,state,state,2);
    }

    private void resetTank(IBlockState state){
        clearBuses();
        tier = -1;
        world.notifyBlockUpdate(this.pos,state,state,2);
        markDirty();
    }

    private void initFluidTank(){
        int volume = (maxZ - minZ) * (maxX - minX) * (maxY - minY);
        volume *= tier * IC2AdditionsConfig.tankerCasingMultiplier;
        volume *= IC2AdditionsConfig.millibucketsPerBlock;
        if(fluidTank == null)
            fluidTank = new FluidTank(volume);
        else {
            fluidTank.setCapacity(volume);
        }
        if(fluidTank.getFluid() == null){
            fluidTank.fill(new FluidStack(FluidRegistry.LAVA, volume/3*2),true);
        }
        markDirty();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void readFromNBT(NBTTagCompound tag)
    {
        if(tag.hasKey("tanker")){
            NBTTagCompound tankerTag = tag.getCompoundTag("tanker");

            if(tankerTag.getBoolean("tankBroken")) return;
            if(!tankerTag.hasKey("volume")) return;

            int tankerCapacity = tankerTag.getInteger("volume");

            fluidTank = new FluidTank(tankerCapacity);

            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tankerTag);
            if(fluidStack == null) return;

            fluidTank.fill(fluidStack,true);
        }
        super.readFromNBT(tag);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag = super.writeToNBT(tag);
        NBTTagCompound tankerTag = new NBTTagCompound();
        FluidStack fluidStack;
        tankerTag.setBoolean("tankBroken", isTankBroken());
        if(fluidTank!=null){
            fluidStack = fluidTank.getFluid();
            if(fluidStack != null){
                tankerTag.setInteger("volume", fluidTank.getCapacity());
                fluidStack.writeToNBT(tankerTag);
            }
        }
        tag.setTag("tanker",tankerTag);
        return tag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        tag.setByte("tier", (byte) tier);
        tag.setByte("status", (byte) status.id);
        tag.setShort("minX",(short) minX);
        tag.setShort("maxX",(short) maxX);
        tag.setShort("minY",(short) minY);
        tag.setShort("maxY",(short) maxY);
        tag.setShort("minZ",(short) minZ);
        tag.setShort("maxZ",(short) maxZ);
        return new SPacketUpdateTileEntity(pos,1,tag);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        readFromNBT(tag);
        if(tag.hasKey("tier"))tier = tag.getByte("tier");
        if(tag.hasKey("status"))status = Status.getById(tag.getByte("status"));
        if(tag.hasKey("minX"))minX = tag.getShort("minX");
        if(tag.hasKey("maxX"))maxX = tag.getShort("maxX");
        if(tag.hasKey("minY"))minY = tag.getShort("minY");
        if(tag.hasKey("maxY"))maxY = tag.getShort("maxY");
        if(tag.hasKey("minZ"))minZ = tag.getShort("minZ");
        if(tag.hasKey("maxZ"))maxZ = tag.getShort("maxZ");
    }

    static BlockPos.MutableBlockPos mutBP = new BlockPos.MutableBlockPos();
    private boolean initTankSize(){
        int tileX = this.pos.getX(), tileY = this.pos.getY(), tileZ = this.pos.getZ();
        minX = 0; minZ = 0; minY = 0;
        maxX = 0; maxZ = 0; maxY = 0;

        IBlockState state = getBlockState(world,tileX,tileY,tileZ);
        if(state == null) return false;
        if(!state.getProperties().containsKey(BlockHorizontal.FACING)) return false;
        EnumFacing tankerFront = state.getValue(BlockHorizontal.FACING);

        // minX, maxX, minZ, maxZ
        boolean edgeX = tankerFront.getAxis() == EnumFacing.Axis.Z;
        int count = 0;

        while (true){
            if(count >= IC2AdditionsConfig.maxTankerSize)
                return false;
            if(edgeX) minX--; else  minZ--;
            count++;

            state = getBlockState( world,tileX+minX,tileY,tileZ+minZ);
            if(state==null) return false;
            if(isInvalidBlockFrame(state)){
                if(edgeX) minX++; else minZ++;
                break;
            }
        }
        while (true){
            if(count >= IC2AdditionsConfig.maxTankerSize)
                return false;
            if(edgeX) maxX++; else  maxZ++;
            count++;

            state = getBlockState(world, tileX+maxX,tileY,tileZ+maxZ);
            if(state==null) return false;
            if(isInvalidBlockFrame(state)){
                if(edgeX) maxX--; else maxZ--;
                break;
            }
        }

        count = 0;
        edgeX = !edgeX;
        boolean findMin = tankerFront.getAxisDirection().getOffset() > 0;
        while (count < IC2AdditionsConfig.maxTankerSize){
            if(edgeX)
                if(findMin) minX--; else maxX++;
            else
                if(findMin) minZ--; else maxZ++;


            state = getBlockState(world,tileX+minX, tileY, tileZ+minZ);
            if(state==null) return false;
            if(isInvalidBlockFrame(state)){
                if(edgeX)
                    if(findMin) minX++; else maxX--;
                else
                    if(findMin) minZ++; else maxZ--;

                break;
            }
            count++;
        }

        count = 0;
        while (count < IC2AdditionsConfig.maxTankerSize){
            maxY++;
            state = getBlockState(world,tileX+minX, tileY+maxY, tileZ+minZ);
            if(state == null) return false;
            if(isInvalidBlockFrame(state)){
                maxY--;
                break;
            }
            count++;
        }

        return maxX - minX > 3 && maxY - minY > 3 && maxZ - minZ > 3;
    }

    private boolean hasFrame(){
        int tileX = this.pos.getX(), tileY = this.pos.getY(), tileZ = this.pos.getZ();
        BlocksProperties.Casing casing = null;

        //get casing type
        IBlockState state = getBlockState(world,tileX+minX,tileY+minY,tileZ+minZ);
        if(state != null)
            if(state.getProperties().containsKey(BlocksProperties.TYPE))
                casing = state.getValue(BlocksProperties.TYPE);

        state = getBlockState(world,tileX+maxX,tileY+maxY,tileZ+maxZ);
        if(state == null)
            if(casing == null)
                return false;

        if(casing == null)
            if(state.getProperties().containsKey(BlocksProperties.TYPE))
                casing = state.getValue(BlocksProperties.TYPE);



        if(casing == null) return false;

        int absMin = Math.min(Math.min(minZ,minX),minY);
        int absMax = Math.max(Math.max(maxZ,maxX),maxY);

        for (int v = absMin; v <= absMax; v++) {
            int x = Math.max(Math.min(v,maxX),minX);
            int y = Math.max(Math.min(v,maxY),minY);
            int z = Math.max(Math.min(v,maxZ),minZ);

            if(frameBlockInvalid(world, tileX + minX, tileY + y, tileZ + minZ, casing)) return false;
            if(frameBlockInvalid(world, tileX + maxX, tileY + y, tileZ + minZ, casing)) return false;
            if(frameBlockInvalid(world, tileX + maxX, tileY + y, tileZ + maxZ, casing)) return false;
            if(frameBlockInvalid(world, tileX + minX, tileY + y, tileZ + maxZ, casing)) return false;

            if(frameBlockInvalid(world, tileX + x, tileY + minY, tileZ + minZ, casing)) return false;
            if(frameBlockInvalid(world, tileX + x, tileY + maxY, tileZ + minZ, casing)) return false;
            if(frameBlockInvalid(world, tileX + x, tileY + maxY, tileZ + maxZ, casing)) return false;
            if(frameBlockInvalid(world, tileX + x, tileY + minY, tileZ + maxZ, casing)) return false;

            if(frameBlockInvalid(world, tileX + minX, tileY + minY, tileZ + z, casing)) return false;
            if(frameBlockInvalid(world, tileX + maxX, tileY + minY, tileZ + z, casing)) return false;
            if(frameBlockInvalid(world, tileX + maxX, tileY + maxY, tileZ + z, casing)) return false;
            if(frameBlockInvalid(world, tileX + minX, tileY + maxY, tileZ + z, casing)) return false;
        }

        this.tier = casing.getMeta()+1;

        return true;
    }

    private boolean isHermetic(){
        int tileX = this.pos.getX(), tileY = this.pos.getY(), tileZ = this.pos.getZ();

        int absMin = Math.min(Math.min(minZ,minX),minY);
        int absMax = Math.max(Math.max(maxZ,maxX),maxY);

        for (int i=absMin; i <= absMax; i++){

            int xi = Math.max(Math.min(i,maxX),minX);
            int yi = Math.max(Math.min(i,maxY),minY);

            for (int j=absMin; j <= absMax; j++) {

                int yj = Math.max(Math.min(j,maxY),minY);
                int zj = Math.max(Math.min(j,maxZ),minZ);

                if(hermeticBlockInvalid(tileX+xi, tileY+minY, tileZ+zj))return false;
                if(hermeticBlockInvalid(tileX+xi, tileY+maxY, tileZ+zj))return false;

                if(hermeticBlockInvalid(tileX+xi, tileY+yj, tileZ+minZ))return false;
                if(hermeticBlockInvalid(tileX+xi, tileY+yj, tileZ+maxZ))return false;

                if(hermeticBlockInvalid(tileX+minX, tileY+yi, tileZ+zj))return false;
                if(hermeticBlockInvalid(tileX+maxX, tileY+yi, tileZ+zj))return false;
            }
        }
        return true;
    }

    private static boolean frameBlockInvalid(World world, int posX, int posY, int posZ, BlocksProperties.Casing casing){
        IBlockState state = getBlockState(world,posX,posY,posZ);
        if(state == null)
            return true;
        if(ignoreBlock(state))
            return false;
        if(!state.getProperties().containsKey(BlocksProperties.TYPE))
            return true;
        return state.getValue(BlocksProperties.TYPE) != casing;
    }

    private boolean hermeticBlockInvalid(int posX, int posY, int posZ){
        if(posX == this.pos.getX() && posY == this.pos.getY() && posZ == this.pos.getZ())
            return false;
        return hermeticBlockInvalid(world,posX,posY,posZ, this);
    }
    private static boolean hermeticBlockInvalid(World world, int posX, int posY, int posZ, TileEntityTankController controller){
        IBlockState state = getBlockState(world,posX,posY,posZ);
        if(state == null) return true;
        if(canSaveBus(getTile(world, posX, posY, posZ), controller)) return false;
        return !validHermeticBlock(state);
    }

    @Nullable
    private static IBlockState getBlockState(World world, int posX, int posY, int posZ){
        mutBP.setPos(posX,posY,posZ);
        if(!world.isBlockLoaded(mutBP))
            return null;
        return world.getBlockState(mutBP);
    }

    private static boolean canSaveBus(TileEntity tile, TileEntityTankController controller){
        if(tile instanceof TileEntityTankerBus){
            TileEntityTankerBus bus =  ((TileEntityTankerBus) tile);
            bus.setController(controller);
            controller.buses.add(bus);
            return true;
        }
        return false;
    }

    @Nullable
    private static TileEntity getTile(World world, int posX, int posY, int posZ){
        mutBP.setPos(posX,posY,posZ);
        if(!world.isBlockLoaded(mutBP))
            return null;
        return world.getTileEntity(mutBP);
    }

    private static boolean ignoreBlock(IBlockState state){
        Block block = state.getBlock();
        return block == IC2Additions.Blocks.tank_controller
                || block == IC2Additions.Blocks.tank_bus;
    }

    private static boolean validHermeticBlock(IBlockState state){
        Block block = state.getBlock();
        return block == IC2Additions.Blocks.tank_casing
                || block == IC2Additions.Blocks.pure_glass
                || block == IC2Additions.Blocks.tank_bus;
    }

    private static boolean isInvalidBlockFrame(IBlockState state){
        Block block = state.getBlock();
        return block != IC2Additions.Blocks.tank_casing
                && block != IC2Additions.Blocks.tank_controller
                && block != IC2Additions.Blocks.tank_bus;
    }

    private void clearBuses(){
        Iterator<TileEntityTankerBus> iterator = buses.iterator();
        while (iterator.hasNext()){
            iterator.next().setController(null);
            iterator.remove();
        }
    }

    @Override
    public int getColor() {
        if(tier == -1)
            return 0xFFFFFFFF;
        return BlocksProperties.Casing.getAsMeta(tier-1).getColor();
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        if(isTankBroken())
            return new IFluidTankProperties[0];
        return fluidTank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(isTankBroken())
            return 0;
        return fluidTank.fill(resource,doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if(isTankBroken())
            return null;
        return fluidTank.drain(resource,doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if(isTankBroken())
            return null;
        return fluidTank.drain(maxDrain,doDrain);
    }

    public enum Status{
        NULL("tile.tanker.status.not_init", 0),
        INITIALIZED("tile.tanker.status.all_ok", 1),
        BROKEN_FRAME("tile.tanker.status.frameless", 2),
        NOT_HERMETIC("tile.tanker.status.no_hermetic", 3);
        public final String langKey;
        public final int id;
        public static final Status[] map = new Status[Status.values().length];
        Status(String langKey, int id) {
            this.langKey = langKey;
            this.id = id;
        }

        public static Status getById(int id){
            if(id<0||id>=map.length)
                return NULL;
            return map[id];
        }

        static {
            for (Status status:
                    Status.values()) {
                map[status.id]=status;
            }
        }
    }

    public FluidTank getCurrentTank(){
        return isTankBroken() ? null: fluidTank;
    }

    public boolean isTankBroken(){
        return status != Status.INITIALIZED;
    }

    public AxisAlignedBB getTankerAABB(boolean isGas, float percent){
        int tileX = this.pos.getX(), tileY = this.pos.getY(), tileZ = this.pos.getZ();
        if(this.minX==0 &&this.maxX==0 &&this.minZ==0 &&this.maxZ==0 &&this.minY==0 &&this.maxY==0) return null;
        float minX = tileX+this.minX+0.1f;
        float maxX = tileX+this.maxX+0.9f;
        float minZ = tileZ+this.minZ+0.1f;
        float maxZ = tileZ+this.maxZ+0.9f;
        float minY = tileY+this.minY+1.01f;
        float maxY = tileY+this.maxY;
        if(!isGas)
            maxY -= (maxY-minY)*(1-percent);
        if(maxY == 0) maxY = minY;
        return new AxisAlignedBB(minX,minY,minZ,maxX,maxY,maxZ);
    }
}
