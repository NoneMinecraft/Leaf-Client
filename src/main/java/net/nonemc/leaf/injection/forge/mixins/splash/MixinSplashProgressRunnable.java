//by leaf client

package net.nonemc.leaf.injection.forge.mixins.splash;

import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import net.nonemc.leaf.utils.ClientUtils;
import net.nonemc.leaf.utils.render.AnimatedValue;
import net.nonemc.leaf.utils.render.RenderUtils;
import net.nonemc.leaf.utils.render.EaseUtils.EnumEasingType;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.ProgressManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        targets = {"net.minecraftforge.fml.client.SplashProgress$3"},
        remap = false
)
public abstract class MixinSplashProgressRunnable {
    public MixinSplashProgressRunnable() {
    }

    @Shadow(
            remap = false
    )
    protected abstract void setGL();

    @Shadow(
            remap = false
    )
    protected abstract void clearGL();

    @Inject(
            method = {"run()V"},
            at = {@At("HEAD")},
            remap = false,
            cancellable = true
    )
    private void run(CallbackInfo callbackInfo) {
        callbackInfo.cancel();
        this.setGL();
        GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
        ClientUtils.INSTANCE.logInfo("[Splash] Loading Texture...");
        GL11.glEnable(3553);

        int tex;
        try {
            tex = RenderUtils.loadGlTexture(ImageIO.read(this.getClass().getResourceAsStream("assets/minecraft/leaf/splash.png")));
        } catch (IOException var7) {
            tex = 0;
        }

        GL11.glDisable(3553);
        AnimatedValue animatedValue = new AnimatedValue();
        animatedValue.setType(EnumEasingType.CIRC);
        animatedValue.setDuration(600L);
        ClientUtils.INSTANCE.logInfo("[Splash] Starting Render Thread...");

        for(; !SplashProgress.done; Display.sync(60)) {
            GL11.glClear(16384);
            int width = Display.getWidth();
            int height = Display.getHeight();
            GL11.glViewport(0, 0, width, height);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, (double)width, (double)height, 0.0, -1.0, 1.0);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(3553);
            GL11.glBindTexture(3553, tex);
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f((float)width, 0.0F);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f((float)width, (float)height);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(0.0F, (float)height);
            GL11.glEnd();
            GL11.glDisable(3553);
            float progress = (float)animatedValue.sync((double)getProgress());
            if (progress > 0.0F) {
                this.drawRingProgressBar(50.0F, (float)(height - 50), 25.0F, 20.0F, progress);
            }

            SplashProgress.mutex.acquireUninterruptibly();
            Display.update();
            SplashProgress.mutex.release();
            if (SplashProgress.pause) {
                this.clearGL();
                this.setGL();
            }
        }

        GL11.glDeleteTextures(tex);
        this.clearGL();
    }

    private static float getProgress() {
        float progress = 0.0F;
        Iterator<ProgressManager.ProgressBar> it = ProgressManager.barIterator();
        if (it.hasNext()) {
            ProgressManager.ProgressBar bar = (ProgressManager.ProgressBar)it.next();
            progress = (float)bar.getStep() / (float)bar.getSteps();
        }

        return progress;
    }

    private void drawRingProgressBar(float centerX, float centerY, float outerRadius, float innerRadius, float progress) {
        int segments = 100;
        float angleStep = (float)(6.283185307179586 / (double)segments);
        float progressAngle = (float)(6.283185307179586 * (double)progress);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
        GL11.glBegin(5);

        int i;
        float angle;
        for(i = 0; i <= segments; ++i) {
            angle = angleStep * (float)i;
            GL11.glVertex2f(centerX + (float)Math.cos((double)angle) * outerRadius, centerY + (float)Math.sin((double)angle) * outerRadius);
            GL11.glVertex2f(centerX + (float)Math.cos((double)angle) * innerRadius, centerY + (float)Math.sin((double)angle) * innerRadius);
        }

        GL11.glEnd();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBegin(5);

        for(i = 0; (float)i <= (float)segments * progress; ++i) {
            angle = angleStep * (float)i;
            GL11.glVertex2f(centerX + (float)Math.cos((double)angle) * outerRadius, centerY + (float)Math.sin((double)angle) * outerRadius);
            GL11.glVertex2f(centerX + (float)Math.cos((double)angle) * innerRadius, centerY + (float)Math.sin((double)angle) * innerRadius);
        }

        GL11.glEnd();
    }
}
