package com.unascribed.blockrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ItemRenderTask extends RenderTask {

	public final ItemStack stack;
	
	public ItemRenderTask(ItemStack stack) {
		this.stack = stack;
	}
	
	@Override
	public String getCategory() {
		return "items";
	}
	
	@Override
	public Component getPreviewDisplayName() {
		return stack.getDisplayName();
	}

	@Override
	public String getDisplayName() {
		return stack.getDisplayName().getString();
	}

	@Override
	public ResourceLocation getId() {
		return stack.getItem().getRegistryName();
	}

	@Override
	public void renderPreview(PoseStack poseStack, int x, int y) {
		poseStack.pushPose();
		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
		poseStack.popPose();
	}

	@Override
	public void render(int renderSize) {
		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, 0, 0);
	}

}
