package ru.starshineproject.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;
import ru.starshineproject.IC2Additions;
import ru.starshineproject.container.ContainerTanker;
import ru.starshineproject.tile.tanker.TileEntityTankController;

import java.awt.*;

public class GuiTanker extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(IC2Additions.MOD_ID,"textures/gui/tanker.png");
    private static final int FONT_COLOR = new Color(120,255,255).getRGB();
    private static final int FONT_COLOR_STATUS = new Color(160,255,255).getRGB();
    private final TileEntityTankController tankController;
    public static final int id = 1006;
    public GuiTanker(InventoryPlayer playerInv, TileEntityTankController tankController) {
        super(new ContainerTanker(playerInv,tankController));
        this.ySize = 114 + 6 * 18;
        this.tankController = tankController;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("gui.tanker.name"), 8, 6, 4210752);
        renderFluid(10,20);
        fontRenderer.drawString(I18n.format("gui.tanker.fluid", tankController.getFluidName()), 30, 20, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.tanker.amount", tankController.getFluidAmount()), 30, 30, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.tanker.volume", tankController.getVolume()), 30, 40, FONT_COLOR);
        fontRenderer.drawString(I18n.format("gui.tanker.status"), 10, 83, FONT_COLOR_STATUS);
        fontRenderer.drawString(tankController.status.toLocalizedString(), 13, 93, tankController.status.color);
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

    public void renderFluid(double x, double y){

        if(tankController == null)
            return;
        FluidTank tank = tankController.getCurrentTank();
        if(tank == null)
            return;
        FluidStack fluidStack = tank.getFluid();
        if(fluidStack == null)
            return;
        int color = fluidStack.getFluid().getColor();
        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill().toString());

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        bufferbuilder.pos(x, y+16, this.zLevel).tex(minU,maxV).lightmap(0,0).color(r,g,b,a).endVertex();
        bufferbuilder.pos(x+16, y+16, this.zLevel).tex(maxU,maxV).lightmap(0,0).color(r,g,b,a).endVertex();
        bufferbuilder.pos(x+16, y, this.zLevel).tex(maxU,minV).lightmap(0,0).color(r,g,b,a).endVertex();
        bufferbuilder.pos(x, y, this.zLevel).tex(minU,minV).lightmap(0,0).color(r,g,b,a).endVertex();
        tessellator.draw();

    }
}
