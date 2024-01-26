package ru.starshineproject.config;

import net.minecraftforge.common.config.Config;


@Config(modid = "ic2additions", name = "IC2 Additions")
public class IC2AdditionsConfig {

    public static boolean ownershipEnabled = false;

    public static Integer[] dimBlacklist = new Integer[0];
    public static String[] blocksToMine = new String[0];

    public static Miner miner_1 = new Miner(1);
    public static Miner miner_2 = new Miner(2);
    public static Miner miner_3 = new Miner(3);
    public static Miner miner_4 = new Miner(4);
    public static Miner miner_5 = new Miner(5);

    @Config.RangeInt(min = 3, max = 50)
    public static int maxTankerSize = 20;
    @Config.RangeInt(min = 1, max = 1000000)
    public static int millibucketsPerBlock = 1000;
    @Config.RangeInt(min = 1, max = 10)
    public static int tankerCasingMultiplier = 1;

    public static Miner getConfigFromLevel(int tier) {
        switch (tier) {
            case 2: return miner_2;
            case 3: return miner_3;
            case 4: return miner_4;
            case 5: return miner_5;
            default: return miner_1;
        }
    }

    public static boolean dimIsBlacklisted(int dim) {
        for (int i = 0; i < IC2AdditionsConfig.dimBlacklist.length; i++) {
            if (IC2AdditionsConfig.dimBlacklist[i] == dim) {
                return true;
            }
        }

        return false;
    }

    public static class Miner {

        public Miner(int defaultLevel) {
            initDefaultByLevel(defaultLevel);
        }

        @Config.Ignore
        public int level;

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

        private void initDefaultByLevel(int level) {
            this.level = level;
            switch (level) {
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
