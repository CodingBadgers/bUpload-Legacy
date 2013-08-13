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
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

/**
 * The Class bUpload.
 */
@Mod(modid = "@MOD_ID@", name = "@MOD_NAME@", version = "@VERSION@")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class bUpload {
	
	@Instance("@MOD_ID@")
	public static bUpload INSTANCE = null;

	public static final char COLOUR = 167;
	private static IntBuffer PIXEL_BUFFER = null;
	private static int[] PIXEL_ARRAY = null;
	private static ArrayList<UploadedImage> m_uploadHistory = new ArrayList<UploadedImage>();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private Screenshot m_lastScreenshot = new Screenshot();
	
	public static Configuration CONFIG = null;
	public static boolean SHOULD_REMEMBER_CHOICE = false;
	public static boolean SHOULD_COPY_TO_CLIPBOARD = true;
    public static int CHOICE_TO_REMEMBER = 0;
	public static String ADV_SS_KEYBINDING = null;
	public static String SS_HISTORY_KEYBINDING = null;
	
	public static String server;
	public static int port;

	/**
	 * Pre init.
	 *
	 * @param event the event
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CONFIG = new Configuration(event.getSuggestedConfigurationFile());	
		setupConfig();
	}

	private void setupConfig() {
		CONFIG.load();
		
		Property remSave = CONFIG.get(Configuration.CATEGORY_GENERAL, "RememberSaveChoice", false);
		remSave.comment = "Rember the choice of what do with the screenshots";
		SHOULD_REMEMBER_CHOICE = remSave.getBoolean(false);
		
		Property choice = CONFIG.get(Configuration.CATEGORY_GENERAL, "ChoiceToRemember", 0);
		choice.comment = "What to do with the screenshot after it has been taken (1 -> save to HD, 2 -> save to imgur, 3 -> save to both)";
        CHOICE_TO_REMEMBER = choice.getInt(0);

		Property remClipboard = CONFIG.get(Configuration.CATEGORY_GENERAL, "CopyToClipboard", true);
		remClipboard.comment = "If the imgur link should be copied to your clipboard after it has been uploaded";
        SHOULD_COPY_TO_CLIPBOARD = remClipboard.getBoolean(true);
        
        Property keyBindingAdvSS = CONFIG.get("Keybindings", "advancedScreenshot", "F12");
        keyBindingAdvSS.comment = "The key that should be used to take a screenshot with bUpload";
        ADV_SS_KEYBINDING = keyBindingAdvSS.getString();
        
        Property keyBindingSSHist = CONFIG.get("Keybindings", "screenshotHistory", "EQUALS");
        keyBindingSSHist.comment = "The key that should be used to view the screenshot history";
        SS_HISTORY_KEYBINDING = keyBindingSSHist.getString();
        
        ImgurProfile.loadRefreshToken();
        CONFIG.save();
	}

	public static void saveKeyBindings() {
		CONFIG.load();
        Property keyBindingAdvSS = CONFIG.get("Keybindings", "advancedScreenshot", "F12");
        Property keyBindingSSHist = CONFIG.get("Keybindings", "screenshotHistory", "EQUALS");
        
        keyBindingAdvSS.set(Keyboard.getKeyName(KeyBindingHandler.onScreenShot.keyCode));
        keyBindingSSHist.set(Keyboard.getKeyName(KeyBindingHandler.onUploadHistory.keyCode));
        CONFIG.save();
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		System.out.println("Server starting");
		saveKeyBindings();
	}

	@EventHandler
	public void serverStop(FMLServerStoppingEvent event) {
		System.out.println("Server stopping");
		saveKeyBindings();
	}

	/**
	 * Load.
	 *
	 * @param event the event
	 */
	@EventHandler
	public void load(FMLInitializationEvent event) {
		KeyBindingRegistry.registerKeyBinding(new KeyBindingHandler(this));
		NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
	}

	/**
	 * Creates the screenshot.
	 */
	public void createScreenshot() {
		Minecraft minecraft = ModLoader.getMinecraftInstance();

		try {
			int screenSize = minecraft.displayWidth * minecraft.displayHeight;

			if (PIXEL_BUFFER == null || PIXEL_BUFFER.capacity() < screenSize) {
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
			m_lastScreenshot.imageID = TextureUtil.func_110987_a(TextureUtil.func_110996_a(), m_lastScreenshot.image);
		} catch (Exception ex) {
			ex.printStackTrace();
			m_lastScreenshot.image = null;
			m_lastScreenshot.imageID = 0;
		}
	}

	/**
	 * Upload screen shot.
	 */
	public void uploadScreenShot() {
		if (m_lastScreenshot.image != null && m_lastScreenshot.imageID != 0) {
			bUpload.sendChatMessage("Uploading image to Imgur...");
			ImageUploadThread uploadThread = new ImageUploadThread(m_lastScreenshot);
			new Thread(uploadThread).start();
		}
	}

	/**
	 * Save screenshot to hd.
	 */
	public void saveScreenshotToHD() {
		Minecraft minecraft = Minecraft.getMinecraft();

		if (m_lastScreenshot != null) {
			String imagePath = minecraft.mcDataDir.getAbsolutePath();

			// for some reason player is null in the menu
			if (minecraft.thePlayer == null) {
				imagePath += File.separatorChar + "screenshots" + File.separatorChar + "menu" + File.separatorChar;
			} else {
				imagePath += File.separatorChar + "screenshots" + File.separatorChar + minecraft.thePlayer.username;
			
				if (minecraft.isSingleplayer()) {
					imagePath += File.separatorChar + "single player" + File.separatorChar;
					imagePath += minecraft.getIntegratedServer().getFolderName() + File.separatorChar;
				} else {
					imagePath += File.separatorChar + "multiplayer" + File.separatorChar;
					imagePath += server + File.separatorChar;
				}
			}

			imagePath += DATE_FORMAT.format(new Date()).toString();
			imagePath += ".png";
			File outputFile = new File(imagePath);

			if (outputFile != null) {
				if (!outputFile.exists()) {
					outputFile.mkdirs();
				}

				try {
					ImageIO.write(m_lastScreenshot.image, "PNG", outputFile);
				} catch (IOException e) {
					bUpload.sendChatMessage("Failed to save image to disk!");
					e.printStackTrace();
					return;
				}

				bUpload.sendChatMessage("Image saved to disk!");
				bUpload.addUploadedImage(new UploadedImage(imagePath.substring(imagePath.lastIndexOf("\\") + 1), imagePath, m_lastScreenshot, true));
			}
		}
	}
	
	/**
	 * Copy the screen buffer.
	 *
	 * @param buffer to copy too
	 * @param width of the buffer
	 * @param height of the buffer
	 */
	private static void copyScreenBuffer(int[] buffer, int width, int height) {
		int[] var3 = new int[width];
		int halfHeight = height / 2;

		for (int index = 0; index < halfHeight; ++index) {
			System.arraycopy(buffer, index * width, var3, 0, width);
			System.arraycopy(buffer, (height - 1 - index) * width, buffer, index * width, width);
			System.arraycopy(var3, 0, buffer, (height - 1 - index) * width, width);
		}
	}

	/**
	 * Add an image to our upload history.
	 *
	 * @param newImage the new image
	 */

	public static synchronized void addUploadedImage(UploadedImage newImage) {
		m_uploadHistory.add(newImage);
	}

	/**
	 * Gets the uploaded image.
	 *
	 * @param index the index
	 * @return the uploaded image
	 */
	public UploadedImage getUploadedImage(int index) {
		synchronized (m_uploadHistory) {
			if (index >= m_uploadHistory.size()) {
				return null;
			}

			return m_uploadHistory.get(index);
		}
	}

	/**
	 * Upload history size.
	 *
	 * @return the int
	 */
	public int uploadHistorySize() {
		synchronized (m_uploadHistory) {
			return m_uploadHistory.size();
		}
	}
	
	public static void sendChatMessage(String message) {
		if (Minecraft.getMinecraft().ingameGUI == null || Minecraft.getMinecraft().ingameGUI.getChatGUI() == null) {
			System.err.println(message);
			return;
		}
		
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(COLOUR + "6[bUpload]" + COLOUR + "f " + message);
	}

}
