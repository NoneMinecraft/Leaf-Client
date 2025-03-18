package net.nonemc.leaf.launch.data.modernui.mainmenu;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.nonemc.leaf.MainValue;
import net.nonemc.leaf.features.module.modules.client.MainMenu;
import net.nonemc.leaf.ui.client.altmanager.GuiAltManager;
import net.nonemc.leaf.utils.MainMenuButton;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import static net.nonemc.leaf.Key.drawBan;

public class ModernGuiMainMenu extends GuiScreen {
    private final ArrayList<MainMenuButton> buttons = new ArrayList<>();
    private ScaledResolution res;
    private float sidebarX = -120.0f;
    private boolean hoveringSidebar = false;
    private final float sidebarWidth = 120.0f;
    private final float sidebarHeight = 250.0f;
    private final float animationSpeed = 5.0f;
    private boolean isAlternativeLayout = false;
    private ShaderProgram backgroundShader;
    private long startTime;

    private Framebuffer framebuffer;
    public ModernGuiMainMenu() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        res = new ScaledResolution(this.mc);
        try {
            if (backgroundShader == null) {
                String mode = MainMenu.Companion.getBackgroundMode().get();
                String fragShader;

                switch (mode) {
                    case "Old":
                        fragShader = "background.frag";
                        break;
                    case "New":
                        fragShader = "background2.frag";
                        break;
                    case "Other":
                        fragShader = "background4.frag";
                        break;
                    case "NoStar":
                        fragShader = "background3.frag";
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown background mode: " + mode);
                }

                backgroundShader = new ShaderProgram(
                        "leaf/shaders/background.vert",
                        "leaf/shaders/" + fragShader
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (framebuffer == null) {
            framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        updateResolution();
        initDefaultLayout();
        super.initGui();
    }
    private void initDefaultLayout() {
        final float buttonWidth = 100.0f;
        final float buttonHeight = 15.0f;
        final float startY = (this.height - sidebarHeight) / 2.0f + 20.0f;

        this.buttons.add(new MainMenuButton(this, 0, "G", "Single", () -> this.mc.displayGuiScreen(new GuiSelectWorld(this)), buttonWidth, buttonHeight, 10 , startY));
        this.buttons.add(new MainMenuButton(this, 1, "H", "Multi", () -> this.mc.displayGuiScreen(new GuiMultiplayer(this)), buttonWidth, buttonHeight, 10, startY + 30.0f));
        this.buttons.add(new MainMenuButton(this, 2, "I", "Alt", () -> this.mc.displayGuiScreen(new GuiAltManager(this)), buttonWidth, buttonHeight, 10, startY + 60.0f));
        this.buttons.add(new MainMenuButton(this, 3, "K", "Option", () -> this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings)), buttonWidth, buttonHeight, 10, startY + 90.0f));
        this.buttons.add(new MainMenuButton(this, 4, "L", "Language", () -> this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager())), buttonWidth, buttonHeight, 10, startY + 120.0f));
    }
    private void drawShaderBackground() {
        if (backgroundShader == null) return;

        framebuffer.bindFramebuffer(true);
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, mc.displayWidth, mc.displayHeight, 0.0, -1.0, 1.0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        backgroundShader.use();

        int resLoc = GL20.glGetUniformLocation(backgroundShader.getProgramId(), "iResolution");
        int timeLoc = GL20.glGetUniformLocation(backgroundShader.getProgramId(), "iTime");
        GL20.glUniform2f(resLoc, mc.displayWidth, mc.displayHeight);
        GL20.glUniform1f(timeLoc, (System.currentTimeMillis() - startTime) / 1000.0f);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(0, mc.displayHeight, 0).endVertex();
        worldRenderer.pos(mc.displayWidth, mc.displayHeight, 0).endVertex();
        worldRenderer.pos(mc.displayWidth, 0, 0).endVertex();
        worldRenderer.pos(0, 0, 0).endVertex();
        tessellator.draw();

        backgroundShader.release();

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        framebuffer.unbindFramebuffer();
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        framebuffer.framebufferRender(mc.displayWidth, mc.displayHeight);
    }
    private void updateResolution() {
        if (framebuffer == null) {
            framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        if (framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            framebuffer.createFramebuffer(mc.displayWidth, mc.displayHeight);
        }
        res = new ScaledResolution(mc);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (res == null || res.getScaledWidth() != mc.displayWidth / res.getScaleFactor()
                || res.getScaledHeight() != mc.displayHeight / res.getScaleFactor()) {
            updateResolution();
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableTexture2D();
        drawShaderBackground();
        GlStateManager.enableTexture2D();

        if (!isAlternativeLayout) {
            hoveringSidebar = mouseX < sidebarWidth && mouseY > (this.height - sidebarHeight) / 2 && mouseY < (this.height + sidebarHeight) / 2;
            sidebarX += (hoveringSidebar ? animationSpeed : -animationSpeed);
            sidebarX = Math.max(-sidebarWidth, Math.min(0, sidebarX));
            GlStateManager.pushMatrix();
            GlStateManager.translate(sidebarX, 0, 0);
        }

        for (MainMenuButton button : buttons) {
            button.draw(mouseX, mouseY);
        }

        if (!isAlternativeLayout) {
            GlStateManager.popMatrix();
        }
        drawBan(mouseX , mouseY);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!MainValue.Companion.getCanRun()) return;
        for (MainMenuButton button : buttons) {
            button.mouseClick(mouseX, mouseY, mouseButton);
        }
    }

    private static class ShaderProgram {
        private final int programId;

        public ShaderProgram(String vertPath, String fragPath) throws IOException {
            int vertShader = createShader(vertPath, GL20.GL_VERTEX_SHADER);
            int fragShader = createShader(fragPath, GL20.GL_FRAGMENT_SHADER);

            programId = GL20.glCreateProgram();
            GL20.glAttachShader(programId, vertShader);
            GL20.glAttachShader(programId, fragShader);
            GL20.glLinkProgram(programId);

            if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("Shader linking failed: " + GL20.glGetProgramInfoLog(programId, 1024));
            }
        }

        private int createShader(String path, int type) throws IOException {
            int shader = GL20.glCreateShader(type);
            String source = readShaderFile(path);
            GL20.glShaderSource(shader, source);
            GL20.glCompileShader(shader);

            if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException("Shader compilation failed: " + GL20.glGetShaderInfoLog(shader, 1024));
            }
            return shader;
        }

        private String readShaderFile(String path) throws IOException {
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(path)).getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, bytesRead));
            }
            in.close();
            return sb.toString();
        }

        public void use() {
            GL20.glUseProgram(programId);
        }

        public void release() {
            GL20.glUseProgram(0);
        }

        public int getProgramId() {
            return programId;
        }
    }
}