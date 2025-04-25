package net.nonemc.leaf.ui.clickgui;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.nonemc.leaf.file.FileManager;
import net.nonemc.leaf.file.FileConfigManager;
import net.nonemc.leaf.ui.UILaunchOption;
import net.nonemc.leaf.ui.clickgui.Panel;
import net.nonemc.leaf.ui.clickgui.elements.Element;
import net.nonemc.leaf.ui.clickgui.elements.ModuleElement;

import java.io.File;

public class ClickGuiConfig extends FileManager {

    public ClickGuiConfig(final File file) {
        super(file);
    }

    @Override
    public void loadConfig(String config) {
        final JsonElement jsonElement = new JsonParser().parse(config);

        if (jsonElement instanceof JsonNull)
            return;

        final JsonObject jsonObject = (JsonObject) jsonElement;

        for (final Panel panel : UILaunchOption.clickGui.panels) {
            if (!jsonObject.has(panel.getCategory().getConfigName()))
                continue;

            try {
                final JsonObject panelObject = jsonObject.getAsJsonObject(panel.getCategory().getConfigName());

                panel.setOpen(panelObject.get("open").getAsBoolean());
                panel.setVisible(panelObject.get("visible").getAsBoolean());
                panel.setX(panelObject.get("posX").getAsInt());
                panel.setY(panelObject.get("posY").getAsInt());

                for (final Element element : panel.getElements()) {
                    if (!(element instanceof ModuleElement))
                        continue;

                    final ModuleElement moduleElement = (ModuleElement) element;

                    if (!panelObject.has(moduleElement.getModule().getName()))
                        continue;

                    try {
                        final JsonObject elementObject = panelObject.getAsJsonObject(moduleElement.getModule().getName());

                        moduleElement.setShowSettings(elementObject.get("Settings").getAsBoolean());
                    } catch (final Exception e) {
                    }
                }
            } catch (final Exception e) {
            }
        }
    }

    @Override
    public String saveConfig() {
        final JsonObject jsonObject = new JsonObject();

        for (final Panel panel : UILaunchOption.clickGui.panels) {
            final JsonObject panelObject = new JsonObject();

            panelObject.addProperty("open", panel.getOpen());
            panelObject.addProperty("visible", panel.isVisible());
            panelObject.addProperty("posX", panel.getX());
            panelObject.addProperty("posY", panel.getY());

            for (final Element element : panel.getElements()) {
                if (!(element instanceof ModuleElement))
                    continue;

                final ModuleElement moduleElement = (ModuleElement) element;

                final JsonObject elementObject = new JsonObject();

                elementObject.addProperty("Settings", moduleElement.isShowSettings());

                panelObject.add(moduleElement.getModule().getName(), elementObject);
            }

            jsonObject.add(panel.getCategory().getConfigName(), panelObject);
        }

        return FileConfigManager.Companion.getPRETTY_GSON().toJson(jsonObject);
    }
}