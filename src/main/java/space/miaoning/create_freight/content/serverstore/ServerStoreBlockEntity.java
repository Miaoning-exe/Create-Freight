package space.miaoning.create_freight.content.serverstore;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import space.miaoning.create_freight.config.TradingConfig;
import space.miaoning.create_freight.recipe.TradingRecipe;
import space.miaoning.create_freight.util.TradingRecipeHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerStoreBlockEntity extends SmartBlockEntity {

    private final ServerStoreItemHandler itemHandler;
    private final LazyOptional<IItemHandler> lazyItemHandler;

    private String regionName = "";
    private @Nullable ZonedDateTime lastRefreshTime;
    private @Nullable ZonedDateTime lastRestockTime;
    private final List<BlockPos> tableClothPositions = new ArrayList<>();
    private final List<BigItemStack> presetItems = new ArrayList<>();
    private final List<BigItemStack> virtualInventory = new ArrayList<>();

    public ServerStoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.itemHandler = new ServerStoreItemHandler(() -> virtualInventory);
        this.lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void lazyTick() {
        if (level != null && !level.isClientSide()) {
            if (tableClothPositions.isEmpty()) {
                searchTableClothPositions();
            }
            if (TradingConfig.tradingRefreshChecker.shouldExecute(lastRefreshTime)) {
                refreshRecipes();
            }
            if (TradingConfig.tradingRestockChecker.shouldExecute(lastRestockTime)) {
                updateVirtualInventory();
            }
        }
    }

    public void refreshRecipes() {
        if (level == null || level.isClientSide()) {
            return;
        }

        List<TradingRecipe> recipes = TradingRecipeHelper.getRandomTradingRecipes(
                level, regionName, tableClothPositions.size(), RandomSource.create());

        // 设置服务器商店预设物品
        presetItems.clear();
        for (TradingRecipe recipe : recipes) {
            BigItemStack sellItem = new BigItemStack(
                    recipe.getSell().copyWithCount(1),
                    recipe.getLimit() > 0 ? recipe.getSell().getCount() * recipe.getLimit() : BigItemStack.INF
            );
            if (sellItem.count > 0) {
                presetItems.add(sellItem);
            }
        }
        setChanged();

        // 为每个桌布分配交易
        for (int i = 0; i < tableClothPositions.size(); i++) {
            BlockPos pos = tableClothPositions.get(i);
            if (!(level.getBlockEntity(pos) instanceof TableClothBlockEntity tcBE)) {
                continue;
            }
            if (i >= recipes.size()) {
                tcBE.requestData.isValid = false;
            } else {
                TradingRecipe recipe = recipes.get(i);

                ItemStack cost = recipe.getCost();
                FilteringBehaviour behaviour = tcBE.getBehaviour(FilteringBehaviour.TYPE);
                behaviour.setFilter(cost.copyWithCount(1));
                behaviour.count = cost.getCount();

                ItemStack sell = recipe.getSell();
                BigItemStack sellStack = new BigItemStack(sell.copyWithCount(1), sell.getCount());
                tcBE.requestData.isValid = true;
                tcBE.requestData.encodedRequest = new PackageOrderWithCrafts(
                        new PackageOrder(List.of(sellStack)),
                        List.of()
                );
            }
            tcBE.setChanged();
        }

        lastRefreshTime = ZonedDateTime.now();
        lastRestockTime = null;
    }

    private void updateVirtualInventory() {
        if (level != null && !level.isClientSide()) {
            virtualInventory.clear();

            for (BigItemStack defaultItem : presetItems) {
                BigItemStack newStack = new BigItemStack(defaultItem.stack, defaultItem.count);
                virtualInventory.add(newStack);
            }
            setChanged();

            lastRestockTime = ZonedDateTime.now();
        }
    }

    public boolean hasItems() {
        return virtualInventory.stream().anyMatch(stack -> stack.count > 0);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (level != null && side != null) {
                BlockPos adjacentPos = worldPosition.relative(side);
                BlockState adjacentState = level.getBlockState(adjacentPos);
                if (AllBlocks.PACKAGER.has(adjacentState)) {
                    return lazyItemHandler.cast();
                }
            }
            return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        regionName = tag.getString("Region");
        try {
            lastRefreshTime = ZonedDateTime.parse(tag.getString("LastRefreshTime"));
        } catch (DateTimeParseException e) {
            lastRefreshTime = null;
        }
        try {
            lastRestockTime = ZonedDateTime.parse(tag.getString("LastRestockTime"));
        } catch (DateTimeParseException e) {
            lastRestockTime = null;
        }

        presetItems.clear();
        if (tag.contains("PresetItems")) {
            ListTag presetTag = tag.getList("PresetItems", Tag.TAG_COMPOUND);
            for (int i = 0; i < presetTag.size(); i++) {
                CompoundTag stackTag = presetTag.getCompound(i);
                BigItemStack stack = BigItemStack.read(stackTag);
                if (stack.count > 0) {
                    presetItems.add(stack);
                }
            }
        }

        virtualInventory.clear();
        if (tag.contains("VirtualInventory")) {
            ListTag inventoryTag = tag.getList("VirtualInventory", Tag.TAG_COMPOUND);
            for (int i = 0; i < inventoryTag.size(); i++) {
                CompoundTag stackTag = inventoryTag.getCompound(i);
                BigItemStack stack = BigItemStack.read(stackTag);
                if (stack.count > 0) {
                    virtualInventory.add(stack);
                }
            }
        }

        tableClothPositions.clear();
        if (tag.contains("TableClothPositions")) {
            ListTag tableClothTag = tag.getList("TableClothPositions", Tag.TAG_COMPOUND);
            for (int i = 0; i < tableClothTag.size(); i++) {
                BlockPos pos = NbtUtils.readBlockPos(tableClothTag.getCompound(i));
                tableClothPositions.add(pos);
            }
        }
    }

    @SuppressWarnings("removal")
    private void searchTableClothPositions() {
        if (level == null) return;

        List<String> STRUCTURE_NAMES = List.of(
                "desert",
                "jungle",
                "ocean",
                "plain",
                "snowy_plain",
                "taiga"
        );

        var currentBiomeHolder = level.getBiome(getBlockPos());
        for (String structureName : STRUCTURE_NAMES) {
            TagKey<Biome> biomeTag = TagKey.create(Registries.BIOME, new ResourceLocation(
                    "create_freight",
                    "has_structure/" + structureName + "_trading_post"
            ));
            if (currentBiomeHolder.is(biomeTag)) {
                regionName = structureName;
                break;
            }
        }

        int verticalRange = 3;
        int horizontalRange = 12;

        BlockPos startPos = worldPosition.offset(-horizontalRange, -verticalRange, -horizontalRange);
        BlockPos endPos = worldPosition.offset(horizontalRange, verticalRange, horizontalRange);

        for (BlockPos pos : BlockPos.betweenClosed(startPos, endPos)) {
            BlockState state = level.getBlockState(pos);

            if (AllBlocks.TABLE_CLOTHS.contains(state.getBlock()) && state.getValue(TableClothBlock.HAS_BE)) {
                tableClothPositions.add(pos.immutable());
            }
        }

        setChanged();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putString("Region", regionName);
        tag.putString("LastRefreshTime", lastRefreshTime != null ? lastRefreshTime.toString() : "");
        tag.putString("LastRestockTime", lastRestockTime != null ? lastRestockTime.toString() : "");

        ListTag presetTag = new ListTag();
        for (BigItemStack stack : presetItems) {
            presetTag.add(stack.write());
        }
        tag.put("PresetItems", presetTag);

        ListTag inventoryTag = new ListTag();
        for (BigItemStack stack : virtualInventory) {
            inventoryTag.add(stack.write());
        }
        tag.put("VirtualInventory", inventoryTag);

        ListTag tableClothTag = new ListTag();
        for (BlockPos pos : tableClothPositions) {
            tableClothTag.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("TableClothPositions", tableClothTag);
    }
}
