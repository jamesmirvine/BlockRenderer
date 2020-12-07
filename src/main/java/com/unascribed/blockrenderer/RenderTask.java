package com.unascribed.blockrenderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class RenderTask {

	public abstract String getCategory();
	
	public abstract ITextComponent getPreviewDisplayName();
	public abstract String getDisplayName();
	public abstract ResourceLocation getId();
	
	public abstract void renderPreview(MatrixStack matrices, int x, int y);
	public abstract void render(int renderSize);
	
}
