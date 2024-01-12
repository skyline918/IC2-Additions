import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;


@Mod(
        modid = IC2Additions.MOD_ID,
        name = IC2Additions.MOD_NAME,
        version = IC2Additions.VERSION
)
public class IC2Additions {
    public static final String MOD_ID = "ic2additions";
    public static final String MOD_NAME = "IC2 Additions";
    public static final String VERSION = "1.0";
    public static Logger logger;

    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Block> event) {
        Registration.registerBlocks(event);
        Registration.registerTiles();
    }

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        Registration.registerItems(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {

    }

    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Items {
//        public static final Item
    }

    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Blocks {
        public static final Block MINER_1 = ObjectHolder();
        public static final Block MINER_2 = ObjectHolder();
        public static final Block MINER_3 = ObjectHolder();
        public static final Block MINER_4 = ObjectHolder();
        public static final Block MINER_5 = ObjectHolder();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T ObjectHolder() {
        return null;
    }

}