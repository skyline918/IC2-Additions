package ru.starshineproject.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.container.ContainerMiner;
import ru.starshineproject.tile.TileEntityMiner;

import java.awt.*;

public class GuiMiner extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IC2Additions.MOD_ID,"textures/gui/miner.png");
    private static final int FONT_COLOR = new Color(120,255,255).getRGB();
    private static final int FONT_COLOR_STATUS = new Color(160,255,255).getRGB();

    public static final int id = 1005;
    private final TileEntityMiner miner;

    public GuiMiner(InventoryPlayer playerInv, TileEntityMiner miner) {
        super(new ContainerMiner(playerInv, miner));
        this.ySize = 114 + 6 * 18;
        this.miner = miner;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        fontRenderer.drawString(I18n.format("gui.miner.tier", miner.ic2EnergySink.getSinkTier()), 8, 6, 4210752);
        fontRenderer.drawString(String.format("%dk/%dk EU",
                Math.round(miner.ic2EnergySink.getEnergyStored() / 1000),
                Math.round(miner.ic2EnergySink.getCapacity() / 1000)
        ), 10, 20, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.miner.sinkTier", miner.ic2EnergySink.getSinkTier()), 10, 30, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.miner.owner", miner.ownerName), 10, 40, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.miner.height", miner.cursorY, miner.getPos().getY() + miner.cursorY), 10, 50, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.miner.scanned", miner.totalScanned), 10, 60, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.miner.mined", miner.totalMined), 10, 70, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.miner.status", miner.status.toLocalizedString()), 10, 113, FONT_COLOR_STATUS);

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
