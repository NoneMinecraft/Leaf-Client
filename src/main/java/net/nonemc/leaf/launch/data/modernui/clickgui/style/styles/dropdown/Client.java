package net.nonemc.leaf.launch.data.modernui.clickgui.style.styles.dropdown;


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
            throw t;
        }
    }
}
