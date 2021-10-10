package com.unascribed.blockrenderer;

import net.minecraftforge.fml.IExtensionPoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(BlockRenderer.MODID)
public class BlockRenderer {

    public static final Logger log = LogManager.getLogger("BlockRenderer");
    public static final String MODID = "blockrenderer";

    public static ClientRenderHandler renderHandler;

    public BlockRenderer() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> renderHandler = new ClientRenderHandler());
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ignored", (remote, isServer) -> true));
    }
}