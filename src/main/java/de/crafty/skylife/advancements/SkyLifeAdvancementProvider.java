package de.crafty.skylife.advancements;

import de.crafty.skylife.advancements.sub.SkyLifeHammeringAdvancementProvider;
import de.crafty.skylife.advancements.sub.SkyLifeStoryAdvancementProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkyLifeAdvancementProvider {


    public static AdvancementProvider create(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> consumer){
        return new AdvancementProvider(packOutput, consumer, List.of(
                new SkyLifeStoryAdvancementProvider(),
                new SkyLifeHammeringAdvancementProvider()
        ));
    }
}
