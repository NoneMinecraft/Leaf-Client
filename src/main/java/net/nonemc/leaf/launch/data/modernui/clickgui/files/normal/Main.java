package net.nonemc.leaf.launch.data.modernui.clickgui.files.normal;

import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.features.module.ModuleManager;

import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static int categoryCount;

    public static boolean reloadModules;

    public static float allowedClickGuiHeight = 300;

    /**
     * 一个个添加
     */

    public static List<Module> getModulesInCategory(ModuleCategory c, ModuleManager moduleManager) {
        return moduleManager.getModules().stream().filter(m -> m.getCategory() == c).collect(Collectors.toList());
    }

}
