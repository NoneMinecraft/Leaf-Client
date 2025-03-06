package net.nonemc.leaf.utils.render;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ImageUtils {
    //fix by N0ne
    public static ByteBuffer readImageToBuffer(BufferedImage bufferedImage) {
        int[] rgbArray = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * rgbArray.length);
        for (int rgb : rgbArray) {
            byteBuffer.putInt(rgb << 8 | rgb >> 24 & 255);
        }

        Buffer buffer = byteBuffer;
        buffer.flip();

        return byteBuffer;
    }

    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = buffImg.getGraphics();
        graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        graphics.dispose();
        return buffImg;
    }
}