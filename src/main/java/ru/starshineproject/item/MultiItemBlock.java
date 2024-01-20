package ru.starshineproject.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import ru.starshineproject.block.IPropertyValueName;

public class MultiItemBlock extends ItemBlock {
    public MultiItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
    }
    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        String translationKey = super.getTranslationKey(stack);
        if(this.block instanceof IPropertyValueName)
            translationKey += "."+((IPropertyValueName)block).getValueName(stack);
        return translationKey;
    }
}
