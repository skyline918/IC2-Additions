package ru.starshineproject.config;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Config(modid = "ic2additions", name = "IC2 Additions")
public class IC2AdditionsConfig {
    public static boolean ownershipEnabled = false;
    @Config.RangeInt(min = 20, max = 100)
    public static int maxIterationPerTick = 20;

    public static Miner miner_1 = new Miner(1);
    public static Miner miner_2 = new Miner(2);
    public static Miner miner_3 = new Miner(3);
    public static Miner miner_4 = new Miner(4);
    public static Miner miner_5 = new Miner(5);
    public static String minedOres =
            "\n\t\tminecraft:gold_ore;" +
            "\n\t\tminecraft:iron_ore;" +
            "\n\t\tminecraft:coal_ore;" +
            "\n\t\tminecraft:lapis_ore;" +
            "\n\t\tminecraft:diamond_ore;" +
            "\n\t\tminecraft:redstone_ore;" +
            "\n\t\tminecraft:emerald_ore;" +
            "\n\t\tminecraft:quartz_ore;" +
            "\n\t\tminecraft:ic2:blockmetal@0;"+
            "\n\t\tminecraft:ic2:blockmetal@1;"+
            "\n\t\tminecraft:ic2:blockmetal@2;"+
            "\n\t\tminecraft:ic2:blockmetal@3;";

    public static class Miner {

        public Miner(int defaultTier) {
            initDefaultByTier(defaultTier);
        }

        @Config.Comment("Whether the miner is enabled. It does not affect game registry. Existing miners will just stop")
        public boolean enabled = true;

        @Config.RangeInt(min = 1, max = 10)
        @Config.Comment("Industrial Craft Tiers: 32eu/t is 1, 128 is 2, etc")
        public int tier;

        @Config.RangeInt(min = 8000)
        @Config.Comment("Capacity in IC2 Energy Units. More energy stored, more work can be done when chunks are unloaded")
        public int capacity;

        @Config.RangeInt(min = 1, max = 120)
        @Config.Comment("Radius of miner. It is square radius, not circular")
        public int radius;

        @Config.RangeInt(min = 1, max = 360)
        public int ticksForEachBlock;

        @Config.RangeInt(min = 2, max = 1048576)
        public int energyToBlock;

        private void initDefaultByTier(int tier) {
            switch (tier) {
                default:
                    this.tier = 1;
                    this.capacity = 80000;
                    this.radius = 8;
                    this.ticksForEachBlock = 20;
                    this.energyToBlock = 8;
                    break;
                case 2:
                    this.tier = 2;
                    this.capacity = 600000;
                    this.radius = 16;
                    this.ticksForEachBlock = 15;
                    this.energyToBlock = 16;
                    break;
                case 3:
                    this.tier = 3;
                    this.capacity = 1000000;
                    this.radius = 24;
                    this.ticksForEachBlock = 10;
                    this.energyToBlock = 32;
                    break;
                case 4:
                    this.tier = 4;
                    this.capacity = 1000000;
                    this.radius = 32;
                    this.ticksForEachBlock = 5;
                    this.energyToBlock = 64;
                    break;
                case 5:
                    this.tier = 5;
                    this.capacity = 1000000;
                    this.radius = 40;
                    this.ticksForEachBlock = 2;
                    this.energyToBlock = 128;
                    break;
            }
        }

        @Override
        public String toString() {
            return "Miner{" +
                    "tier=" + tier +
                    ", capacity=" + capacity +
                    ", radius=" + radius +
                    ", ticksForEachBlock=" + ticksForEachBlock +
                    ", energyToBlock=" + energyToBlock +
                    '}';
        }
    }

    public static HashMap<Item,ArrayList<Integer>> buildOreMap(){
        HashMap<Item,ArrayList<Integer>> map = new HashMap<>();

        String[] ores = minedOres.replace("\n","").replace("\t","").split(";");
        for (String ore : ores) {
            String[] par = ore.split("@");
            Item item = Item.getByNameOrId(par[0]);
            map.putIfAbsent(item, new ArrayList<>());
            if (par.length == 2) {
                map.get(item).add(Integer.valueOf(par[1]));
                continue;
            }
            map.get(item).add(0);
        }

        return map;
    }

    public static Miner getMinerConfig(int tier){
        switch (tier){
            case 2: return miner_2;
            case 3: return miner_3;
            case 4: return miner_4;
            case 5: return miner_5;
            default: return miner_1;
        }
    }
}
