package ru.starshineproject.config;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class IC2AConfig {
    Configuration configuration;
    public IC2AConfig(File file){
        configuration = new Configuration(file);
        Miner._conf = configuration;
    }

    public static final String CATEGORY_MAIN = "main";
    public static final String CATEGORY_MINER = "miner";

    //VALUES
    public static boolean ownershipEnabled = true;
    public static int maxIterationPerTick = 20;
    public static final Miner MINER_1 = new Miner(1).setDefault(true,1,80000,8,20,8);
    public static final Miner MINER_2 = new Miner(2).setDefault(true,2,600000,16,15,16);
    public static final Miner MINER_3 = new Miner(3).setDefault(true,3,1000000,24,10,32);
    public static final Miner MINER_4 = new Miner(4).setDefault(true,4,1000000,32,5,64);
    public static final Miner MINER_5 = new Miner(5).setDefault(true,5,1000000,40,2,128);
    public static final HashMap<Item, Set<Integer>> MINED_ORES = new HashMap<>();

    public void sync(){
        configuration.load();
        configuration.addCustomCategoryComment(CATEGORY_MAIN,"");
        ownershipEnabled = configuration.getBoolean("ownershipEnabled", CATEGORY_MAIN, true,"");
        maxIterationPerTick = configuration.getInt("maxIterationPerTick", CATEGORY_MAIN, 20,5,256,"");

        MINER_1.init();
        MINER_2.init();
        MINER_3.init();
        MINER_4.init();
        MINER_5.init();

        applyMinedOres(configuration.getStringList("minedOres",CATEGORY_MAIN,DEFAULT_MINED_ORES_EXAMPLE,""));
        if(configuration.hasChanged())
            configuration.save();
    }

    private static void applyMinedOres(String[] ores){
        MINED_ORES.clear();
        for (String ore: ores) {
            String[] par = ore.split("@");
            Item item = Item.getByNameOrId(par[0]);
            if(item == null) continue;
            MINED_ORES.putIfAbsent(item, new HashSet<>());
            if (par.length == 2) {
                MINED_ORES.get(item).add(Integer.valueOf(par[1]));
                continue;
            }
            MINED_ORES.get(item).add(0);
        }
    }

    public static Miner getMinerByTier(int tier){
        switch (tier){
            case 2: return MINER_2;
            case 3: return MINER_3;
            case 4: return MINER_4;
            case 5: return MINER_5;
            default: return MINER_1;
        }
    }

    public static class Miner {
        public static Configuration _conf;
        private final int number;
        public boolean enabled;
        public int tier;
        public int capacity;
        public int radius;
        public int ticksForEachBlock;
        public int energyToBlock;

        public Miner(int number){
            this.number = number;
        }

        public Miner setDefault(boolean enabled, int tier, int capacity, int radius, int ticksForEachBlock, int energyToBlock){
            this.enabled = enabled;
            this.tier = tier;
            this.capacity = capacity;
            this.radius = radius;
            this.ticksForEachBlock = ticksForEachBlock;
            this.energyToBlock = energyToBlock;
            return this;
        }

        public void init(){
            createCategory();
            apply();
        }

        public void createCategory(){
            _conf.addCustomCategoryComment(CATEGORY_MINER+"_"+ number,"");
        }
        public void apply(){
            this.enabled = _conf.getBoolean("enabled",CATEGORY_MINER+"_"+ number,this.enabled,"Whether the miner is enabled. It does not affect game registry. Existing miners will just stop");
            this.tier = _conf.getInt("energyTier",CATEGORY_MINER+"_"+ number, this.tier, 1,10,"Industrial Craft Tiers: 32eu/t is 1, 128 is 2, etc");
            this.capacity = _conf.getInt("capacity",CATEGORY_MINER+"_"+ number,this.capacity,8000,Integer.MAX_VALUE,"Industrial Craft Tiers: 32eu/t is 1, 128 is 2, etc");
            this.radius = _conf.getInt("radius",CATEGORY_MINER+"_"+ number,this.radius,1,120,"Radius of miner. It is square radius, not circular");
            this.ticksForEachBlock = _conf.getInt("ticksForEachBlock",CATEGORY_MINER+"_"+ number,this.ticksForEachBlock,1,360,"Capacity in IC2 Energy Units. More energy stored, more work can be done when chunks are unloaded");
            this.energyToBlock = _conf.getInt("energyToBlock",CATEGORY_MINER+"_"+ number,this.energyToBlock,2,1048576,"");
        }
    }
    public static final String[] DEFAULT_MINED_ORES_EXAMPLE = new String[]{
            "minecraft:gold_ore;",
            "minecraft:iron_ore",
            "minecraft:coal_ore",
            "minecraft:lapis_ore",
            "minecraft:diamond_ore",
            "minecraft:redstone_ore",
            "minecraft:emerald_ore",
            "minecraft:quartz_ore",
            "ic2:blockmetal@0",
            "ic2:blockmetal@1",
            "ic2:blockmetal@2",
            "ic2:blockmetal@3"
    };
}
