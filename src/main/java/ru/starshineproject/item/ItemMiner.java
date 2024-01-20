package ru.starshineproject.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import ru.starshineproject.block.BlockMiner;
import ru.starshineproject.config.IC2AdditionsConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemMiner extends ItemBlock {

    private final IC2AdditionsConfig.Miner minerConfig;
    private String langCache1;
    private String langCache2;
    private String langCache3;

    public ItemMiner(Block p_i45328_1_, @Nonnull IC2AdditionsConfig.Miner minerConfig) {
        super(p_i45328_1_);
        this.minerConfig = minerConfig;
    }
    public ItemMiner(BlockMiner miner){
        this(miner,miner.getConfig());
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (langCache2 == null) {
            langCache2 = new TextComponentTranslation("item.miner.tooltip").getFormattedText();
        }
        if (langCache1 == null) {
            langCache1 = new TextComponentTranslation("item.miner.tooltip-radius", minerConfig.radius).getFormattedText();
        }
        if (langCache3 == null && IC2AdditionsConfig.dimBlacklist.length > 0) {
            ArrayList<String> arr = new ArrayList<>(IC2AdditionsConfig.dimBlacklist.length);
            for (Integer integer : IC2AdditionsConfig.dimBlacklist) {
                try {
                    String name = DimensionType.getById(integer).getName();
                    arr.add(TextFormatting.RED + name);
                } catch (Exception ignored) {
                }
            }

            if (arr.size() > 0) {
                langCache3 = new TextComponentTranslation("item.miner.tooltip-blacklisted-dims", String.join(", ", arr)).getFormattedText();
            } else {
                langCache3 = "";
            }
        }

        tooltip.add(langCache2);
        tooltip.add(langCache1);
        if (!langCache3.isEmpty()) tooltip.add(langCache3);
    }
}
