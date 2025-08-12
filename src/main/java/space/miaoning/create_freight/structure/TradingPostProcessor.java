package space.miaoning.create_freight.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import space.miaoning.create_freight.CFBlocks;
import space.miaoning.create_freight.CFStructureProcessors;
import space.miaoning.create_freight.util.NetworkHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TradingPostProcessor extends StructureProcessor {
    public static final Codec<TradingPostProcessor> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("region").forGetter(processor -> processor.regionName)
            ).apply(instance, TradingPostProcessor::new)
    );

    private final String regionName;

    public TradingPostProcessor(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor pServerLevel,
                                                                         BlockPos pOffset,
                                                                         BlockPos pPos,
                                                                         List<StructureTemplate.StructureBlockInfo> pOriginalBlockInfos,
                                                                         List<StructureTemplate.StructureBlockInfo> pProcessedBlockInfos,
                                                                         StructurePlaceSettings pSettings) {

        BlockPos stockTickerPos = null;
        BlockInfoWithIndex serverStoreInfo = null;
        List<BlockInfoWithIndex> tableClothInfos = new ArrayList<>();

        for (int i = 0; i < pProcessedBlockInfos.size(); i++) {
            StructureTemplate.StructureBlockInfo info = pProcessedBlockInfos.get(i);
            if (AllBlocks.STOCK_TICKER.has(info.state())) {
                stockTickerPos = info.pos();
            } else if (CFBlocks.SERVER_STORE.has(info.state())) {
                serverStoreInfo = new BlockInfoWithIndex(info, i);
            } else if (AllBlocks.TABLE_CLOTHS.contains(info.state().getBlock()) &&
                    info.state().getValue(TableClothBlock.HAS_BE)) {
                tableClothInfos.add(new BlockInfoWithIndex(info, i));
            }
        }

        if (stockTickerPos == null || serverStoreInfo == null || tableClothInfos.isEmpty()) {
            return pProcessedBlockInfos;
        }

        ListTag tableClothPositions = new ListTag();
        String dimensionId = pServerLevel.getLevel().dimensionType().effectsLocation().toString();

        for (BlockInfoWithIndex tableClothInfo : tableClothInfos) {
            BlockPos pos = tableClothInfo.info().pos();
            tableClothPositions.add(NbtUtils.writeBlockPos(pos));

            CompoundTag nbt = tableClothInfo.info().nbt() != null
                    ? tableClothInfo.info().nbt().copy()
                    : new CompoundTag();
            nbt.putUUID("OwnerUUID", NetworkHelper.SERVER_ID);
            nbt.putString("TargetDim", dimensionId);
            nbt.put("TargetOffset", NbtUtils.writeBlockPos(stockTickerPos.subtract(pos)));
            pProcessedBlockInfos.set(tableClothInfo.index(), new StructureTemplate.StructureBlockInfo(
                    tableClothInfo.info().pos(),
                    tableClothInfo.info().state(),
                    nbt
            ));
        }

        CompoundTag serverStoreNbt = serverStoreInfo.info().nbt() != null
                ? serverStoreInfo.info().nbt().copy()
                : new CompoundTag();
        serverStoreNbt.putString("Region", regionName);
        serverStoreNbt.remove("LastUpdateDate");
        serverStoreNbt.remove("LastRestockingDate");
        serverStoreNbt.put("TableClothPositions", tableClothPositions);
        pProcessedBlockInfos.set(serverStoreInfo.index(), new StructureTemplate.StructureBlockInfo(
                serverStoreInfo.info().pos(),
                serverStoreInfo.info().state(),
                serverStoreNbt
        ));

        return pProcessedBlockInfos;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return CFStructureProcessors.TRADING_POST_PROCESSOR.get();
    }

    public record BlockInfoWithIndex(StructureTemplate.StructureBlockInfo info, int index) {
    }
}
