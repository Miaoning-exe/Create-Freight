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
 * 在 Server-side 触发。
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

    /**
     * @return 触发该事件的结构实例 (StructureStart)。
     */
    public StructureStart getStructureStart() {
        return structureStart;
    }

    /**
     * @return 结构所属的 Structure 类型。
     */
    public Structure getStructure() {
        return structureStart.getStructure();
    }

    /**
     * @return 结构所在的服务器世界。
     */
    public ServerLevel getLevel() {
        return level;
    }

    /**
     * @return 正在生成结构的区块。
     */
    public ChunkAccess getChunk() {
        return chunk;
    }

    /**
     * @return 代表该结构实例的唯一 NBT 标签。
     */
    public CompoundTag getStructureNbt() {
        RegistryAccess registryAccess = this.level.registryAccess();
        StructurePieceSerializationContext serializationContext = new StructurePieceSerializationContext(this.level, registryAccess);
        return structureStart.createTag(serializationContext, this.chunk.getPos());
    }
}
