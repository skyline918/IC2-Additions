package ru.starshineproject;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import ru.starshineproject.config.IC2AConfig;
import ru.starshineproject.gui.GuiHandler;
import ru.starshineproject.tile.TileEntityMiner;

import javax.annotation.Nonnull;
import java.io.File;


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
    public static IC2AConfig config = new IC2AConfig(new File(Loader.instance().getConfigDir(), "IC2_Additions.cfg"));
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
        @Override
        public @Nonnull ItemStack createIcon() {
            return new ItemStack(Items.miner_1, 1);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config.sync();
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(IC2Additions.instance, new GuiHandler());

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        config.sync();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {

    }

    @SuppressWarnings("unused")
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Items {
        public static final Item miner_1 = ObjectHolder();
        public static final Item miner_2 = ObjectHolder();
        public static final Item miner_3 = ObjectHolder();
        public static final Item miner_4 = ObjectHolder();
        public static final Item miner_5 = ObjectHolder();
    }

    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Blocks {
        public static final Block miner_1 = ObjectHolder();
        public static final Block miner_2 = ObjectHolder();
        public static final Block miner_3 = ObjectHolder();
        public static final Block miner_4 = ObjectHolder();
        public static final Block miner_5 = ObjectHolder();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T ObjectHolder() {
        return null;
    }

}