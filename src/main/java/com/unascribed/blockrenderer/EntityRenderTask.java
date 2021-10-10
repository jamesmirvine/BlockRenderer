package com.unascribed.blockrenderer;

import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

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
    public Component getPreviewDisplayName() {
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
    public void renderPreview(PoseStack posestack, int x, int y) {
        posestack.pushPose();
        try {
            posestack.mulPoseMatrix(posestack.last().pose());
            posestack.translate(x + 8, y + 8, 0);
            AABB boundingBox = entity.getBoundingBox();
            drawEntity(entity, 16 / (float) Doubles.max(boundingBox.getXsize(), boundingBox.getYsize(), boundingBox.getZsize()));
        } finally {
            posestack.popPose();
        }
    }

    @Override
    public void render(int renderSize) {
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        try {
            poseStack.translate(0.5f, 0.5f, 0);
            AABB boundingBox = entity.getBoundingBox();
            drawEntity(entity, 1 / (float) Doubles.max(boundingBox.getXsize(), boundingBox.getYsize(), boundingBox.getZsize()));
        } finally {
            poseStack.popPose();
        }
    }

    public static void drawEntity(Entity entity, float scale) {
        if (entity == null) return;
        float yaw = -45;
        float pitch = 0;
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        Quaternion rot = Vector3f.ZP.rotationDegrees(180f);
        Quaternion xRot = Vector3f.XP.rotationDegrees(20f);
        rot.mul(xRot);
        poseStack.mulPose(rot);
        float oldYaw = entity.getYRot();
        float oldPitch = entity.getXRot();
        LivingEntity lentity = entity instanceof LivingEntity ? (LivingEntity) entity : null;
        Float oldYawOfs = lentity != null ? lentity.yBodyRot : null;
        Float oldPrevYawHead = lentity != null ? lentity.yHeadRotO : null;
        Float oldYawHead = lentity != null ? lentity.yHeadRot : null;
        entity.setYRot(yaw);
        entity.setXRot(-pitch);
        if (lentity != null) {
            lentity.yBodyRot = yaw;
            lentity.yHeadRot = entity.getYRot();
            lentity.yHeadRotO = entity.getYRot();
        }
        EntityRenderDispatcher erm = Minecraft.getInstance().getEntityRenderDispatcher();
        xRot.conj();
        erm.overrideCameraOrientation(xRot);
        erm.setRenderShadow(false);
        GraphicsStatus oldFanciness = Minecraft.getInstance().options.graphicsMode;
        Minecraft.getInstance().options.graphicsMode = GraphicsStatus.FANCY;
        try {
            MultiBufferSource.BufferSource buf = Minecraft.getInstance().renderBuffers().bufferSource();
            erm.render(entity, 0, 0, 0, 0, 1, poseStack, buf, 0xF000F0);
            buf.endBatch();
        } finally {
            Minecraft.getInstance().options.graphicsMode = oldFanciness;
            erm.setRenderShadow(true);
            entity.setYRot(oldYaw);
            entity.setXRot(oldPitch);
            if (lentity != null) {
                lentity.yBodyRot = oldYawOfs;
                lentity.yHeadRotO = oldPrevYawHead;
                lentity.yHeadRot = oldYawHead;
            }
            poseStack.popPose();
        }
    }

}