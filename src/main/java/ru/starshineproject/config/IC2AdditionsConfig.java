package ru.starshineproject.config;

import net.minecraftforge.common.config.Config;


@Config(modid = "ic2additions", name = "IC2 Additions")
public class IC2AdditionsConfig {

    public static boolean ownershipEnabled = false;

    public static String[] blocksToMine = new String[3];
    static {
        blocksToMine[0] = "minecraft:iron_ore";
        blocksToMine[1] = "minecraft:gold_ore";
        blocksToMine[2] = "minecraft:diamond_ore";
    }

    public static Miner miner_1 = new Miner(1);
    public static Miner miner_2 = new Miner(2);
    public static Miner miner_3 = new Miner(3);
    public static Miner miner_4 = new Miner(4);
    public static Miner miner_5 = new Miner(5);

    public static Miner getConfigFromTier(int tier) {
        switch (tier) {
            case 2: return miner_2;
            case 3: return miner_3;
            case 4: return miner_4;
            case 5: return miner_5;
            default: return miner_1;
        }
    }

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

        @Config.RangeInt(min = 1, max = 360000)
        public int msToScan;

        @Config.RangeInt(min = 1, max = 360000)
        public int msToMine;

        @Config.RangeInt(min = 1, max = 360000)
        public int requiredEnergyToScan;

        @Config.RangeInt(min = 1, max = 360000)
        public int requiredEnergyToMine;

        @Config.RangeInt(min = 1, max = 360000)
        public int maxScansPerUpdate = 50;

        private void initDefaultByTier(int tier) {
            switch (tier) {
                default:
                    this.tier = 1;
                    this.capacity = 80000;
                    this.radius = 8;
                    this.msToScan = 1000;
                    this.msToMine = 5000;
                    this.requiredEnergyToScan = 50;
                    this.requiredEnergyToMine = 500;
                    break;
                case 2:
                    this.tier = 2;
                    this.capacity = 600000;
                    this.radius = 16;
                    this.msToScan = 1000;
                    this.msToMine = 5000;
                    this.requiredEnergyToScan = 50;
                    this.requiredEnergyToMine = 500;
                    break;
                case 3:
                    this.tier = 3;
                    this.capacity = 1000000;
                    this.radius = 24;
                    this.msToScan = 1000;
                    this.msToMine = 5000;
                    this.requiredEnergyToScan = 50;
                    this.requiredEnergyToMine = 500;
                    break;
                case 4:
                    this.tier = 4;
                    this.capacity = 1000000;
                    this.radius = 32;
                    this.msToScan = 1000;
                    this.msToMine = 5000;
                    this.requiredEnergyToScan = 50;
                    this.requiredEnergyToMine = 500;
                    break;
                case 5:
                    this.tier = 5;
                    this.capacity = 1000000;
                    this.radius = 40;
                    this.msToScan = 1000;
                    this.msToMine = 5000;
                    this.requiredEnergyToScan = 50;
                    this.requiredEnergyToMine = 500;
                    break;
            }
        }
    }

}
