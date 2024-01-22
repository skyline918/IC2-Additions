package ru.starshineproject;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import ru.starshineproject.block.BlockMiner;
import ru.starshineproject.block.BlockPureGlass;
import ru.starshineproject.block.BlockTankCasing;
import ru.starshineproject.command.CommandReloadConfig;
import ru.starshineproject.config.IC2AdditionsConfig;
import ru.starshineproject.item.ItemMiner;
import ru.starshineproject.item.MultiItemBlock;
import ru.starshineproject.tile.TileEntityMiner;

import java.util.ArrayList;
import java.util.List;

import static ru.starshineproject.tile.TileEntityMiner.VALID_ORES;

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
        registerBlock("pure_glass", new BlockPureGlass(), registry);
        registerBlock("tank_casing", new BlockTankCasing(), registry);

        GameRegistry.registerTileEntity(TileEntityMiner.class, new ResourceLocation(IC2Additions.MOD_ID, "miner"));

    }

    public static void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandReloadConfig());

    }

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(IC2Additions.MOD_ID)) {
            ConfigManager.sync(IC2Additions.MOD_ID, Config.Type.INSTANCE);
            discoverOres();
        }
    }

    public static void discoverOres() {
        VALID_ORES.clear();
        if (IC2AdditionsConfig.blocksToMine.length == 0) {
            List<String> newOres = new ArrayList<>();
            for (String oreName : OreDictionary.getOreNames()) {
                if (oreName.startsWith("ore")) {
                    for (ItemStack ore : OreDictionary.getOres(oreName)) {
                        if (ore.getItem() instanceof ItemBlock) {
                            if (ore.getItem().getRegistryName() == null) continue;

                            VALID_ORES.put(ore.getItem(), ore.getMetadata());
                            newOres.add(String.format("%s.%d", ore.getItem().getRegistryName().toString(), ore.getMetadata()));
                        }
                    }
                }
            }
            IC2AdditionsConfig.blocksToMine = newOres.toArray(new String[0]);
        }
        for (String s : IC2AdditionsConfig.blocksToMine) {
            try {
                String[] arr = s.split("\\.");
                if (arr.length != 2) continue;

                String id = arr[0];
                String meta = arr[1];

                Item item = Item.getByNameOrId(id);
                if (item == null) throw new IllegalArgumentException("Not found in registry");

                VALID_ORES.put(item, Integer.valueOf(meta));
            } catch (Exception err) {
                IC2Additions.logger.warn("Failed to register miner ore '{}'. Error: {}", s, err.getMessage());
            }

        }

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem("miner_1", new ItemMiner((BlockMiner) IC2Additions.Blocks.miner_1), registry);
        registerItem("miner_2", new ItemMiner((BlockMiner) IC2Additions.Blocks.miner_2), registry);
        registerItem("miner_3", new ItemMiner((BlockMiner) IC2Additions.Blocks.miner_3), registry);
        registerItem("miner_4", new ItemMiner((BlockMiner) IC2Additions.Blocks.miner_4), registry);
        registerItem("miner_5", new ItemMiner((BlockMiner) IC2Additions.Blocks.miner_5), registry);
        registerBlockSubItem("pure_glass", IC2Additions.Blocks.pure_glass, registry);
        registerBlockSubItem("tank_casing", IC2Additions.Blocks.tank_casing, registry);
    }

    private static void registerBlockSubItem(String name, Block block, IForgeRegistry<Item> registry) {
        MultiItemBlock itemBlock = new MultiItemBlock(block);
        itemBlock
                .setTranslationKey(name)
                .setRegistryName(IC2Additions.MOD_ID, name)
                .setCreativeTab(IC2Additions.CREATIVE_TAB);
        registry.register(itemBlock);

        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && itemBlock.getRegistryName() != null) {
            ModelResourceLocation mrl = new ModelResourceLocation(itemBlock.getRegistryName().toString());

            for (IBlockState state : block.getBlockState().getValidStates()){
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),block.getMetaFromState(state),mrl);
            }
        }
    }

    private static void registerItem(String name, Item item, IForgeRegistry<Item> registry) {
        registry.register(item
                .setTranslationKey(name)
                .setRegistryName(IC2Additions.MOD_ID, name)
                .setCreativeTab(IC2Additions.CREATIVE_TAB)
        );

        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && item.getRegistryName() != null)
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName().toString()));
    }

    private static void registerBlock(String name, Block block, IForgeRegistry<Block> registry) {
        registry.register(block.setTranslationKey(name).setRegistryName(IC2Additions.MOD_ID, name).setCreativeTab(IC2Additions.CREATIVE_TAB));
    }


}
