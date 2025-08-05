package space.miaoning.create_freight.structure;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import space.miaoning.create_freight.CFStructureProcessors;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FreqProcessor extends StructureProcessor {
    public static final Codec<FreqProcessor> CODEC = Codec.unit(FreqProcessor::new);

    private static UUID getFreqIdForPos(BlockPos pos) {
        long x = pos.getX() * 0x9E3779B97F4A7C15L;
        long y = pos.getY() * 0x85EBCA6BL;
        long z = pos.getZ() * 0xC2B2AE3DL;
        return new UUID(x ^ y, z ^ x);
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader level,
                                                        BlockPos jigsawPiecePos,
                                                        BlockPos jigsawPieceBottomCenterPos,
                                                        StructureTemplate.StructureBlockInfo blockInfoLocal,
                                                        StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {

        BlockState state = blockInfoGlobal.state();
        if (AllBlocks.STOCK_TICKER.has(state) || AllBlocks.STOCK_LINK.has(state)) {
            UUID freqId = getFreqIdForPos(jigsawPieceBottomCenterPos);

            CompoundTag nbt = blockInfoGlobal.nbt() != null ? blockInfoGlobal.nbt().copy() : new CompoundTag();
            nbt.putUUID("Freq", freqId);

            LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks
                    .computeIfAbsent(freqId, $ -> new LogisticsNetwork(freqId));
            if (network.owner == null) {
                network.owner = new UUID(0, 0);
                network.locked = true;
                Create.LOGISTICS.markDirty();
            }

            return new StructureTemplate.StructureBlockInfo(
                    blockInfoGlobal.pos(),
                    state,
                    nbt
            );
        }

        return blockInfoGlobal;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return CFStructureProcessors.FREQ_PROCESSOR.get();
    }
}
