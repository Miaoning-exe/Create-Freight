package space.miaoning.create_freight.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.datagen.recipe.Recipes;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = CreateFreight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new Recipes(output));

        generator.addProvider(event.includeClient(), new GenBlockStates(output,helper));

        generator.addProvider(event.includeServer(), new GenLootTables(output));
    }
}
