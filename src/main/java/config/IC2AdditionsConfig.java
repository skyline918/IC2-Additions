package config;

import net.minecraftforge.common.config.Config;

@Config(modid = "ic2additions", name = "IC2 Additions")
public class IC2AdditionsConfig {

    public static Miner miner_1 = new Miner(1);
    public static Miner miner_2 = new Miner(2);
    public static Miner miner_3 = new Miner(3);
    public static Miner miner_4 = new Miner(4);
    public static Miner miner_5 = new Miner(5);

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

        private void initDefaultByTier(int tier) {
            switch (tier) {
                default:
                    this.tier = 1;
                    this.capacity = 80000;
                    this.radius = 8;
                    this.ticksForEachBlock = 20;
                    break;
                case 2:
                    this.tier = 2;
                    this.capacity = 600000;
                    this.radius = 16;
                    this.ticksForEachBlock = 15;
                    break;
                case 3:
                    this.tier = 3;
                    this.capacity = 1000000;
                    this.radius = 24;
                    this.ticksForEachBlock = 10;
                    break;
                case 4:
                    this.tier = 4;
                    this.capacity = 1000000;
                    this.radius = 32;
                    this.ticksForEachBlock = 5;
                    break;
                case 5:
                    this.tier = 5;
                    this.capacity = 1000000;
                    this.radius = 40;
                    this.ticksForEachBlock = 2;
                    break;
            }
        }
    }

}
