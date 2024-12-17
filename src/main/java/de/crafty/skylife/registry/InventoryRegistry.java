package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.inventory.BlockBreakerMenu;
import de.crafty.skylife.inventory.FluxFurnaceMenu;
import de.crafty.skylife.inventory.SolidFluidMergerMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.LinkedHashMap;

public class InventoryRegistry {

    private static final LinkedHashMap<ResourceLocation, MenuType<?>> MENU_TYPES = new LinkedHashMap<>();

    public static final MenuType<BlockBreakerMenu> BLOCK_BREAKER = register("block_breaker", BlockBreakerMenu::new);
    public static final MenuType<FluxFurnaceMenu> FLUX_FURNACE = register("flux_furnace", FluxFurnaceMenu::new);
    public static final MenuType<SolidFluidMergerMenu> SOLID_FLUID_MERGER = register("solid_fluid_merger", SolidFluidMergerMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String id, MenuType.MenuSupplier<T> menuSupplier) {
        MenuType<T> menuType = new MenuType<T>(menuSupplier, FeatureFlagSet.of());
        MENU_TYPES.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), menuType);
        return menuType;
    }

    public static void perform() {
        MENU_TYPES.forEach((resourceLocation, menuType) -> {
            Registry.register(BuiltInRegistries.MENU, resourceLocation, menuType);
        });
    }
}
