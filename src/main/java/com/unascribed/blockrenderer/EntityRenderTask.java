package com.unascribed.blockrenderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import com.google.common.primitives.Doubles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public class EntityRenderTask extends RenderTask {
	
	public final Entity entity;
	
	public EntityRenderTask(Entity entity) {
		this.entity = entity;
	}

	@Override
	public String getCategory() {
		return "entities";
	}
	
	@Override
	public ITextComponent getPreviewDisplayName() {
		return entity.getDisplayName();
	}
	
	@Override
	public String getDisplayName() {
		return entity.getDisplayName().getString();
	}

	@Override
	public ResourceLocation getId() {
		return entity.getType().getRegistryName();
	}

	@Override
	public void renderPreview(MatrixStack matrices, int x, int y) {
		RenderSystem.pushMatrix();
		try {
			RenderSystem.multMatrix(matrices.getLast().getMatrix());
			RenderSystem.translatef(x+8, y+8, 0);
			AxisAlignedBB rbb = entity.getRenderBoundingBox();
			drawEntity(entity, 16/(float)Doubles.max(rbb.getXSize(), rbb.getYSize(), rbb.getZSize()));
		} finally {
			RenderSystem.popMatrix();
		}
	}

	@Override
	public void render(int renderSize) {
		RenderSystem.pushMatrix();
		try {
			RenderSystem.translatef(0.5f, 0.5f, 0);
			AxisAlignedBB rbb = entity.getRenderBoundingBox();
			drawEntity(entity, 1/(float)Doubles.max(rbb.getXSize(), rbb.getYSize(), rbb.getZSize()));
		} finally {
			RenderSystem.popMatrix();
		}
	}
	
	public static void drawEntity(Entity entity, float scale) {
		if (entity == null) return;
		float yaw = -45;
		float pitch = 0;
		RenderSystem.pushMatrix();
		MatrixStack matrices = new MatrixStack();
		matrices.scale(scale, scale, scale);
		Quaternion rot = Vector3f.ZP.rotationDegrees(180f);
		Quaternion xRot = Vector3f.XP.rotationDegrees(20f);
		rot.multiply(xRot);
		matrices.rotate(rot);
		float oldYaw = entity.rotationYaw;
		float oldPitch = entity.rotationPitch;
		LivingEntity lentity = entity instanceof LivingEntity ? (LivingEntity)entity : null;
		Float oldYawOfs = lentity != null ? lentity.renderYawOffset : null;
		Float oldPrevYawHead = lentity != null ? lentity.prevRotationYawHead : null;
		Float oldYawHead = lentity != null ? lentity.rotationYawHead : null;
		entity.rotationYaw = yaw;
		entity.rotationPitch = -pitch;
		if (lentity != null) {
			lentity.renderYawOffset = yaw;
			lentity.rotationYawHead = entity.rotationYaw;
			lentity.prevRotationYawHead = entity.rotationYaw;
		}
		EntityRendererManager erm = Minecraft.getInstance().getRenderManager();
		xRot.conjugate();
		erm.setCameraOrientation(xRot);
		erm.setRenderShadow(false);
		GraphicsFanciness oldFanciness = Minecraft.getInstance().gameSettings.graphicFanciness;
		Minecraft.getInstance().gameSettings.graphicFanciness = GraphicsFanciness.FANCY;
		try {
			IRenderTypeBuffer.Impl buf = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
			erm.renderEntityStatic(entity, 0, 0, 0, 0, 1, matrices, buf, 0xF000F0);
			buf.finish();
		} finally {
			Minecraft.getInstance().gameSettings.graphicFanciness = oldFanciness;
			erm.setRenderShadow(true);
			entity.rotationYaw = oldYaw;
			entity.rotationPitch = oldPitch;
			if (lentity != null) {
				lentity.renderYawOffset = oldYawOfs;
				lentity.prevRotationYawHead = oldPrevYawHead;
				lentity.rotationYawHead = oldYawHead;
			}
			RenderSystem.popMatrix();
		}
	}
	
}