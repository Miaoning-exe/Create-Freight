package space.miaoning.create_freight.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import space.miaoning.create_freight.CFBlocks;
import space.miaoning.create_freight.CreateFreight;

public class GenBlockStates extends BlockStateProvider {
    public GenBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CreateFreight.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(CFBlocks.SERVER_STORE.get());
//        simpleBlockWithItem(CFBlocks.CARGO_STATION.get());
        simpleBlockWithItem(CFBlocks.AUTOMATIC_TRADER.get());
    }

    private void simpleBlockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }
}
