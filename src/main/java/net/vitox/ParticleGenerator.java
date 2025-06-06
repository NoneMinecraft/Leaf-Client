package net.vitox;

import net.minecraft.client.Minecraft;
import net.nonemc.leaf.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ParticleGenerator {

    private final List<Particle> particles = new ArrayList<>();
    private final int amount;

    private int prevWidth;
    private int prevHeight;

    public ParticleGenerator(final int amount) {
        this.amount = amount;
    }

    public void draw(final int mouseX, final int mouseY) {
        if (Minecraft.getMinecraft() == null)
            return;
        if (particles.isEmpty() || prevWidth != Minecraft.getMinecraft().displayWidth || prevHeight != Minecraft.getMinecraft().displayHeight) {
            particles.clear();
            create();
        }

        prevWidth = Minecraft.getMinecraft().displayWidth;
        prevHeight = Minecraft.getMinecraft().displayHeight;

        for (final Particle particle : particles) {
            particle.fall();
            particle.interpolation();

            int range = 50;
            final boolean mouseOver = (mouseX >= particle.x - range) && (mouseY >= particle.y - range) && (mouseX <= particle.x + range) && (mouseY <= particle.y + range);

            if (mouseOver) {
                particles.stream()
                        .filter(part -> (part.getX() > particle.getX() && part.getX() - particle.getX() < range
                                && particle.getX() - part.getX() < range)
                                && (part.getY() > particle.getY() && part.getY() - particle.getY() < range
                                || particle.getY() > part.getY() && particle.getY() - part.getY() < range))
                        .forEach(connectable -> particle.connect(connectable.getX(), connectable.getY()));
            }

            RenderUtils.drawCircle(particle.getX(), particle.getY(), particle.size, 0xffFFFFFF);
        }
    }

    private void create() {
        final Random random = new Random();

        for (int i = 0; i < amount; i++)
            particles.add(new Particle(random.nextInt(Minecraft.getMinecraft().displayWidth), random.nextInt(Minecraft.getMinecraft().displayHeight)));
    }
}