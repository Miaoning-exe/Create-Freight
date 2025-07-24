package space.miaoning.create_freight.event;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraftforge.eventbus.api.Event;

/**
 * 当一个结构实例 (StructureStart) 成功生成并即将被写入区块数据时触发。
 * 这个事件不是可取消的，主要用于在结构生成后获取其数据。
 */
public class StructureGeneratedEvent extends Event {
    private final StructureStart structureStart;
    private final ServerLevel level;
    private final ChunkAccess chunk;

    public StructureGeneratedEvent(StructureStart structureStart, ServerLevel level, ChunkAccess chunk) {
        this.structureStart = structureStart;
        this.level = level;
        this.chunk = chunk;
    }

    public StructureStart getStructureStart() {
        return structureStart;
    }

    public Structure getStructure() {
        return structureStart.getStructure();
    }

    public ServerLevel getLevel() {
        return level;
    }

    public ChunkAccess getChunk() {
        return chunk;
    }
}
