package ru.starshineproject.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.block.BlocksProperties;
import ru.starshineproject.config.IC2AdditionsConfig;

import javax.annotation.Nullable;

public class TileEntityTankController extends TileEntity implements ITickable, IColored {

    public int tier = -1;
    public Status status = Status.NULL;

    private int minX = 0, minZ = 0, minY = 0;
    private int maxX = 0, maxZ = 0, maxY = 0;

    public TileEntityTankController(){
    }
    @Override
    public void update() {

    }

    @Override
    public void onLoad() {
        validateTanker();
    }

    private void validateTanker(){
        if(!initTankSize()){
            status = Status.NULL;
            return;
        }
        if(!hasFrame()){
            status = Status.BROKEN_FRAME;
            return;
        }
        if(!isHermetic()){
            status = Status.NOT_HERMETIC;
            return;
        }
        status = Status.INITIALIZED;
    }

    static BlockPos.MutableBlockPos mutBP = new BlockPos.MutableBlockPos();
    private boolean initTankSize(){
        int tileX = this.pos.getX(), tileY = this.pos.getY(), tileZ = this.pos.getZ();

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

        return maxX - minX >= 3 && maxY - minY >= 3 && maxZ - minZ >= 3;
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
        return hermeticBlockInvalid(world,posX,posY,posZ);
    }
    private static boolean hermeticBlockInvalid(World world, int posX, int posY, int posZ){
        IBlockState state = getBlockState(world,posX,posY,posZ);
        if(state == null)
            return true;
        return !validHermeticBlock(state);
    }

    @Nullable
    private static IBlockState getBlockState(World world, int posX, int posY, int posZ){
        mutBP.setPos(posX,posY,posZ);
        if(!world.isBlockLoaded(mutBP))
            return null;
        return world.getBlockState(mutBP);
    }

    private static boolean ignoreBlock(IBlockState state){
        Block block = state.getBlock();
        return block == IC2Additions.Blocks.tank_controller;
        //      || block == IC2Additions.Blocks.tank_input;
        //      || block == IC2Additions.Blocks.tank_output;
    }

    private static boolean validHermeticBlock(IBlockState state){
        Block block = state.getBlock();
        return block == IC2Additions.Blocks.tank_casing
                || block == IC2Additions.Blocks.pure_glass;
        //      || block == IC2Additions.Blocks.tank_input;
        //      || block == IC2Additions.Blocks.tank_output;
    }

    private static boolean isInvalidBlockFrame(IBlockState state){
        Block block = state.getBlock();
        return block != IC2Additions.Blocks.tank_casing
                && block != IC2Additions.Blocks.tank_controller;
        //      || block == IC2Additions.Blocks.tank_input;
        //      || block == IC2Additions.Blocks.tank_output;
    }

    @Override
    public int getColor() {
        if(tier == -1)
            return 0xFFFFFFFF;
        return BlocksProperties.Casing.getAsMeta(tier-1).getColor();
    }

    public enum Status{
        NULL("tile.tanker.status.not_init"),
        INITIALIZED("tile.tanker.status.all_ok"),
        BROKEN_FRAME("tile.tanker.status.frameless"),
        NOT_HERMETIC("tile.tanker.status.no_hermetic");
        public final String langKey;

        Status(String langKey) {
            this.langKey = langKey;
        }
    }
}
