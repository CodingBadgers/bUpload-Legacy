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
package uk.codingbadgers.bUpload.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.lwjgl.opengl.GL11;

import uk.codingbadgers.bUpload.UploadedImage;
import uk.codingbadgers.bUpload.bUpload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class UploadHistoryGUI extends bUploadGuiScreen {
	
	private bUpload m_mod = null;

	private static final int COTAINER_WIDTH = 176;
	private static final int CONTAINER_HIGHT = 222;
	
	private static final int BUTTON_PREVIOUS = 0;
	private static final int BUTTON_NEXT = 1;
	private static final int BUTTON_SETTINGS = 2;
	
	private int m_currentImage = 0;
	private GuiButton m_settingsButton;

	/**
	 * Default constructor
	 * 
	 * @param mod
	 *            Access to our main mod instance
	 */
	public UploadHistoryGUI(bUpload mod) {
		m_mod = mod;
	}

	/**
	 * Initialise the gui, adding buttons to the screen
	 */
	@SuppressWarnings("unchecked")
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButton(BUTTON_PREVIOUS, (width / 2) - (COTAINER_WIDTH / 2) - (70), ((height / 2) - (CONTAINER_HIGHT / 2) + CONTAINER_HIGHT) - 25, 60, 20, "Previous"));
		buttonList.add(new GuiButton(BUTTON_NEXT, (width / 2) + (COTAINER_WIDTH / 2) + (10), ((height / 2) - (CONTAINER_HIGHT / 2) + CONTAINER_HIGHT) - 25, 60, 20, "Next"));

		m_settingsButton = new GuiButton(BUTTON_SETTINGS, (width / 2) + (COTAINER_WIDTH / 2) + (10), (this.height / 2) - (CONTAINER_HIGHT / 2) + 5, 60, 20, I18n.func_135053_a("image.history.settings"));
		buttonList.add(m_settingsButton);
	}

	/**
	 * Draw the container image to the screen
	 * 
	 * @param
	 * @param
	 * @param
	 */
	public void drawScreen(int i, int j, float f) {
		Minecraft minecraft = Minecraft.getMinecraft();
		drawDefaultBackground();
		// load our container image
		minecraft.renderEngine.func_110577_a(new ResourceLocation("bUpload:textures/gui/bupload-history.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect((width / 2) - (COTAINER_WIDTH / 2), (height / 2) - (CONTAINER_HIGHT / 2), 0, 0, COTAINER_WIDTH, CONTAINER_HIGHT);
		UploadedImage imageInfo = m_mod.getUploadedImage(m_currentImage);

		if (imageInfo != null) {
			// draw the image information
			int yOffset = 132;
			drawCenteredString(minecraft.fontRenderer, imageInfo.getName(), (width / 2), ((height / 2) - (CONTAINER_HIGHT / 2)) + yOffset, 0xFFFFFFFF);
			yOffset += 16;

			if (!imageInfo.isLocal()) {
				drawCenteredString(minecraft.fontRenderer, imageInfo.getUrl(), (width / 2), ((height / 2) - (CONTAINER_HIGHT / 2)) + yOffset, 0xFFFFAA00);
			} else {
				drawCenteredString(minecraft.fontRenderer, I18n.func_135053_a("image.history.open"), (width / 2), ((height / 2) - (CONTAINER_HIGHT / 2)) + yOffset, 0xFFFFAA00);
			}

			// draw the image preview
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageInfo.getImageID());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRectSized((width / 2) - (COTAINER_WIDTH / 2) + 8, (height / 2) - (CONTAINER_HIGHT / 2) + 18, 0, 0, 160, 101, 256, 256);
		} else {
			drawCenteredString(minecraft.fontRenderer, I18n.func_135053_a("image.history.empty"), (width / 2), ((height / 2) - (CONTAINER_HIGHT / 2)) + 132, 0xFFFFFFFF);
		}

		super.drawScreen(i, j, f);
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v,
	 * width, height, uvwidth, uvheight
	 */
	public void drawTexturedModalRectSized(int x, int y, int u, int v, int width, int height, int uvwidth, int uvheight) {
		final float var7 = 0.00390625F;
		final float var8 = 0.00390625F;
		Tessellator quad = Tessellator.instance;
		quad.startDrawingQuads();
		quad.addVertexWithUV((double) (x), (double) (y + height), (double) zLevel, (double) (u * var7), (double) ((v + uvheight) * var8));
		quad.addVertexWithUV((double) (x + width), (double) (y + height), (double) zLevel, (double) ((u + uvwidth) * var7), (double) ((v + uvheight) * var8));
		quad.addVertexWithUV((double) (x + width), (double) (y), (double) zLevel, (double) ((u + uvwidth) * var7), (double) (v * var8));
		quad.addVertexWithUV((double) (x), (double) (y), (double) zLevel, (double) (u * var7), (double) (v * var8));
		quad.draw();
	}

	/**
	 * Called when a button is pressed by a user
	 */
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
		case BUTTON_PREVIOUS: {
			m_currentImage--;

			if (m_currentImage < 0) {
				m_currentImage = m_mod.uploadHistorySize() - 1;
			}

			if (m_currentImage < 0) {
				m_currentImage = 0;
			}
		}
			break;

		case BUTTON_NEXT: {
			m_currentImage++;

			if (m_currentImage >= m_mod.uploadHistorySize()) {
				m_currentImage = 0;
			}
		}
			break;

		case BUTTON_SETTINGS: {
			mc.displayGuiScreen(new SettingsGUI(m_mod));
		}
			break;
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		if (x < (width / 2) - (COTAINER_WIDTH / 2) + 12) {
			return;
		}

		if (x > (width / 2) - (COTAINER_WIDTH / 2) + COTAINER_WIDTH - 12) {
			return;
		}

		if (y < ((height / 2) - (CONTAINER_HIGHT / 2)) + 148) {
			return;
		}

		if (y > ((height / 2) - (CONTAINER_HIGHT / 2)) + 158) {
			return;
		}

		UploadedImage imageInfo = m_mod.getUploadedImage(m_currentImage);

		if (imageInfo != null) {
			if (!imageInfo.isLocal()) {
				if (mc.gameSettings.chatLinksPrompt) {
					mc.displayGuiScreen(new GuiConfirmOpenLink(this, imageInfo.getUrl(), 0, false));
				} else {
					openUrl();
				}
			} else {
				Desktop dt = Desktop.getDesktop();
				try {
					dt.open(new File(imageInfo.getUrl()));
				} catch (IOException e) {
					mc.currentScreen = null;
					bUpload.sendChatMessage("image.history.open.fail.1", true);
					bUpload.sendChatMessage("image.history.open.fail.2", true);
					try {
						dt.open(new File(imageInfo.getUrl().replace(imageInfo.getName(), "")));
					} catch (IOException e1) {
						bUpload.sendChatMessage("image.history.open.fail.3", true);
					}
				}
			}
		}
	}

	/**
	 * Called when the user clicks a button on the 'should i open that link' gui
	 */
	public void confirmClicked(boolean openUrl, int par2) {
		if (openUrl) {
			openUrl();
		}

		mc.displayGuiScreen(this);
	}
	
	public void openUrl() {
		UploadedImage imageInfo = m_mod.getUploadedImage(m_currentImage);

		if (imageInfo != null) {
			try {
				Desktop dt = Desktop.getDesktop();
				dt.browse(URI.create(imageInfo.getUrl()));
			} catch (Throwable var4) {
				var4.printStackTrace();
			}
		}
	}
}
