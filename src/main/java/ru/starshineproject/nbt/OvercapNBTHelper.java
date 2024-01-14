package ru.starshineproject.nbt;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class OvercapNBTHelper {
    public static NBTTagCompound saveAllItems(@Nonnull NBTTagCompound tag, NonNullList<ItemStack> list){
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < list.size(); ++i)
        {
            ItemStack itemstack = list.get(i);

            if (!itemstack.isEmpty())
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttagcompound.removeTag("Count");
                nbttagcompound.setShort("Count",(short) itemstack.getCount());
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        if (!nbttaglist.isEmpty())
            tag.setTag("Items", nbttaglist);

        return tag;
    }

    public static void loadAllItems(NBTTagCompound tag, NonNullList<ItemStack> list)
    {
        NBTTagList nbttaglist = tag.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < list.size())
            {
                list.set(j, itemStackFromCustomTag(nbttagcompound));
            }
        }
    }

    public static ItemStack itemStackFromCustomTag(NBTTagCompound tag){
        Item item = tag.hasKey("id", 8) ? Item.getByNameOrId(tag.getString("id")) : Items.AIR;
        int stackSize = tag.getShort("Count");
        int itemDamage = Math.max(0, tag.getShort("Damage"));
        NBTTagCompound itemStackTag = tag.hasKey("ForgeCaps") ? tag.getCompoundTag("ForgeCaps") : null;

        return new ItemStack(item,stackSize,itemDamage,itemStackTag);
    }
}
