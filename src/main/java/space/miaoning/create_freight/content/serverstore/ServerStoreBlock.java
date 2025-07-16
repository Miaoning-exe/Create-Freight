package space.miaoning.create_freight.content.serverstore;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import space.miaoning.create_freight.CFBlockEntityTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerStoreBlock extends WrenchableDirectionalBlock implements IBE<ServerStoreBlockEntity>, IWrenchable {

    public ServerStoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<ServerStoreBlockEntity> getBlockEntityClass() {
        return ServerStoreBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ServerStoreBlockEntity> getBlockEntityType() {
        return CFBlockEntityTypes.SERVER_STORE.get();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return getBlockEntityOptional(level, pos).map(ssBE -> {
            LazyOptional<IItemHandler> capability = ssBE.getCapability(ForgeCapabilities.ITEM_HANDLER);
            return capability.map(itemHandler -> {
                if (itemHandler.getSlots() == 0) {
                    return 0;
                }
                ItemStack stackInSlot = itemHandler.getStackInSlot(0);
                return stackInSlot.isEmpty() ? 0 : 15;
            }).orElse(0);
        }).orElse(0);
    }
}
