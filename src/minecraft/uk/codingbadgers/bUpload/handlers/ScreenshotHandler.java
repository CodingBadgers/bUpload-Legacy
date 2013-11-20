package uk.codingbadgers.bUpload.handlers;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;

import org.lwjgl.BufferUtils;

import uk.codingbadgers.bUpload.Screenshot;
import uk.codingbadgers.bUpload.handlers.upload.UploadType;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class ScreenshotHandler {

    private static final Minecraft minecraft = Minecraft.getMinecraft();

    private static int[] PIXEL_ARRAY = null;
    private static IntBuffer PIXEL_BUFFER = null;
    
    public static Screenshot createScreenshot() {
        Screenshot shot = new Screenshot();

        try {
            int screenSize = minecraft.displayWidth * minecraft.displayHeight;

            if (PIXEL_BUFFER == null || PIXEL_BUFFER.capacity() < screenSize) {
                PIXEL_BUFFER = BufferUtils.createIntBuffer(screenSize);
                PIXEL_ARRAY = new int[screenSize];
            }

            glPixelStorei(GL_PACK_ALIGNMENT, 1);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            PIXEL_BUFFER.clear();
            glReadPixels(0, 0, minecraft.displayWidth, minecraft.displayHeight, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, PIXEL_BUFFER);
            PIXEL_BUFFER.get(PIXEL_ARRAY);
            copyScreenBuffer(PIXEL_ARRAY, minecraft.displayWidth, minecraft.displayHeight);
            shot.image = new BufferedImage(minecraft.displayWidth, minecraft.displayHeight, 1);
            shot.image.setRGB(0, 0, minecraft.displayWidth, minecraft.displayHeight, PIXEL_ARRAY, 0, minecraft.displayWidth);
            shot.imageID = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), shot.image);
        } catch (Exception ex) {
            ex.printStackTrace();
            shot.image = null;
            shot.imageID = 0;
        }
        
        return shot;
    }

    private static void copyScreenBuffer(int[] buffer, int width, int height) {
        int[] var3 = new int[width];
        int halfHeight = height / 2;

        for (int index = 0; index < halfHeight; ++index) {
            System.arraycopy(buffer, index * width, var3, 0, width);
            System.arraycopy(buffer, (height - 1 - index) * width, buffer, index * width, width);
            System.arraycopy(var3, 0, buffer, (height - 1 - index) * width, width);
        }
    }
    
    public static void handleScreenshot() {
        Screenshot screen = createScreenshot();
        boolean upload = false;
        
        if (ConfigHandler.SAVE_FILE) {
            runHandler(UploadType.FILE, screen);
            upload = true;
        }
          
        if (ConfigHandler.SAVE_IMGUR) {
            runHandler(UploadType.IMGUR, screen);
            upload = true;
        }
        
        if (ConfigHandler.SAVE_FTP) {
            runHandler(UploadType.FTP, screen);
            upload = true;
        }
        
        if (!upload) {
            MessageHandler.sendChatMessage("image.upload.noupload");
        }
    }
    
    private static void runHandler(UploadType type, Screenshot screen) {
        Thread thread = new Thread(type.newHandler(screen));
        thread.start();
    }
}