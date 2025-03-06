package net.nonemc.leaf.features.module.modules.render;

import net.minecraft.entity.EntityLivingBase;
import net.nonemc.leaf.event.AttackEvent;
import net.nonemc.leaf.event.EventTarget;
import net.nonemc.leaf.event.MotionEvent;
import net.nonemc.leaf.event.Render3DEvent;
import net.nonemc.leaf.features.module.Module;
import net.nonemc.leaf.features.module.ModuleCategory;
import net.nonemc.leaf.features.module.ModuleInfo;
import net.nonemc.leaf.utils.particles.EvictingList;
import net.nonemc.leaf.utils.particles.Particle;
import net.nonemc.leaf.utils.particles.Vec3;
import net.nonemc.leaf.utils.render.RenderUtils;
import net.nonemc.leaf.utils.timer.ParticleTimer;
import net.nonemc.leaf.value.BoolValue;
import net.nonemc.leaf.value.IntegerValue;

import java.util.List;

@ModuleInfo(name = "Particles", category = ModuleCategory.RENDER)
public final class Particles extends Module {

    private final IntegerValue amount = new IntegerValue("Amount", 10, 1, 20);

    private final BoolValue physics = new BoolValue("Physics", true);

    private final List<Particle> particles = new EvictingList<>(100);
    private final ParticleTimer timer = new ParticleTimer();
    private EntityLivingBase target;

    @EventTarget
    public void onAttack(final AttackEvent event) {
        if (event.getTargetEntity() instanceof EntityLivingBase)
            target = (EntityLivingBase) event.getTargetEntity();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if (target != null && target.hurtTime >= 9 && mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < 10) {
            for (int i = 0; i < amount.get(); i++)
                particles.add(new Particle(new Vec3(target.posX + (Math.random() - 0.5) * 0.5, target.posY + Math.random() * 1 + 0.5, target.posZ + (Math.random() - 0.5) * 0.5)));

            target = null;
        }
    }

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (particles.isEmpty())
            return;

        for (int i = 0; i <= timer.getElapsedTime() / 1E+11; i++) {
            if (physics.get())
                particles.forEach(Particle::update);
            else
                particles.forEach(Particle::updateWithoutPhysics);
        }

        particles.removeIf(particle -> mc.thePlayer.getDistanceSq(particle.position.xCoord, particle.position.yCoord, particle.position.zCoord) > 50 * 10);

        timer.reset();

        RenderUtils.renderParticles(particles);
    }
}