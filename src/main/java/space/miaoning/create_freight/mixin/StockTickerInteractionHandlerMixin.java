package space.miaoning.create_freight.mixin;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerInteractionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import space.miaoning.create_freight.util.NetworkHelper;

import java.util.List;
import java.util.function.Consumer;

@Mixin(StockTickerInteractionHandler.class)
public class StockTickerInteractionHandlerMixin {

    @Redirect(
            method = "interactWithShop",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                    remap = false
            ),
            remap = false
    )
    private static void redirectForEach(List<ItemStack> toTransfer, Consumer<ItemStack> action,
                                        Player player, Level level, BlockPos targetPos, ItemStack mainHandItem) {

        if (level.getBlockEntity(targetPos) instanceof StockTickerBlockEntity tickerBE
                && NetworkHelper.isServerNetwork(tickerBE.behaviour.freqId)) {
            return;
        }
        toTransfer.forEach(action);
    }
}
