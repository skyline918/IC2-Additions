import block.BlockMiner;
import config.IC2AdditionsConfig;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import tile.TileEntityMiner;

public class Registration {



    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registerBlock("miner_1", new BlockMiner(IC2AdditionsConfig.miner_1), registry);
        registerBlock("miner_2", new BlockMiner(IC2AdditionsConfig.miner_2), registry);
        registerBlock("miner_3", new BlockMiner(IC2AdditionsConfig.miner_3), registry);
        registerBlock("miner_4", new BlockMiner(IC2AdditionsConfig.miner_4), registry);
        registerBlock("miner_5", new BlockMiner(IC2AdditionsConfig.miner_5), registry);
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(new ItemBlock(IC2Additions.Blocks.MINER_1), registry);
        registerItem(new ItemBlock(IC2Additions.Blocks.MINER_2), registry);
        registerItem(new ItemBlock(IC2Additions.Blocks.MINER_3), registry);
        registerItem(new ItemBlock(IC2Additions.Blocks.MINER_4), registry);
        registerItem(new ItemBlock(IC2Additions.Blocks.MINER_5), registry);
    }

    public static void registerTiles() {
        GameRegistry.registerTileEntity(TileEntityMiner.class, new ResourceLocation(IC2Additions.MOD_ID, "miner"));
    }

    private static void registerItem(Item item, IForgeRegistry<Item> registry) {
        registry.register(item);

        //noinspection ConstantConditions
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString()));
    }

    private static void registerBlock(String name, Block block, IForgeRegistry<Block> registry) {
        registry.register(block.setTranslationKey(name).setRegistryName(IC2Additions.MOD_ID, name));

    }

}
