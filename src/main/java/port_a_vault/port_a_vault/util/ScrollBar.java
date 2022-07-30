package port_a_vault.port_a_vault.util;

import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import port_a_vault.port_a_vault.block.HubBlockEntity;
import port_a_vault.port_a_vault.gui.HubGuiDescription;
import port_a_vault.port_a_vault.gui.HubScreen;

public class ScrollBar extends WScrollBar{

    public boolean dragging = false;
    public ScrollBar(Axis axis) {
        super(axis);
    }

    @Override
    public InputResult onMouseDrag(int x, int y, int button, double deltaX, double deltaY) {
        //System.out.println("dragging");
        //System.out.println(deltaY);
        //entity.scroll((int)deltaY);
        return super.onMouseDrag(x, y, button, deltaX, deltaY);
    }

    @Override
    public InputResult onMouseScroll(int x, int y, double amount) {
        //System.out.println("scrolling");
        //System.out.println(amount);
        //onMouseDrag(x, y, 0, 0, 0);
        return super.onMouseScroll(x, y, amount);
    }
}
