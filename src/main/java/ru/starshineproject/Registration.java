package ru.starshineproject;

import ru.starshineproject.block.BlockMiner;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import ru.starshineproject.config.IC2AdditionsConfig;
import ru.starshineproject.tile.TileEntityMiner;

@Mod.EventBusSubscriber
public class Registration {

    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registerBlock("miner_1", new BlockMiner(IC2AdditionsConfig.miner_1), registry);
        registerBlock("miner_2", new BlockMiner(IC2AdditionsConfig.miner_2), registry);
        registerBlock("miner_3", new BlockMiner(IC2AdditionsConfig.miner_3), registry);
        registerBlock("miner_4", new BlockMiner(IC2AdditionsConfig.miner_4), registry);
        registerBlock("miner_5", new BlockMiner(IC2AdditionsConfig.miner_5), registry);

        GameRegistry.registerTileEntity(TileEntityMiner.class, new ResourceLocation(IC2Additions.MOD_ID, "miner"));

    }

    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem("miner_1", new ItemBlock(IC2Additions.Blocks.miner_1), registry);
        registerItem("miner_2", new ItemBlock(IC2Additions.Blocks.miner_2), registry);
        registerItem("miner_3", new ItemBlock(IC2Additions.Blocks.miner_3), registry);
        registerItem("miner_4", new ItemBlock(IC2Additions.Blocks.miner_4), registry);
        registerItem("miner_5", new ItemBlock(IC2Additions.Blocks.miner_5), registry);
    }

    private static void registerItem(String name, Item item, IForgeRegistry<Item> registry) {
        registry.register(item
                .setTranslationKey(name)
                .setRegistryName(IC2Additions.MOD_ID, name)
                .setCreativeTab(IC2Additions.CREATIVE_TAB)
        );

        //noinspection ConstantConditions
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString()));
    }

    private static void registerBlock(String name, Block block, IForgeRegistry<Block> registry) {
        registry.register(block.setTranslationKey(name).setRegistryName(IC2Additions.MOD_ID, name).setCreativeTab(IC2Additions.CREATIVE_TAB));
    }


}
