package space.miaoning.create_freight.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import space.miaoning.create_freight.CFBlocks;
import space.miaoning.create_freight.CFStructureProcessors;
import space.miaoning.create_freight.recipe.TradingRecipe;
import space.miaoning.create_freight.util.NetworkHelper;
import space.miaoning.create_freight.util.TradingRecipeHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TradingRecipeProcessor extends StructureProcessor {
    public static final Codec<TradingRecipeProcessor> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("region").forGetter(processor -> processor.regionName)
            ).apply(instance, TradingRecipeProcessor::new)
    );

    private final String regionName;

    public TradingRecipeProcessor(String regionName) {
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
            } else if (CFBlocks.SERVER_STORE.has(info.state()) &&
                    info.nbt() != null) {
                serverStoreInfo = new BlockInfoWithIndex(info, i);
            } else if (AllBlocks.TABLE_CLOTHS.contains(info.state().getBlock()) &&
                    info.state().getValue(TableClothBlock.HAS_BE)) {
                tableClothInfos.add(new BlockInfoWithIndex(info, i));
            }
        }

        if (stockTickerPos == null || serverStoreInfo == null || tableClothInfos.isEmpty()) {
            return pProcessedBlockInfos;
        }

        // 生成交易配方
        ServerLevel level = pServerLevel.getLevel();
        List<TradingRecipe> recipes = TradingRecipeHelper.getRandomTradingRecipes(
                level, regionName, tableClothInfos.size(), pSettings.getRandom(serverStoreInfo.info().pos()));

        // 设置服务器商店预设物品
        CompoundTag serverStoreNbt = Objects.requireNonNull(serverStoreInfo.info().nbt()).copy();
        setServerStoreNbt(serverStoreNbt, recipes, tableClothInfos);

        pProcessedBlockInfos.set(serverStoreInfo.index(), new StructureTemplate.StructureBlockInfo(
                serverStoreInfo.info().pos(),
                serverStoreInfo.info().state(),
                serverStoreNbt
        ));

        // 为每个桌布分配交易
        for (int i = 0; i < recipes.size(); i++) {
            BlockInfoWithIndex tableClothInfo = tableClothInfos.get(i);

            CompoundTag nbt = tableClothInfo.info().nbt() != null
                    ? tableClothInfo.info().nbt().copy()
                    : new CompoundTag();
            setRecipeNbt(
                    nbt,
                    level.dimensionType().effectsLocation().toString(),
                    stockTickerPos.subtract(tableClothInfo.info().pos()),
                    recipes.get(i)
            );

            pProcessedBlockInfos.set(tableClothInfo.index(), new StructureTemplate.StructureBlockInfo(
                    tableClothInfo.info().pos(),
                    tableClothInfo.info().state(),
                    nbt
            ));
        }

        return pProcessedBlockInfos;
    }

    private void setServerStoreNbt(CompoundTag nbt,
                                   List<TradingRecipe> recipes,
                                   List<BlockInfoWithIndex> tableClothInfos) {

        nbt.putString("Region", regionName);
        nbt.putString("LastUpdateDate", "");

        ListTag presetItems = new ListTag();
        for (TradingRecipe recipe : recipes) {
            BigItemStack sellItem = new BigItemStack(
                    recipe.getSell().copyWithCount(1),
                    recipe.getLimit() > 0 ? recipe.getSell().getCount() * recipe.getLimit() : BigItemStack.INF
            );
            if (sellItem.count > 0) {
                presetItems.add(sellItem.write());
            }
        }
        nbt.put("PresetItems", presetItems);

        ListTag tableClothPositions = new ListTag();
        for (BlockInfoWithIndex tableClothInfo : tableClothInfos) {
            BlockPos pos = tableClothInfo.info().pos();
            tableClothPositions.add(NbtUtils.writeBlockPos(pos));
        }
        nbt.put("TableClothPositions", tableClothPositions);
    }

    private static void setRecipeNbt(CompoundTag nbt, String dim, BlockPos offset, TradingRecipe recipe) {
        // 设置基本属性
        nbt.putUUID("OwnerUUID", NetworkHelper.SERVER_ID);
        nbt.putString("TargetDim", dim);
        nbt.put("TargetOffset", NbtUtils.writeBlockPos(offset));
        nbt.putBoolean("Valid", true);

        // 设置cost
        ItemStack cost = recipe.getCost();
        nbt.put("Filter", cost.copyWithCount(1).serializeNBT());
        nbt.putInt("FilterAmount", cost.getCount());
        nbt.putBoolean("UpTo", true);

        // 设置sell
        CompoundTag encodedRequest = new CompoundTag();
        ListTag orderedCrafts = new ListTag();
        encodedRequest.put("OrderedCrafts", orderedCrafts);

        CompoundTag orderedStacks = new CompoundTag();
        ListTag entries = new ListTag();

        ItemStack sell = recipe.getSell();
        CompoundTag entry = new CompoundTag();
        entry.put("Item", sell.copyWithCount(1).serializeNBT());
        entry.putInt("Amount", sell.getCount());

        entries.add(entry);
        orderedStacks.put("Entries", entries);
        encodedRequest.put("OrderedStacks", orderedStacks);
        nbt.put("EncodedRequest", encodedRequest);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return CFStructureProcessors.TRADING_RECIPE_PROCESSOR.get();
    }

    public record BlockInfoWithIndex(StructureTemplate.StructureBlockInfo info, int index) {
    }
}
