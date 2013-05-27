/*
 *  bUpload - a minecraft mod which improves the existing screenshot functionality
 *  Copyright (C) 2013 TheCodingBadgers
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package uk.codingbadgers.bUpload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "TheCodingBadgers-bUpload", name = "bUpload", version = "1.5.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class bUpload
{
    /** A pixel buffer to store the screen shot in */
    private static IntBuffer 					PIXEL_BUFFER = null;

    /** A pixel array to store the pixel buffer in so we can convert it to an image an upload */
    private static int[] 						PIXEL_ARRAY = null;

    /** Prefix for colour codes */
    public final static char					COLOUR = 167;

    /** Session history of uploads */
    private static ArrayList<UploadedImage>		m_uploadHistory = new ArrayList<UploadedImage>();

    /** The last screenshot taken */
    private bUploadScreenShot					m_lastScreenshot = new bUploadScreenShot();

    /** */
    private static final DateFormat 			DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    
    public static Configuration 				CONFIG = null;
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) {

    	CONFIG = new Configuration(event.getSuggestedConfigurationFile());
    	          
    }
    
    @Init
    public void load(FMLInitializationEvent event)
    {
        KeyBindingRegistry.registerKeyBinding(new bUploadKeyHandler(this));
    }

    public void createScreenshot()
    {
        Minecraft minecraft = ModLoader.getMinecraftInstance();

        try
        {
            int screenSize = minecraft.displayWidth * minecraft.displayHeight;

            if (PIXEL_BUFFER == null || PIXEL_BUFFER.capacity() < screenSize)
            {
                PIXEL_BUFFER = BufferUtils.createIntBuffer(screenSize);
                PIXEL_ARRAY = new int[screenSize];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            PIXEL_BUFFER.clear();
            GL11.glReadPixels(0, 0, minecraft.displayWidth, minecraft.displayHeight, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, PIXEL_BUFFER);
            PIXEL_BUFFER.get(PIXEL_ARRAY);
            copyScreenBuffer(PIXEL_ARRAY, minecraft.displayWidth, minecraft.displayHeight);
            m_lastScreenshot.image = new BufferedImage(minecraft.displayWidth, minecraft.displayHeight, 1);
            m_lastScreenshot.image.setRGB(0, 0, minecraft.displayWidth, minecraft.displayHeight, PIXEL_ARRAY, 0, minecraft.displayWidth);
            m_lastScreenshot.imageID = minecraft.renderEngine.allocateAndSetupTexture(m_lastScreenshot.image);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            m_lastScreenshot.image = null;
            m_lastScreenshot.imageID = 0;
        }
    }

    public void uploadScreenShot()
    {
        Minecraft minecraft = ModLoader.getMinecraftInstance();

        if (m_lastScreenshot.image != null && m_lastScreenshot.imageID != 0)
        {
            minecraft.ingameGUI.getChatGUI().printChatMessage(COLOUR + "6[bUpload] " + COLOUR + "FUploading image to Imgur...");
            ImageUploadThread uploadThread = new ImageUploadThread(m_lastScreenshot, minecraft);
            new Thread(uploadThread).start();
        }
    }

    public void saveScreenshotToHD()
    {
        Minecraft minecraft = ModLoader.getMinecraftInstance();

        if (m_lastScreenshot != null)
        {
            String imagePath = Minecraft.getMinecraftDir().getAbsolutePath();
            imagePath = imagePath.substring(0, imagePath.length() - 1);
            
            imagePath += "screenshots" + File.separator + minecraft.thePlayer.username;

            if (minecraft.isSingleplayer())
            {
                imagePath += File.separator + "single player" + File.separator;
                imagePath += minecraft.getIntegratedServer().getFolderName() + File.separator;
            }
            else
            {
                imagePath += File.separator + "multiplayer" + File.separator;
                imagePath += minecraft.getServerData().serverName + File.separator;
            }

            imagePath += DATE_FORMAT.format(new Date()).toString();
            imagePath += ".png";
            File outputFile = new File(imagePath);

            if (outputFile != null)
            {
                if (!outputFile.exists())
                {
                    outputFile.mkdirs();
                }

                try
                {
                    ImageIO.write(m_lastScreenshot.image, "PNG", outputFile);
                }
                catch (IOException e)
                {
                    minecraft.ingameGUI.getChatGUI().printChatMessage(COLOUR + "6[bUpload] " + COLOUR + "FFailed to save image to disk!");
                    e.printStackTrace();
                    return;
                }
                
                minecraft.ingameGUI.getChatGUI().printChatMessage(COLOUR + "6[bUpload] " + COLOUR + "FImage saved to disk!");
                bUpload.addUploadedImage(new UploadedImage(imagePath.substring(imagePath.lastIndexOf("\\") + 1), imagePath, m_lastScreenshot, true));
            }
        }
    }

    /**
     * Copy the screen buffer
     * @param buffer to copy too
     * @param width of the buffer
     * @param height of the buffer
     */
    private static void copyScreenBuffer(int[] buffer, int width, int height)
    {
        int[] var3 = new int[width];
        int halfHeight = height / 2;

        for (int index = 0; index < halfHeight; ++index)
        {
            System.arraycopy(buffer, index * width, var3, 0, width);
            System.arraycopy(buffer, (height - 1 - index) * width, buffer, index * width, width);
            System.arraycopy(var3, 0, buffer, (height - 1 - index) * width, width);
        }
    }

    /**
     * Shows the upload history from the current session
     */
    public void showUploadHistory()
    {
        Minecraft minecraft = ModLoader.getMinecraftInstance();
        minecraft.ingameGUI.getChatGUI().printChatMessage(COLOUR + "6[bUpload] " + COLOUR + "F-- Upload History -- ");

        for (UploadedImage image : m_uploadHistory)
        {
            minecraft.ingameGUI.getChatGUI().printChatMessage(COLOUR + "6[" + image.getName() + "]" + COLOUR + "b " + image.getUrl());
        }
    }

    /**
     * Add an image to our upload history
     */

    public static synchronized void addUploadedImage(
            UploadedImage newImage
    )
    {
        synchronized (m_uploadHistory)
        {
            m_uploadHistory.add(newImage);
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public UploadedImage getUploadedImage(int index)
    {
        synchronized (m_uploadHistory)
        {
            if (index >= m_uploadHistory.size())
            {
                return null;
            }

            return m_uploadHistory.get(index);
        }
    }

    /**
     *
     * @return
     */
    public int uploadHistorySize()
    {
        synchronized (m_uploadHistory)
        {
            return m_uploadHistory.size();
        }
    }
}