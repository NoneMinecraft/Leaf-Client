package net.nonemc.leaf.features.module.modules.render;

import net.nonemc.leaf.event.EventTarget;
import net.nonemc.leaf.event.Render3DEvent;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.features.module.ModuleInfo;
import net.nonemc.leaf.libs.render.RenderWings;
import net.nonemc.leaf.value.BoolValue;

@ModuleInfo(name = "Wings", category = ModuleCategory.RENDER, array = false)
public class Wings extends Module {

    private final BoolValue onlyThirdPerson = new BoolValue("OnlyThirdPerson", true);

    @EventTarget
    public void onRenderPlayer(Render3DEvent event) {
        if (onlyThirdPerson.get() && mc.gameSettings.thirdPersonView == 0) return;

        RenderWings renderWings = new RenderWings();
        renderWings.renderWings(event.getPartialTicks());
    }

}
