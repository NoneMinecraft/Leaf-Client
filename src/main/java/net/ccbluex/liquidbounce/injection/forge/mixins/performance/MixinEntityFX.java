package net.ccbluex.liquidbounce.injection.forge.mixins.performance;

import net.minecraft.client.particle.EntityFX;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={EntityFX.class})
public class MixinEntityFX {

}
