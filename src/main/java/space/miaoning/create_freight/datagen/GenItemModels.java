package space.miaoning.create_freight.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import space.miaoning.create_freight.CreateFreight;

@SuppressWarnings("removal")
public class GenItemModels extends ItemModelProvider {
    public GenItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateFreight.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
    }

    private void itemGeneratedModel(Item item) {
        withExistingParent(itemName(item), "item/generated").texture("layer0", resourceItem(ForgeRegistries.ITEMS.getKey(item).getPath()));
    }

    private String itemName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item).getPath();
    }

    private ResourceLocation resourceItem(String path) {
        return new ResourceLocation(CreateFreight.MODID, "item/" + path);
    }
}
