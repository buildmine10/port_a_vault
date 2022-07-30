package port_a_vault.port_a_vault.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import port_a_vault.port_a_vault.Port_a_vault;
import port_a_vault.port_a_vault.block.HubBlockEntity;
import port_a_vault.port_a_vault.util.ScrollBar;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HubGuiDescription extends SyncedGuiDescription {

    private static final int PROPERTY_COUNT = 3;
    private static final int INVENTORY_SIZE = 9*5;

    private static HashMap<BlockPos, HubBlockEntity> entities = new HashMap<>();
    public static ScrollBar scrollBar = new ScrollBar(Axis.VERTICAL);


    public HubGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Port_a_vault.HUB_SCREEN_HANDLER_TYPE , syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context, PROPERTY_COUNT));
        WGridPanel root = new WGridPanel(3);
        setRootPanel(root);
        //root.setSize(18 * 9, 200);
        root.setInsets(Insets.ROOT_PANEL);
        AtomicBoolean buttonClicked = new AtomicBoolean(true);
        AtomicBoolean buttonClickedNew = new AtomicBoolean(true);


        WGridPanel invenPanel = new WGridPanel();

        //WItemSlot itemSlots = WItemSlot.of(blockInventory, 0, 9, rows);

        for(int x = 0; x < 9; x++){
            for(int y = 0; y < 5; y++){
                //System.out.println(blockInventory.getStack(x + y * 9).getName().getString());

                WItemSlot itemSlot = WItemSlot.of(blockInventory, x + y * 9);
                invenPanel.add(itemSlot, x, y);
            }
        }

        root.add(invenPanel, 0, 3+5);

        WTextField searchBar = new WTextField();

        searchBar.setChangedListener(string->{

            getEntity().setSearchQuery(string);
        });

        root.add(searchBar, 0, 0, 6*9, 10);


        WButton button = new WButton(Text.literal(""));
        button.setIcon(new ItemIcon(Items.SAND));

        button.setOnClick(() -> {
            // This code runs on the client when you click the button.
            //System.out.println("Button clicked!");
            if (!buttonClicked.get()) {
                buttonClickedNew.set(true);
                button.setIcon(new ItemIcon(Items.SAND));
                //System.out.println("set to " + buttonClickedNew.get());
                //System.out.println(buttonClickedNew.get());
                getEntity().backend.switchTrees();
            }else{
                buttonClickedNew.set(false);
                button.setIcon(new ItemIcon(Items.REDSTONE));
                //System.out.println("set to " + buttonClickedNew.get());
                //System.out.println(buttonClickedNew.get());
                getEntity().backend.switchTrees();
            }
            buttonClicked.set(buttonClickedNew.get());
        });
        //button.setIcon(new ItemIcon(Items.SAND));

        //WToggleButton toggleButton = new WToggleButton(tex,tex);


        root.add(this.createPlayerInventoryPanel(), 0, 39);

        root.validate(this);
        //System.out.println("hi");
        root.add(scrollBar, 55, 0, 5, 38);
        root.add(button, 55, 42, 7, 19);

//        toggleButton.setOnToggle(on -> {
//            // This code runs on the client when you toggle the button.
//            System.out.println("Toggle button toggled to " + (on ? "on" : "off"));
//        });

        //TODO: add armor slots??//no


        if(!world.isClient){
            BlockPos pos = new BlockPos(getPropertyDelegate().get(0), getPropertyDelegate().get(1), getPropertyDelegate().get(2));
            HubBlockEntity entity = (HubBlockEntity)world.getBlockEntity(pos);
            entities.put(pos, entity);
            getEntity().setSearchQuery("");
        }
    }

    public HubBlockEntity getEntity(){
        //System.out.print(new BlockPos(getPropertyDelegate().get(0), getPropertyDelegate().get(1), getPropertyDelegate().get(2)).toShortString());
        //System.out.println(entities.get(new BlockPos(getPropertyDelegate().get(0), getPropertyDelegate().get(1), getPropertyDelegate().get(2))));
        return entities.get(new BlockPos(getPropertyDelegate().get(0), getPropertyDelegate().get(1), getPropertyDelegate().get(2)));
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        getEntity().putAway();
    }
}
