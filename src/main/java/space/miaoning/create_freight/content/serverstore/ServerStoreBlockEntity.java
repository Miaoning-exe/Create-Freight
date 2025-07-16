package space.miaoning.create_freight.content.serverstore;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerStoreBlockEntity extends SmartBlockEntity {

    private final ServerStoreItemHandler itemHandler;
    private final LazyOptional<IItemHandler> lazyItemHandler;

    private final List<ItemStack> presetItems = new ArrayList<>();
    private String lastUpdateDate = "";
    private final List<ItemStack> virtualInventory = new ArrayList<>();

    public ServerStoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.itemHandler = new ServerStoreItemHandler(this::getVirtualInventory);
        this.lazyItemHandler = LazyOptional.of(() -> itemHandler);
        setPreset(List.of(
                new ItemStack(Items.CHEST, 1024),
                new ItemStack(Items.STONE, 1024)
        ));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void lazyTick() {
        if (level != null && !level.isClientSide()) {
            String currentDate = LocalDate.now().toString();

            if (!lastUpdateDate.equals(currentDate)) {
                lastUpdateDate = currentDate;
                updateVirtualInventory();
            }
        }
    }

    public void setPreset(List<ItemStack> items) {
        this.presetItems.clear();
        for (ItemStack stack : items) {
            this.presetItems.add(stack.copy());
        }
        updateVirtualInventory();
    }

    private void updateVirtualInventory() {
        if (level != null && !level.isClientSide()) {
            virtualInventory.clear();

            for (ItemStack defaultItem : presetItems) {
                ItemStack newStack = defaultItem.copy();
                virtualInventory.add(newStack);
            }
            setChanged();
        }
    }

    private List<ItemStack> getVirtualInventory() {
        return virtualInventory;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            Direction facing = getBlockState().getValue(DirectionalBlock.FACING).getOpposite();
            if (side == facing) {
                return lazyItemHandler.cast();
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
        lastUpdateDate = tag.getString("LastUpdateDate");

        presetItems.clear();
        if (tag.contains("PresetItems")) {
            ListTag presetTag = tag.getList("PresetItems", 10);
            for (int i = 0; i < presetTag.size(); i++) {
                CompoundTag stackTag = presetTag.getCompound(i);
                ItemStack stack = ItemStack.of(stackTag);
                if (!stack.isEmpty()) {
                    presetItems.add(stack);
                }
            }
        }

        virtualInventory.clear();
        if (tag.contains("VirtualInventory")) {
            ListTag inventoryTag = tag.getList("VirtualInventory", 10);
            for (int i = 0; i < inventoryTag.size(); i++) {
                CompoundTag stackTag = inventoryTag.getCompound(i);
                ItemStack stack = ItemStack.of(stackTag);
                if (!stack.isEmpty()) {
                    virtualInventory.add(stack);
                }
            }
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putString("LastUpdateDate", lastUpdateDate);

        ListTag presetTag = new ListTag();
        for (ItemStack stack : presetItems) {
            CompoundTag stackTag = new CompoundTag();
            stack.save(stackTag);
            presetTag.add(stackTag);
        }
        tag.put("PresetItems", presetTag);

        ListTag inventoryTag = new ListTag();
        for (ItemStack stack : virtualInventory) {
            CompoundTag stackTag = new CompoundTag();
            stack.save(stackTag);
            inventoryTag.add(stackTag);
        }
        tag.put("VirtualInventory", inventoryTag);
    }
}
