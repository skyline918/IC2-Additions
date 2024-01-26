package ru.starshineproject.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import org.lwjgl.opengl.GL11;

public class TankerFluidRender {
    public static void render(AxisAlignedBB box, float gasAlpha, Fluid fluid, int light){
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        GlStateManager.translate(box.minX - renderPosX, box.minY - renderPosY, box.minZ - renderPosZ);
        box = box.offset(-box.minX, -box.minY, -box.minZ);
        GlStateManager.scale(1F, 1F, 1F);

        renderCube(box, fluid, light, gasAlpha);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    private static void renderCube(AxisAlignedBB box, Fluid fluid, int light, float gasAlpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int color = fluid.getColor();
        float a = (color >> 24 & 0xFF) / 255f;
        float r = (color >> 16 & 0xFF) / 255f;
        float g = (color >> 8 & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        if(gasAlpha != -1)
            a *= gasAlpha;

        int lightx = light >> 0x10 & 0xFFFF;
        int lighty = light & 0xFFFF;

        GL11.glColor4f(r,g,b,a);
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();


        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        double ix = box.minX;
        double iy = box.minY;
        double iz = box.minZ;
        double ax = box.maxX;
        double ay = box.maxY;
        double az = box.maxZ;
        
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        //back
        buffer.pos(ix, ay, iz).tex(minU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, ay, iz).tex(maxU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, iy, iz).tex(maxU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, iy, iz).tex(minU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();

        //right
        buffer.pos(ax, iy, iz).tex(minU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, ay, iz).tex(maxU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, ay, az).tex(maxU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, iy, az).tex(minU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();

        //bottom
        buffer.pos(ix, iy, iz).tex(minU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, iy, iz).tex(maxU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, iy, az).tex(maxU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, iy, az).tex(minU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();

        //left
        buffer.pos(ix, ay, iz).tex(minU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, iy, iz).tex(maxU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, iy, az).tex(maxU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, ay, az).tex(minU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();

        //top
        buffer.pos(ax, ay, iz).tex(minU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, ay, iz).tex(maxU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, ay, az).tex(maxU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, ay, az).tex(minU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();

        //front
        buffer.pos(ax, iy, az).tex(minU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ax, ay, az).tex(maxU, maxV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, ay, az).tex(maxU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();
        buffer.pos(ix, iy, az).tex(minU, minV).lightmap(lightx,lighty).color(r,g,b,a).endVertex();

        tessellator.draw();
    }
}
