package net.nonemc.leaf.ui.mainmenu.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public final class Image {
    public static void drawImage(String imagePath, int x, int y, int width, int height, float alpha) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1.0F, 1.0F, 1.0F, alpha);
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);
            return;
        }
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * imgWidth * imgHeight);
        for (int yIndex = 0; yIndex < imgHeight; yIndex++) {
            for (int xIndex = 0; xIndex < imgWidth; xIndex++) {
                int rgb = image.getRGB(xIndex, yIndex);
                buffer.put((byte) ((rgb >> 16) & 0xFF));
                buffer.put((byte) ((rgb >> 8) & 0xFF));
                buffer.put((byte) (rgb & 0xFF));
                buffer.put((byte) ((rgb >> 24) & 0xFF));
            }
        }
        ((Buffer) buffer).flip();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imgWidth, imgHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(x, y);
        glTexCoord2f(1, 0); glVertex2f(x + width, y);
        glTexCoord2f(1, 1); glVertex2f(x + width, y + height);
        glTexCoord2f(0, 1); glVertex2f(x, y + height);
        glEnd();
        glDeleteTextures(textureID);
        glEnable(GL_DEPTH_TEST);
    }

}