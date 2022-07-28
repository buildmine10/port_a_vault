package port_a_vault.port_a_vault.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.gui.HubGuiDescription;
import port_a_vault.port_a_vault.util.AccessPointBackend;

public class HubBlockEntity extends LootableContainerBlockEntity implements NamedScreenHandlerFactory {

    AccessPointBackend backend = new AccessPointBackend();
    String channel = "";
    int scrollAmount = 0;
    DefaultedList<ItemStack> displayStacks = DefaultedList.ofSize(9 * 5, ItemStack.EMPTY);

    protected HubBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }
    public HubBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(Port_a_vault.HUB_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("name 2");
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return super.createMenu(syncId, inv, player);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new HubGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, this.getPos()));
    }

    @Override
    public int size() {
        return 9 * 5;
    }

    @Override
    public boolean isEmpty() {
        this.checkLootInteraction(null);
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        this.checkLootInteraction(null);
        return displayStacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        this.checkLootInteraction(null);
        return displayStacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        //System.out.println("hi");
        this.checkLootInteraction(null);
        return displayStacks.get(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.checkLootInteraction(null);

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {

    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return null;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {}

    public void scroll(int amount){
        scrollAmount += amount;
    }

    private void updateDisplayList(){

    }
}
