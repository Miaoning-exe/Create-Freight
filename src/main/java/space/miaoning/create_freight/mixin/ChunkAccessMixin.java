package space.miaoning.create_freight.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.miaoning.create_freight.event.StructureGeneratedEvent;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin {
    @Inject(
            method = "setStartForStructure(Lnet/minecraft/world/level/levelgen/structure/Structure;Lnet/minecraft/world/level/levelgen/structure/StructureStart;)V",
            at = @At("HEAD")
    )
    private void onSetStartStructureStart(Structure structure, StructureStart start, CallbackInfo ci) {
        ChunkAccess self = (ChunkAccess) (Object) this;
        if (self instanceof LevelChunk levelChunk && !levelChunk.getLevel().isClientSide()) {

            ServerLevel serverLevel = (ServerLevel) levelChunk.getLevel();

            StructureGeneratedEvent event = new StructureGeneratedEvent(start, serverLevel, self);

            MinecraftForge.EVENT_BUS.post(event);
        }
    }
}
