package net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.dropdown;

import net.nonemc.leaf.utils.ClientUtils;
public class Client {
    private static Client INSTANCE;
    public DropdownGUI dropDownGUI;
    public DropdownGUI getDropDownGUI() {
        return dropDownGUI;
    }
    public static Client getInstance() {

        try {
            if (INSTANCE == null) INSTANCE = new Client();
            return INSTANCE;
        } catch (Throwable t) {
            ClientUtils.getLogger().warn(t);
            throw t;
        }
    }
}
