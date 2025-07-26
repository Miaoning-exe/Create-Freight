package space.miaoning.create_freight.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeWithWeight {
    public static final BiomeWithWeight EMPTY = new BiomeWithWeight(null, 0);

    private final ResourceLocation biome;
    private final int weight;

    public BiomeWithWeight(ResourceLocation biome, int weight) {
        this.biome = biome;
        this.weight = weight;
    }

    public ResourceLocation getBiome() {
        return biome;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isEmpty() {
        return biome == null;
    }

    public JsonElement serialize() {
        JsonObject json = new JsonObject();

        json.addProperty("biome",biome.toString());
        json.addProperty("weight",weight);

        return json;
    }

    public static BiomeWithWeight deserialize(JsonElement jsonElement) {
        if (!jsonElement.isJsonObject()) throw new JsonSyntaxException("Must be a json object");

        JsonObject json = jsonElement.getAsJsonObject();

        String id = GsonHelper.getAsString(json,"biome");
        int weight = GsonHelper.getAsInt(json,"weight");

        return new BiomeWithWeight(new ResourceLocation(id),weight);
    }

    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeResourceLocation(biome);
        pBuffer.writeInt(weight);
    }

    public static BiomeWithWeight read(FriendlyByteBuf pBuffer) {
        ResourceLocation biome = pBuffer.readResourceLocation();
        int weight = pBuffer.readInt();

        return new BiomeWithWeight(biome,weight);
    }
}
