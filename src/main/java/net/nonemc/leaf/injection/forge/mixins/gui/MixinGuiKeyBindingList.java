package net.nonemc.leaf.injection.forge.mixins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GuiKeyBindingList.class)
public abstract class MixinGuiKeyBindingList extends GuiSlot {

    public MixinGuiKeyBindingList(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    protected int getScrollBarX() {
        return this.width - 5;
    }
}