package port_a_vault.port_a_vault;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import port_a_vault.port_a_vault.block.CustomChest;
import port_a_vault.port_a_vault.block.CustomChestBlockEntity;
import port_a_vault.port_a_vault.block.CustomChestScreenHandler;


/*
The custom chest works as a chest
The inventory is stored in a hash map in the NetworkGlobals class

If the chest is destroyed
    The inventory ItemStacks that belong to the chest are invalidated using LinkedVariable
    The inventory list for that chest is removed from the hash map

The inventories are not saved to nbt currently so the inventories do not save.
    If the world closes the inventory is cleared
 */
public class Port_a_vault implements ModInitializer {

    public static final Block CUSTOM_CHEST_BLOCK = new CustomChest(FabricBlockSettings.of(Material.METAL));
    public static final Item CUSTOM_CHEST_ITEM = new BlockItem(CUSTOM_CHEST_BLOCK, new FabricItemSettings().group(ItemGroup.MISC));
    public static final BlockEntityType<CustomChestBlockEntity> CUSTOM_CHEST_ENTITY = FabricBlockEntityTypeBuilder.create(CustomChestBlockEntity::new, CUSTOM_CHEST_BLOCK).build(null);
    public static final ScreenHandlerType<CustomChestScreenHandler> CUSTOM_CHEST_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("port_a_vault", "custom_chest"), CustomChestScreenHandler::new);


    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("port_a_vault", "custom_chest"), CUSTOM_CHEST_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("port_a_vault", "custom_chest"), CUSTOM_CHEST_ITEM);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("port_a_vault", "custom_chest"), CUSTOM_CHEST_ENTITY);
        //Registry.register(Registry.SCREEN_HANDLER, new Identifier("port_a_vault", "custom_chest"), CUSTOM_CHEST_SCREEN_HANDLER);
        ScreenRegistry.register(CUSTOM_CHEST_SCREEN_HANDLER, GenericContainerScreen::new);
    }
}
