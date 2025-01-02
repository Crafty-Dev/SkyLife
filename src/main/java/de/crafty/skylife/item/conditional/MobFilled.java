package de.crafty.skylife.item.conditional;

import com.mojang.serialization.MapCodec;
import de.crafty.skylife.item.MobOrbItem;
import de.crafty.skylife.registry.DataComponentTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record MobFilled() implements ConditionalItemModelProperty {

    public static final MapCodec<MobFilled> MAP_CODEC = MapCodec.unit(new MobFilled());

    @Override
    public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return MobOrbItem.readEntityType(itemStack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag()) != null;
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }
}
