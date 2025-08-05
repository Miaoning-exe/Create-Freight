package space.miaoning.create_freight.content.serverstore;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import space.miaoning.create_freight.CFBlockEntityTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ServerStoreBlock extends Block implements IBE<ServerStoreBlockEntity> {

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
        return getBlockEntityOptional(level, pos)
                .filter(ServerStoreBlockEntity::hasItems)
                .map(ssBE -> 15)
                .orElse(0);
    }
}
