package space.miaoning.create_freight;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("removal")
public class CreateFreightClient {
    public static void setup() {
        setupRenderTypes();
    }

    public static void setupRenderTypes() {
        setRenderType(CFBlocks.AUTOMATIC_TRADER.get(), RenderType.cutout());
    }

    public static void setRenderType(Block block, RenderType type) {
        ItemBlockRenderTypes.setRenderLayer(block, type);
    }
}
