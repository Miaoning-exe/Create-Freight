package space.miaoning.create_freight.content.automatictrader;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour.RequestType;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.tableCloth.ShoppingListItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.slf4j.Logger;
import space.miaoning.create_freight.util.NetworkHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AutomaticTraderBlockEntity extends SmartBlockEntity implements Container, MenuProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CONTAINER_SIZE = 9;

    private String address;
    private UUID shopNetwork;
    private InventorySummary paymentEntries;
    private PackageOrder order;
    private ItemStack lastFilterItem = ItemStack.EMPTY;

    private BlockPos stockTickerPos = null;

    private final ItemStackHandler itemHandler = new ItemStackHandler(CONTAINER_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private FilteringBehaviour filtering;

    public AutomaticTraderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(20);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(filtering = createFilter());
        filtering.withPredicate(stack -> stack.getItem() instanceof ShoppingListItem);
        filtering.showCountWhen(() -> false);
        filtering.setLabel(Component.translatable("gui.create_freight.automatic_trader.shopping_list_filter"));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.write(tag, clientPacket);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public FilteringBehaviour createFilter() {
        return new FilteringBehaviour(this, new ValueBoxTransform() {

            @Override
            public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
                TransformStack.of(ms).rotateXDegrees(90);
            }

            @Override
            public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
                return new Vec3(0.5, 15.5 / 16d, 0.5);
            }

            @Override
            public float getScale() {
                return super.getScale();
            }
        });
    }

    @Override
    public void lazyTick() {
        if (level != null && !level.isClientSide()) {
            ItemStack filterItem = filtering.getFilter();
            if (filterItem.isEmpty())
                return;

            StockTickerBlockEntity tickerBE = getValidStockTicker(level);
            if (tickerBE == null)
                return;

            if (!ItemStack.matches(filterItem, lastFilterItem)) {
                updateTradeData(filterItem);
                lastFilterItem = filterItem.copy();
            }

            executeAutomatedTrade(tickerBE);
        }
    }

    @Nullable
    protected StockTickerBlockEntity getValidStockTicker(Level level) {
        StockTickerBlockEntity tickerBE = null;
        if (stockTickerPos != null) {
            tickerBE = getValidTickerAt(level, stockTickerPos);
        }
        if (tickerBE == null) {
            tickerBE = findStockTicker(level, getBlockPos());
            stockTickerPos = tickerBE != null ? tickerBE.getBlockPos() : null;
        }
        return tickerBE;
    }

    @Nullable
    private static StockTickerBlockEntity getValidTickerAt(Level level, BlockPos pos) {
        if (!level.isLoaded(pos))
            return null;

        BlockState blockState = level.getBlockState(pos);
        if (!AllBlocks.STOCK_TICKER.has(blockState))
            return null;

        if (level.getBlockEntity(pos) instanceof StockTickerBlockEntity stbe)
            return stbe.isKeeperPresent() ? stbe : null;
        return null;
    }

    @Nullable
    private static StockTickerBlockEntity findStockTicker(Level level, BlockPos pos) {
        for (int x = -16; x <= 16; x++) {
            for (int z = -16; z <= 16; z++) {
                for (int y = -2; y <= 2; y++) {
                    BlockPos searchPos = pos.offset(x, y, z);
                    StockTickerBlockEntity stbe = getValidTickerAt(level, searchPos);
                    if (stbe != null) {
                        return stbe;
                    }
                }
            }
        }
        return null;
    }

    private void updateTradeData(ItemStack filterItem) {
        ShoppingListItem.ShoppingList shoppingList = ShoppingListItem.getList(filterItem);
        address = ShoppingListItem.getAddress(filterItem);

        if (shoppingList != null) {
            shopNetwork = shoppingList.shopNetwork();
            Couple<InventorySummary> bakeEntries = shoppingList.bakeEntries(level, null);
            paymentEntries = bakeEntries.getSecond();
            InventorySummary orderEntries = bakeEntries.getFirst();
            order = new PackageOrder(orderEntries.getStacksByCount());
        } else {
            paymentEntries = null;
            order = null;
        }
    }

    private void executeAutomatedTrade(StockTickerBlockEntity tickerBE) {
        if (!tickerBE.behaviour.freqId.equals(shopNetwork))
            return;

        tickerBE.getAccurateSummary();

        InventorySummary recentSummary = tickerBE.getRecentSummary();
        for (var entry : order.stacks()) {
            if (recentSummary.getCountOf(entry.stack) < entry.count)
                return;
        }

        IItemHandler receivedPayments = getReceivedPaymentsFromTicker(tickerBE);
        if (receivedPayments == null) {
            return;
        }

        int occupiedSlots = 0;
        for (var entry : paymentEntries.getStacksByCount())
            occupiedSlots += Mth.ceil(entry.count / (float) entry.stack.getMaxStackSize());
        for (int i = 0; i < receivedPayments.getSlots(); i++)
            if (receivedPayments.getStackInSlot(i).isEmpty())
                occupiedSlots--;

        if (occupiedSlots > 0)
            return;

        if (!transferPaymentItems(receivedPayments, true))
            return;
        transferPaymentItems(receivedPayments, false);

        tickerBE.broadcastPackageRequest(RequestType.REDSTONE, order, null, getAddress());

        if (!order.isEmpty())
            AllSoundEvents.STOCK_TICKER_TRADE.playOnServer(level, tickerBE.getBlockPos());
    }

    @Nullable
    private IItemHandler getReceivedPaymentsFromTicker(StockTickerBlockEntity tickerBE) {
        try {
            var receivedPaymentsField = StockTickerBlockEntity.class.getDeclaredField("receivedPayments");
            receivedPaymentsField.setAccessible(true);
            return (IItemHandler) receivedPaymentsField.get(tickerBE);
        } catch (Exception e) {
            LOGGER.error("Failed to access receivedPayments field", e);
            return null;
        }
    }

    private boolean transferPaymentItems(IItemHandler receivedPayments, boolean simulate) {
        InventorySummary tally = paymentEntries.copy();
        List<ItemStack> toTransfer = new ArrayList<>();

        for (int i = 0; i < CONTAINER_SIZE; i++) {
            ItemStack item = itemHandler.getStackInSlot(i);
            if (item.isEmpty())
                continue;
            int countOf = tally.getCountOf(item);
            if (countOf == 0)
                continue;
            int toRemove = Math.min(item.getCount(), countOf);
            tally.add(item, -toRemove);

            if (simulate)
                continue;

            int newStackSize = item.getCount() - toRemove;
            itemHandler.setStackInSlot(i, newStackSize == 0 ? ItemStack.EMPTY : item.copyWithCount(newStackSize));
            toTransfer.add(item.copyWithCount(toRemove));
        }

        if (simulate) {
            return tally.getTotalCount() == 0;
        }

        if (!NetworkHelper.isServerNetwork(shopNetwork)) {
            toTransfer.forEach(s -> ItemHandlerHelper.insertItemStacked(receivedPayments, s, false));
        }
        return true;
    }

    private String getAddress() {
        if (level == null) {
            return address;
        }
        for (Direction side : Iterate.directions) {
            BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
            if (!(blockEntity instanceof SignBlockEntity sign))
                continue;
            for (boolean front : Iterate.trueAndFalse) {
                SignText text = sign.getText(front);
                StringBuilder address = new StringBuilder();
                for (Component component : text.getMessages(false)) {
                    String string = component.getString();
                    if (!string.isBlank())
                        address.append(string.trim()).append(" ");
                }
                String signAddress = address.toString().trim();
                if (!signAddress.isBlank())
                    return signAddress;
            }
        }
        return address;
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return IntStream.range(0, CONTAINER_SIZE)
                .allMatch(i -> itemHandler.getStackInSlot(i).isEmpty());
    }

    @Override
    public ItemStack getItem(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return itemHandler.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return itemHandler.extractItem(slot, itemHandler.getStackInSlot(slot).getCount(), false);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return !isRemoved() && player.distanceToSqr(getBlockPos().getX() + 0.5,
                getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() {
        IntStream.range(0, CONTAINER_SIZE)
                .forEach(i -> itemHandler.setStackInSlot(i, ItemStack.EMPTY));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.create_freight.automatic_trader");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ChestMenu(MenuType.GENERIC_9x1, containerId, playerInventory, this, 1);
    }
}
