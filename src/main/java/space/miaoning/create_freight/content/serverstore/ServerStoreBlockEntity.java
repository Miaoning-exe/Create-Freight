package space.miaoning.create_freight.content.serverstore;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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

    private final List<BigItemStack> presetItems = new ArrayList<>();
    private String lastUpdateDate = "";
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
            String currentDate = LocalDate.now().toString();

            if (!lastUpdateDate.equals(currentDate)) {
                lastUpdateDate = currentDate;
                updateVirtualInventory();
            }
        }
    }

    private void updateVirtualInventory() {
        if (level != null && !level.isClientSide()) {
            virtualInventory.clear();

            for (BigItemStack defaultItem : presetItems) {
                BigItemStack newStack = new BigItemStack(defaultItem.stack, defaultItem.count);
                virtualInventory.add(newStack);
            }
            setChanged();
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
        lastUpdateDate = tag.getString("LastUpdateDate");

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
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putString("LastUpdateDate", lastUpdateDate);

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
    }
}
