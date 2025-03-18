package net.nonemc.leaf.launch.data.modernui.scriptOnline;

import net.nonemc.leaf.Leaf;

import java.util.ArrayList;
import java.util.List;

public class Subscriptions {
    public static boolean loadingCloud = false;
    public static String tempJs = "";
    public static List<ScriptSubscribe> subscribes = new ArrayList<>();

    public static void addSubscribes(ScriptSubscribe scriptSubscribe) {
        subscribes.add(scriptSubscribe);
        Leaf.fileManager.getSubscriptsConfig().addSubscripts(scriptSubscribe.url, scriptSubscribe.name);
    }
}
