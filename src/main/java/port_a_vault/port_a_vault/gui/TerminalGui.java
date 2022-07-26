package port_a_vault.port_a_vault.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;

public class TerminalGui extends LightweightGuiDescription {
    public TerminalGui() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(300,200);
    }
}
