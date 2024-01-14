package ru.starshineproject.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.container.ContainerMiner;
import ru.starshineproject.tile.TileEntityMiner;

import java.awt.*;

public class GuiMiner extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IC2Additions.MOD_ID,"textures/gui/miner.png");
//    private static final int FONT_COLOR = new Color(128,128,255).getRGB();

    public static final int id = 1005;
    private final TileEntityMiner miner;

    public GuiMiner(InventoryPlayer playerInv, TileEntityMiner miner) {
        super(new ContainerMiner(playerInv, miner));
        this.ySize = 114 + 6 * 18;
        this.miner = miner;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int FONT_COLOR = new Color(120,255,255).getRGB();

        fontRenderer.drawString(String.format("Miner T%d", miner.ic2EnergySink.getSinkTier()), 8, 6, 4210752);
        fontRenderer.drawString(String.format("%d/%d EU",
                Math.round(miner.ic2EnergySink.getEnergyStored()),
                Math.round(miner.ic2EnergySink.getCapacity())
        ), 10, 20, FONT_COLOR);
        fontRenderer.drawString(String.format("IC2 Tier: %d", miner.ic2EnergySink.getSinkTier()), 10, 30, FONT_COLOR);
        fontRenderer.drawString(String.format("Owner: %s", miner.ownerName), 10, 40, FONT_COLOR);

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
