package net.nonemc.leaf.launch.data.modernui.scriptOnline;

import net.nonemc.leaf.script.Script;

public class OnlineScriptManager {
    public static boolean isOnlineScript(Script script) {
        return script.isOnline();
    }
}
