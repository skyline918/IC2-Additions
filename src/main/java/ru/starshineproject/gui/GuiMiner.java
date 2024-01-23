package ru.starshineproject.gui;

import ic2.api.energy.EnergyNet;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.container.ContainerMiner;
import ru.starshineproject.tile.TileEntityMiner;

import java.awt.*;
import java.text.DecimalFormat;

public class GuiMiner extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IC2Additions.MOD_ID,"textures/gui/miner.png");
    private static final int FONT_COLOR = new Color(120,255,255).getRGB();

    public static final int id = 1005;
    private final TileEntityMiner miner;
    final DecimalFormat df = new DecimalFormat("#.0");

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

        fontRenderer.drawString(I18n.format("gui.miner.tier", miner.config.level), 8, 6, 4210752);
        fontRenderer.drawString(String.format("%d kEU", Math.round(miner.ic2EnergySink.getEnergyStored() / 1000)), 10, 20, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.miner.sinkTier", EnergyNet.instance.getPowerFromTier(miner.ic2EnergySink.getSinkTier())), 10, 30, FONT_COLOR);
        fontRenderer.drawString(this.percents(), 10, 85, FONT_COLOR);
        fontRenderer.drawString(miner.status.toLocalizedString(), 10, 95, miner.status.color);

    }

    protected String percents() {
        int radius = this.miner.config.radius;
        int diameter = (2 * radius + 1) ;
        double total = Math.max(1, diameter * diameter * this.miner.getPos().getY());
        IC2Additions.logger.info("{}/{} ({})", this.miner.totalScanned, total, radius);
        return String.format("%s%%", df.format(((double) this.miner.totalScanned) / total * 100));
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
