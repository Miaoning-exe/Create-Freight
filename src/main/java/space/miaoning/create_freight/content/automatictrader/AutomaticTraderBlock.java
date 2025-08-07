package space.miaoning.create_freight.content.automatictrader;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import space.miaoning.create_freight.CFBlockEntityTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AutomaticTraderBlock extends HorizontalDirectionalBlock implements IBE<AutomaticTraderBlockEntity>, IWrenchable {

    public AutomaticTraderBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public Class<AutomaticTraderBlockEntity> getBlockEntityClass() {
        return AutomaticTraderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AutomaticTraderBlockEntity> getBlockEntityType() {
        return CFBlockEntityTypes.AUTOMATIC_TRADER.get();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult blockRayTraceResult) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AutomaticTraderBlockEntity traderBE) {
            if (traderBE.getValidStockTicker(world) == null) {
                player.displayClientMessage(
                        Component.translatable("info.create_freight.no_valid_stock_ticker"),
                        true
                );
            } else {
                player.openMenu(traderBE);
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    // 处理内容物掉落
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (level.isClientSide || state.getBlock() == newState.getBlock()) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof AutomaticTraderBlockEntity trader) {
            Containers.dropContents(level, pos, trader);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }
}
