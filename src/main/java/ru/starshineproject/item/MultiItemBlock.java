package ru.starshineproject.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class MultiItemBlock extends ItemBlock {
    public MultiItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }
    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
