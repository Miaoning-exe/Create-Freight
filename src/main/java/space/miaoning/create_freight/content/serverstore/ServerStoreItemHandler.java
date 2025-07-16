package space.miaoning.create_freight.content.serverstore;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerStoreItemHandler extends ItemStackHandler {

    private final Supplier<List<ItemStack>> virtualInventorySupplier;

    public ServerStoreItemHandler(Supplier<List<ItemStack>> virtualInventorySupplier) {
        this.virtualInventorySupplier = virtualInventorySupplier;
    }

    @Override
    public int getSlots() {
        List<ItemStack> inventory = virtualInventorySupplier.get();
        return inventory != null ? inventory.size() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        List<ItemStack> inventory = virtualInventorySupplier.get();
        if (inventory == null || slot < 0 || slot >= inventory.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = inventory.get(slot);
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount());
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        List<ItemStack> inventory = virtualInventorySupplier.get();
        if (inventory == null || slot < 0 || slot >= inventory.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = inventory.get(slot);
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int extractAmount = Math.min(stack.getCount(), amount);
        extractAmount = Math.min(extractAmount, stack.getMaxStackSize());

        if (extractAmount <= 0) {
            return ItemStack.EMPTY;
        }

        if (!simulate) {
            stack.shrink(extractAmount);
            if (stack.isEmpty()) {
                inventory.set(slot, ItemStack.EMPTY);
            }
        }

        return ItemHandlerHelper.copyStackWithSize(stack, extractAmount);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public int getSlotLimit(int slot) {
        List<ItemStack> inventory = virtualInventorySupplier.get();
        if (inventory == null || slot < 0 || slot >= inventory.size()) {
            return 0;
        }

        ItemStack stack = inventory.get(slot);
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        return stack.getCount();
    }
}
