package space.miaoning.create_freight.mixin;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagingRequest;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlockEntity;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.miaoning.create_freight.CFBlocks;
import space.miaoning.create_freight.util.NetworkHelper;

import java.util.List;

@Mixin(PackagerBlockEntity.class)
public class PackagerBlockEntityMixin {

    @Inject(method = "attemptToSend", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAttemptToSend(List<PackagingRequest> queuedRequests, CallbackInfo ci) {
        PackagerBlockEntity self = (PackagerBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        Direction targetDirection = self.getBlockState()
                .getValue(PackagerBlock.FACING)
                .getOpposite();

        BlockState targetState = level.getBlockState(self.getBlockPos().relative(targetDirection));
        if (!CFBlocks.SERVER_STORE.has(targetState)) {
            return;
        }

        if (queuedRequests == null || queuedRequests.isEmpty()) {
            ci.cancel();
            return;
        }

        for (Direction d : Iterate.directions) {
            BlockPos adjacentPos = self.getBlockPos().relative(d);
            BlockState adjacentState = level.getBlockState(adjacentPos);
            if (!AllBlocks.STOCK_LINK.has(adjacentState))
                continue;
            if (PackagerLinkBlock.getConnectedDirection(adjacentState) != d)
                continue;

            if (level.getBlockEntity(adjacentPos) instanceof PackagerLinkBlockEntity plbe) {
                if (!NetworkHelper.isServerNetwork(plbe.behaviour.freqId)) {
                    queuedRequests.remove(0);
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
