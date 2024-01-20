package ru.starshineproject.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import ru.starshineproject.block.BlockMiner;
import ru.starshineproject.config.IC2AdditionsConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMiner extends ItemBlock {

    private final IC2AdditionsConfig.Miner minerConfig;
    private String langCache1;
    private String langCache2;

    public ItemMiner(Block p_i45328_1_, @Nonnull IC2AdditionsConfig.Miner minerConfig) {
        super(p_i45328_1_);
        this.minerConfig = minerConfig;
    }
    public ItemMiner(BlockMiner miner){
        this(miner,miner.getConfig());
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World p_77624_2_, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, p_77624_2_, tooltip, flag);
        if (langCache2 == null) {
            langCache2 = new TextComponentTranslation("item.miner.tooltip").getFormattedText();
        }
        if (langCache1 == null) {
            langCache1 = new TextComponentTranslation("item.miner.tooltip-radius", minerConfig.radius).getFormattedText();
        }

        tooltip.add(langCache2);
        tooltip.add(langCache1);
    }
}
