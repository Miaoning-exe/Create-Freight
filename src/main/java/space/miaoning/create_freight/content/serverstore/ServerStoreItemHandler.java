package space.miaoning.create_freight.content.serverstore;

import com.simibubi.create.content.logistics.BigItemStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerStoreItemHandler extends ItemStackHandler {

    private final Supplier<List<BigItemStack>> virtualInventorySupplier;

    public ServerStoreItemHandler(Supplier<List<BigItemStack>> virtualInventorySupplier) {
        this.virtualInventorySupplier = virtualInventorySupplier;
    }

    @Override
    public int getSlots() {
        List<BigItemStack> inventory = virtualInventorySupplier.get();
        return inventory != null ? inventory.size() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        List<BigItemStack> inventory = virtualInventorySupplier.get();
        if (inventory == null || slot < 0 || slot >= inventory.size()) {
            return ItemStack.EMPTY;
        }

        BigItemStack stack = inventory.get(slot);
        if (stack == null || stack.count <= 0) {
            return ItemStack.EMPTY;
        }

        return stack.stack.copyWithCount(stack.count);
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
        List<BigItemStack> inventory = virtualInventorySupplier.get();
        if (inventory == null || slot < 0 || slot >= inventory.size()) {
            return ItemStack.EMPTY;
        }

        BigItemStack stack = inventory.get(slot);
        if (stack == null || stack.count <= 0) {
            return ItemStack.EMPTY;
        }

        int extractAmount = Math.min(stack.count, amount);

        if (extractAmount <= 0) {
            return ItemStack.EMPTY;
        }

        if (!simulate) {
            stack.count -= extractAmount;
        }

        return stack.stack.copyWithCount(extractAmount);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public int getSlotLimit(int slot) {
        List<BigItemStack> inventory = virtualInventorySupplier.get();
        if (inventory == null || slot < 0 || slot >= inventory.size()) {
            return 0;
        }

        BigItemStack stack = inventory.get(slot);
        if (stack == null || stack.count <= 0) {
            return 0;
        }
        return stack.count;
    }
}
