package space.miaoning.create_freight.structure;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import space.miaoning.create_freight.CFStructureProcessors;
import space.miaoning.create_freight.util.NetworkHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FreqProcessor extends StructureProcessor {
    public static final Codec<FreqProcessor> CODEC = Codec.unit(FreqProcessor::new);

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor pServerLevel,
                                                                         BlockPos pOffset,
                                                                         BlockPos pPos,
                                                                         List<StructureTemplate.StructureBlockInfo> pOriginalBlockInfos,
                                                                         List<StructureTemplate.StructureBlockInfo> pProcessedBlockInfos,
                                                                         StructurePlaceSettings pSettings) {

        RandomSource random = pSettings.getRandom(pPos);
        UUID freqId = new UUID(random.nextLong(), random.nextLong());
        NetworkHelper.initServerNetwork(freqId);
        for (int i = 0; i < pProcessedBlockInfos.size(); i++) {
            StructureTemplate.StructureBlockInfo blockInfo = pProcessedBlockInfos.get(i);
            BlockState state = blockInfo.state();
            if (AllBlocks.STOCK_TICKER.has(state) || AllBlocks.STOCK_LINK.has(state)) {
                CompoundTag nbt = blockInfo.nbt() != null ? blockInfo.nbt().copy() : new CompoundTag();
                nbt.putUUID("Freq", freqId);

                pProcessedBlockInfos.set(i, new StructureTemplate.StructureBlockInfo(
                        blockInfo.pos(),
                        state,
                        nbt
                ));
            }
        }
        return pProcessedBlockInfos;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return CFStructureProcessors.FREQ_PROCESSOR.get();
    }
}
