package net.nonemc.leaf.launch.data.modernui.scriptOnline;

import net.nonemc.leaf.Leaf;
import net.nonemc.leaf.script.Script;

import java.io.File;
import java.util.Objects;

public class ScriptSubscribe {
    public String url;
    public String name;
    public boolean state = true;

    public ScriptSubscribe(String url, String name) {
        if (Objects.equals(name, "")) name = url;
        this.url = url;
        this.name = name;
    }

    public void load() {
        Subscriptions.loadingCloud = true;
        for (String script : OnlineScriptLoader.getScriptsBySubscribe(url)) {
            Subscriptions.tempJs = script;
            Script script1 = new Script(new File("CloudLoad"));
            Leaf.scriptManager.getScripts().add(script1);
        }
        Subscriptions.loadingCloud = false;
    }
}
