package space.miaoning.create_freight.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.BigItemStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import space.miaoning.create_freight.CFBlocks;
import space.miaoning.create_freight.CFStructureProcessors;
import space.miaoning.create_freight.core.TradingManager;
import space.miaoning.create_freight.recipe.TradingRecipe;
import space.miaoning.create_freight.util.NetworkHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TradingRecipeProcessor extends StructureProcessor {
    public static final Codec<TradingRecipeProcessor> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.STRING.fieldOf("region").forGetter(processor -> processor.regionName))
            .apply(instance, TradingRecipeProcessor::new)
    );

    private final String regionName;
    private static final Map<BlockPos, List<TradingRecipe>> structureTradings = new HashMap<>();
    private static final Map<BlockPos, Integer> usedTradingCount = new HashMap<>();
    private static final Map<BlockPos, BlockPos> stockTickerPositions = new HashMap<>();

    public TradingRecipeProcessor(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public StructureTemplate.StructureBlockInfo process(LevelReader level,
                                                        BlockPos jigsawPiecePos,
                                                        BlockPos jigsawPieceBottomCenterPos,
                                                        StructureTemplate.StructureBlockInfo blockInfoLocal,
                                                        StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                        StructurePlaceSettings settings,
                                                        @Nullable StructureTemplate template) {

        if (!structureTradings.containsKey(jigsawPieceBottomCenterPos)) {
            List<TradingRecipe> recipes = TradingManager.getRandomTradingRecipes(regionName, 3);
            structureTradings.put(jigsawPieceBottomCenterPos, recipes);
            usedTradingCount.put(jigsawPieceBottomCenterPos, 0);
        }
        List<TradingRecipe> availableRecipes = structureTradings.get(jigsawPieceBottomCenterPos);

        if (blockInfoGlobal.nbt() == null || availableRecipes.isEmpty()) {
            return blockInfoGlobal;
        }

        if (CFBlocks.SERVER_STORE.has(blockInfoGlobal.state())) {
            CompoundTag nbt = blockInfoGlobal.nbt().copy();

            ListTag presetItems = new ListTag();
            for (TradingRecipe recipe : availableRecipes) {
                BigItemStack sellItem = new BigItemStack(
                        recipe.getSell().copyWithCount(1),
                        recipe.getLimit() > 0 ? recipe.getSell().getCount() * recipe.getLimit() : BigItemStack.INF
                );
                if (sellItem.count > 0) {
                    presetItems.add(sellItem.write());
                }
            }
            nbt.put("PresetItems", presetItems);

            return new StructureTemplate.StructureBlockInfo(
                    blockInfoGlobal.pos(),
                    blockInfoGlobal.state(),
                    nbt
            );
        }

        if (!stockTickerPositions.containsKey(jigsawPieceBottomCenterPos)) {
            if (AllBlocks.STOCK_TICKER.has(blockInfoGlobal.state()))
                stockTickerPositions.put(jigsawPieceBottomCenterPos, blockInfoGlobal.pos());
            return blockInfoGlobal;
        }

        if (AllBlocks.TABLE_CLOTHS.contains(blockInfoGlobal.state().getBlock())) {
            // 为桌布分配交易
            int usedCount = usedTradingCount.get(jigsawPieceBottomCenterPos);
            TradingRecipe selectedRecipe = availableRecipes.get(usedCount % availableRecipes.size());
            usedTradingCount.put(jigsawPieceBottomCenterPos, usedCount + 1);

            // 设置桌布的交易信息
            CompoundTag nbt = blockInfoGlobal.nbt().copy();

            // 设置基本属性
            nbt.putUUID("OwnerUUID", NetworkHelper.SERVER_ID);
            nbt.putString("TargetDim", level.dimensionType().effectsLocation().toString());
            BlockPos stockTickerPos = stockTickerPositions.get(jigsawPieceBottomCenterPos);
            BlockPos offset = stockTickerPos.subtract(blockInfoGlobal.pos());
            nbt.put("TargetOffset", NbtUtils.writeBlockPos(offset));
            nbt.putByte("Valid", (byte) 1);

            // 设置cost
            ItemStack cost = selectedRecipe.getCost();
            nbt.put("Filter", cost.copyWithCount(1).serializeNBT());
            nbt.putInt("FilterAmount", cost.getCount());

            // 设置sell
            CompoundTag encodedRequest = new CompoundTag();
            ListTag orderedCrafts = new ListTag();
            encodedRequest.put("OrderedCrafts", orderedCrafts);

            CompoundTag orderedStacks = new CompoundTag();
            ListTag entries = new ListTag();

            ItemStack sell = selectedRecipe.getSell();
            CompoundTag entry = new CompoundTag();
            entry.put("Item", sell.copyWithCount(1).serializeNBT());
            entry.putInt("Amount", sell.getCount());

            entries.add(entry);
            orderedStacks.put("Entries", entries);
            encodedRequest.put("OrderedStacks", orderedStacks);
            nbt.put("EncodedRequest", encodedRequest);

            return new StructureTemplate.StructureBlockInfo(
                    blockInfoGlobal.pos(),
                    blockInfoGlobal.state(),
                    nbt
            );
        }

        return blockInfoGlobal;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return CFStructureProcessors.TRADING_RECIPE_PROCESSOR.get();
    }
}
