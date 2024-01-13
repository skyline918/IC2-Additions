package ru.starshineproject.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.container.ContainerMiner;

public class GuiMiner extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IC2Additions.MOD_ID,"textures/gui/miner.png");

    public static final int id = 1005;

    public GuiMiner(InventoryPlayer playerInv, IInventory inventory) {
        super(new ContainerMiner(playerInv, inventory));
        this.ySize = 114 + 6 * 18;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 6 * 18 + 17);
        this.drawTexturedModalRect(i, j + 6 * 18 + 17, 0, 126, this.xSize, 96);
    }
}
