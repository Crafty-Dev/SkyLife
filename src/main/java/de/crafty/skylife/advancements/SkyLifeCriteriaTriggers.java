package de.crafty.skylife.advancements;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.advancements.criterion.SkyLifeJoinTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;

public class SkyLifeCriteriaTriggers {

    private static final LinkedHashMap<ResourceLocation, CriterionTrigger<?>> TRIGGERS = new LinkedHashMap<>();

    public static final SkyLifeJoinTrigger ENTERED_SKYLIFE = register("entered_skylife", new SkyLifeJoinTrigger());


    public static <T extends CriterionTrigger<?>> T register(String id, T criterionTrigger) {
        TRIGGERS.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), criterionTrigger);
        return criterionTrigger;
    }

    public static void perform(){
        TRIGGERS.forEach((resourceLocation, criterionTrigger) -> {
            Registry.register(BuiltInRegistries.TRIGGER_TYPES, resourceLocation, criterionTrigger);
        });
    }
}
