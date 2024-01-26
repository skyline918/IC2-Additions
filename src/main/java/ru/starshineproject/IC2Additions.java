package ru.starshineproject;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import ru.starshineproject.proxy.CommonProxy;

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

    @Mod.Instance
    public static IC2Additions instance;
    @SidedProxy(serverSide = "ru.starshineproject.proxy.CommonProxy", clientSide = "ru.starshineproject.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
        @Override
        public @Nonnull ItemStack createIcon() {
            return new ItemStack(Items.miner_1, 1);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        Registration.registerCommands(event);
    }

    @SuppressWarnings("unused")
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Items {
        public static final Item miner_1 = ObjectHolder();
        public static final Item miner_2 = ObjectHolder();
        public static final Item miner_3 = ObjectHolder();
        public static final Item miner_4 = ObjectHolder();
        public static final Item miner_5 = ObjectHolder();
        public static final Item pure_glass = ObjectHolder();
        public static final Item tank_casing = ObjectHolder();
        public static final Item tank_controller = ObjectHolder();
        public static final Item tank_bus = ObjectHolder();
    }

    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Blocks {
        public static final Block miner_1 = ObjectHolder();
        public static final Block miner_2 = ObjectHolder();
        public static final Block miner_3 = ObjectHolder();
        public static final Block miner_4 = ObjectHolder();
        public static final Block miner_5 = ObjectHolder();
        public static final Block pure_glass = ObjectHolder();
        public static final Block tank_casing = ObjectHolder();
        public static final Block tank_controller = ObjectHolder();
        public static final Block tank_bus = ObjectHolder();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T ObjectHolder() {
        return null;
    }

}