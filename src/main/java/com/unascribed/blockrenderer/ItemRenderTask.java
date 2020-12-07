package com.unascribed.blockrenderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

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
	public ITextComponent getPreviewDisplayName() {
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
	public void renderPreview(MatrixStack matrices, int x, int y) {
		RenderSystem.pushMatrix();
		Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
		RenderSystem.popMatrix();
	}

	@Override
	public void render(int renderSize) {
		Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
	}

}
