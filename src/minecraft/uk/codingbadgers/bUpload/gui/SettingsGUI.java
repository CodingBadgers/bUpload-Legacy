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

import uk.codingbadgers.Gui.GuiCheckBox;
import uk.codingbadgers.bUpload.handlers.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.ModLoader;

public class SettingsGUI extends bUploadGuiScreen {
    
	private static final int SAVE_TO_HD = 1;
	private static final int SAVE_TO_IMGUR = 2;
	private static final int SAVE_TO_FTP = 3;
	private static final int COPY_TO_CLIPBOARD = 4;
	private static final int HISTORY = 5;
	private static final int EXIT = 6;
	private static final int AUTH = 7;

	private GuiCheckBox m_copyToClipboard = null;
	private GuiCheckBox m_saveToHDD;
	private GuiCheckBox m_saveToImgur;
	private GuiCheckBox m_saveToFtp;
	private GuiButton m_auth;

	public SettingsGUI(bUploadGuiScreen screen) {
	    super(screen);
	}

	@Override
	public void updateLogin() {}

	@SuppressWarnings("unchecked")
	public void initGui() {
		buttonList.clear();
		int ypos = (height / 5);
		int buttonwidth = 100;

		m_saveToImgur = new GuiCheckBox(SAVE_TO_IMGUR, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20,  I18n.getString("image.save.imgur"));
		m_saveToImgur.setChecked(ConfigHandler.SAVE_IMGUR);
        buttonList.add(m_saveToImgur);
        ypos += 24;
        
        m_saveToFtp = new GuiCheckBox(SAVE_TO_FTP, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20,  I18n.getString("image.save.ftp"));
        m_saveToFtp.setChecked(ConfigHandler.SAVE_FTP);
        buttonList.add(m_saveToFtp);
        ypos += 24;
        
        m_saveToHDD = new GuiCheckBox(SAVE_TO_HD, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20,  I18n.getString("image.save.hd"));
        m_saveToHDD.setChecked(ConfigHandler.SAVE_FILE);
        buttonList.add(m_saveToHDD);
        ypos += 24;
        ypos += 24;
        buttonwidth = 160;
        
		m_copyToClipboard = new GuiCheckBox(COPY_TO_CLIPBOARD, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.getString("image.options.copy"));
		m_copyToClipboard.setChecked(ConfigHandler.COPY_URL_TO_CLIPBOARD);
		buttonList.add(m_copyToClipboard);
		ypos += 24;

		m_auth = new GuiButton(AUTH, width / 2 - (buttonwidth / 2), ypos, 160, 20, I18n.getString("image.options.auth"));
		buttonList.add(m_auth);
		ypos += 24;
		buttonwidth = 75;
		
		buttonList.add(new GuiButton(HISTORY, width / 2 - 80, ypos, buttonwidth, 20, I18n.getString("image.options.history")));
		buttonList.add(new GuiButton(EXIT, width / 2 + 5, ypos, buttonwidth, 20, I18n.getString("image.options.cancel")));
		ypos += 24;
	}

	public void actionPerformed(GuiButton button) {
		performScreenshotAction(button.id);
	}
	
	private void performScreenshotAction(int id) {
		Minecraft mc = ModLoader.getMinecraftInstance();

		switch (id) {
		case SAVE_TO_HD: {
			ConfigHandler.SAVE_FILE = m_saveToHDD.getChecked();
			updatedSettings();
			break;
		}

		case SAVE_TO_IMGUR: {
            ConfigHandler.SAVE_IMGUR = m_saveToImgur.getChecked();
			updatedSettings();
			break;
		}

		case SAVE_TO_FTP: {
            ConfigHandler.SAVE_FTP = m_saveToFtp.getChecked();
			updatedSettings();
			break;
		}

		case COPY_TO_CLIPBOARD: {
			ConfigHandler.COPY_URL_TO_CLIPBOARD = m_copyToClipboard.getChecked();
			updatedSettings();
			break;
		}

		case HISTORY: {
			mc.displayGuiScreen(new UploadHistoryGUI(this));
			break;
		}

		case EXIT: {
			mc.displayGuiScreen(null);
			break;
		}

		case AUTH: {
			mc.displayGuiScreen(new AuthGui(this));
			break;
		}

		default: {
			break;
		}
		}
	}

	private void updatedSettings() {
	    ConfigHandler.save();
	}

	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, I18n.getString("image.settings.title"), width / 2, height / 5 - 20, 0xffffff);
		super.drawScreen(i, j, f);
	}
}
