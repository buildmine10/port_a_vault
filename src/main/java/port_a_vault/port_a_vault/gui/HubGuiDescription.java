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

        // button click states
        AtomicBoolean redblackButtonClicked = new AtomicBoolean(true);
        AtomicBoolean redblackButtonClickedNew = new AtomicBoolean(true);

        AtomicBoolean ascButtonClicked = new AtomicBoolean(true);
        AtomicBoolean ascButtonClickedNew = new AtomicBoolean(true);

        AtomicBoolean alphaButtonClicked = new AtomicBoolean(true);
        AtomicBoolean alphaButtonClickedNew = new AtomicBoolean(true);


        WGridPanel invenPanel = new WGridPanel();

        //WItemSlot itemSlots = WItemSlot.of(blockInventory, 0, 9, rows);

        for(int x = 0; x < 9; x++){
            for(int y = 0; y < 5; y++){
                //System.out.println(blockInventory.getStack(x + y * 9).getName().getString());

                WItemSlot itemSlot = WItemSlot.of(blockInventory, x + y * 9);
                invenPanel.add(itemSlot, x, y);
            }
        }



        WTextField searchBar = new WTextField();

        searchBar.setChangedListener(string->{

            getEntity().setSearchQuery(string);
        });

        // text field for setting channel
        WTextField setBar = new WTextField();



        // red black button
        WButton redblackButton = new WButton(Text.literal(""));
        redblackButton.setIcon(new ItemIcon(Port_a_vault.BPLUS_ITEM));

        redblackButton.setOnClick(() -> {
            // This code runs on the client when you click the redblackButton.
            //System.out.println("redblackButton clicked!");
            if (!redblackButtonClicked.get()) {
                redblackButtonClickedNew.set(true);
                redblackButton.setIcon(new ItemIcon(Port_a_vault.BPLUS_ITEM));
                //System.out.println("set to " + redblackButtonClickedNew.get());
                //System.out.println(redblackButtonClickedNew.get());
                getEntity().backend.switchTrees();
            }else{
                redblackButtonClickedNew.set(false);
                redblackButton.setIcon(new ItemIcon(Port_a_vault.REDBLACK_ITEM));
                //System.out.println("set to " + redblackButtonClickedNew.get());
                //System.out.println(redblackButtonClickedNew.get());
                getEntity().backend.switchTrees();
            }
            redblackButtonClicked.set(redblackButtonClickedNew.get());
        });

        // ascending vs descending button
        WButton ascButton = new WButton(Text.literal(""));
        ascButton.setIcon(new ItemIcon(Port_a_vault.UPARROW_ITEM));

        ascButton.setOnClick(() -> {
            // This code runs on the client when you click the ascButton.
            //System.out.println("ascButton clicked!");
            if (!ascButtonClicked.get()) {
                ascButtonClickedNew.set(true);
                ascButton.setIcon(new ItemIcon(Port_a_vault.UPARROW_ITEM));
                //System.out.println("set to " + ascButtonClickedNew.get());
                //System.out.println(ascButtonClickedNew.get());

            }else{
                ascButtonClickedNew.set(false);
                ascButton.setIcon(new ItemIcon(Port_a_vault.DOWNARROW_ITEM));
                //System.out.println("set to " + ascButtonClickedNew.get());
                //System.out.println(ascButtonClickedNew.get());

            }
            ascButtonClicked.set(ascButtonClickedNew.get());
        });

        // alpha vs quantity button
        WButton alphaButton = new WButton(Text.literal(""));
        alphaButton.setIcon(new ItemIcon(Port_a_vault.A_ITEM));

        alphaButton.setOnClick(() -> {
            // This code runs on the client when you click the alphaButton.
            //System.out.println("alphaButton clicked!");
            if (!alphaButtonClicked.get()) {
                alphaButtonClickedNew.set(true);
                alphaButton.setIcon(new ItemIcon(Port_a_vault.A_ITEM));
                //System.out.println("set to " + alphaButtonClickedNew.get());
                //System.out.println(alphaButtonClickedNew.get());

            }else{
                alphaButtonClickedNew.set(false);
                alphaButton.setIcon(new ItemIcon(Port_a_vault.N_ITEM));
                //System.out.println("set to " + alphaButtonClickedNew.get());
                //System.out.println(alphaButtonClickedNew.get());

            }
            alphaButtonClicked.set(alphaButtonClickedNew.get());
        });

        // set channel button
        WButton setButton = new WButton(Text.literal(""));
        setButton.setIcon(new ItemIcon(Port_a_vault.SET_ITEM));

        setButton.setOnClick(() -> {
            String channel = setBar.getText();
            System.out.println(channel);
            getEntity().backend.setChannel(channel);
        });

        root.validate(this);
        //System.out.println("hi");
        root.add(invenPanel, 0, 3+5);
        root.add(searchBar, 0, 0, 41, 10);
        root.add(setButton, 42, 0, 7, 29);
        root.add(setBar, 50, 0, 13, 10);
        root.add(this.createPlayerInventoryPanel(), 0, 39);
        root.add(scrollBar, 57, 8, 5, 30);
        root.add(redblackButton, 56, 43, 7, 29);
        root.add(ascButton, 56, 51, 7, 29);
        root.add(alphaButton, 56, 59, 7, 29);



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
