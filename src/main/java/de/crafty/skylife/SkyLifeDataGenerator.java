package de.crafty.skylife;

import de.crafty.skylife.advancements.SkyLifeAdvancementProvider;
import de.crafty.skylife.loot.SkyLifeLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SkyLifeDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        System.out.println(fabricDataGenerator.getModId());

        pack.addProvider(SkyLifeAdvancementProvider::create);
        pack.addProvider(SkyLifeLootTableProvider::create);

    }

}
