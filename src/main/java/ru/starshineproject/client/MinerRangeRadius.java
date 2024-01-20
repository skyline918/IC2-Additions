package ru.starshineproject.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MinerRangeRadius {

    public static void renderBox(AxisAlignedBB box, Color color) {
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();

        GlStateManager.translate(box.minX - renderPosX, box.minY - renderPosY, box.minZ - renderPosZ);
        box = box.offset(-box.minX, -box.minY, -box.minZ);
        GlStateManager.scale(1F, 1F, 1F);

        renderBoxEdges(box, color);

//        renderClaimFacets(box);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    private static void renderBoxEdges(AxisAlignedBB aabb, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.glLineWidth(4.0F);
        GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 255);
        double ix = aabb.minX + 0.01;
        double iy = aabb.minY + 0.01;
        double iz = aabb.minZ + 0.01;
        double ax = aabb.maxX + 0.99;
        double ay = aabb.maxY + 0.99;
        double az = aabb.maxZ + 0.99;

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        buffer.pos(ix, iy, iz).endVertex();
        buffer.pos(ix, ay, iz).endVertex();

        buffer.pos(ix, ay, iz).endVertex();
        buffer.pos(ax, ay, iz).endVertex();

        buffer.pos(ax, ay, iz).endVertex();
        buffer.pos(ax, iy, iz).endVertex();

        buffer.pos(ax, iy, iz).endVertex();
        buffer.pos(ix, iy, iz).endVertex();

        buffer.pos(ix, iy, az).endVertex();
        buffer.pos(ix, ay, az).endVertex();

        buffer.pos(ix, iy, az).endVertex();
        buffer.pos(ax, iy, az).endVertex();

        buffer.pos(ax, iy, az).endVertex();
        buffer.pos(ax, ay, az).endVertex();

        buffer.pos(ix, ay, az).endVertex();
        buffer.pos(ax, ay, az).endVertex();

        buffer.pos(ix, iy, iz).endVertex();
        buffer.pos(ix, iy, az).endVertex();

        buffer.pos(ix, ay, iz).endVertex();
        buffer.pos(ix, ay, az).endVertex();

        buffer.pos(ax, iy, iz).endVertex();
        buffer.pos(ax, iy, az).endVertex();

        buffer.pos(ax, ay, iz).endVertex();
        buffer.pos(ax, ay, az).endVertex();

        tessellator.draw();
    }

}
