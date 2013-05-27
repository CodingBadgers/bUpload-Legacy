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

import uk.codingbadgers.Gui.GuiCheckBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;

public class AdvancedScreenshotGUI extends GuiScreen
{
    private static final int SAVE_TO_HD = 1;
    private static final int SAVE_TO_IMGUR = 2;
    private static final int SAVE_TO_BOTH = 3;
    private static final int REMEMBER_CHOICE = 4;
    private static final int HISTORY = 5;
    private static final int EXIT = 6;
    private static final int SETTINGS = 7;
    private static final int COPY_TO_CLIPBOARD = 8;

    private GuiCheckBox m_rememberCheckBox = null;
	private GuiCheckBox m_copyToClipboard = null;

    private mod_bUpload m_mod = null;

    public AdvancedScreenshotGUI(mod_bUpload mod)
    {
        m_mod = mod;
        m_mod.createScreenshot();
        
        mod_bUpload.CONFIG.load();
       
        mod_bUpload.CONFIG.save(); 
    }

    @SuppressWarnings("unchecked")
	public void initGui()
    {
        if (mod_bUpload.SHOULD_REMEMBER_CHOICE)
        {
            performScreenshotAction(mod_bUpload.CHOICE_TO_REMEMBER);
            return;
        }

        buttonList.clear();
        int ypos = (height / 5) + 20;
        int buttonwidth = 160;
        buttonList.add(new GuiButton(SAVE_TO_HD, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Save image to hard drive"));
        ypos += 24;
        buttonList.add(new GuiButton(SAVE_TO_IMGUR, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Save image to imgur.com"));
        ypos += 24;
        buttonList.add(new GuiButton(SAVE_TO_BOTH, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Save image to both"));
        ypos += 24;
        m_rememberCheckBox = new GuiCheckBox(REMEMBER_CHOICE, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Remember Choice");
        m_rememberCheckBox.setChecked(mod_bUpload.SHOULD_REMEMBER_CHOICE);
        buttonList.add(m_rememberCheckBox);
        ypos += 24;
        // FIXME currently doesn't render text correctly
        //m_copyToClipboard = new GuiCheckBox(COPY_TO_CLIPBOARD, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Copy to Clipboard");
        //m_copyToClipboard.setChecked(SHOULD_COPY_TO_CLIPBOARD);
        //buttonList.add(m_copyToClipboard);
        ypos += 24 * 2;
        buttonwidth = 75;
        buttonList.add(new GuiButton(HISTORY, width / 2 - 80, ypos, buttonwidth, 20, "History"));
        buttonList.add(new GuiButton(EXIT, width / 2 + 5, ypos, buttonwidth, 20, "Cancel"));
        ypos += 24;
        buttonwidth = 160;
        //buttonList.add(new GuiButton(SETTINGS, width / 2 - (buttonwidth / 2), ypos, 160, 20, "Settings"));
    }

    public void actionPerformed(GuiButton button)
    {
        performScreenshotAction(button.id);
    }

    private void performScreenshotAction(int id)
    {
        Minecraft mc = ModLoader.getMinecraftInstance();

        switch (id)
        {
            case SAVE_TO_HD:
            {
            	mod_bUpload.CHOICE_TO_REMEMBER = SAVE_TO_HD;
                updatedSettings();
                m_mod.saveScreenshotToHD();
                mc.displayGuiScreen(null);
                break;
            }

            case SAVE_TO_IMGUR:
            {
            	mod_bUpload.CHOICE_TO_REMEMBER = SAVE_TO_IMGUR;
                updatedSettings();
                m_mod.uploadScreenShot();
                mc.displayGuiScreen(null);
                break;
            }

            case SAVE_TO_BOTH:
            {
            	mod_bUpload.CHOICE_TO_REMEMBER = SAVE_TO_BOTH;
                updatedSettings();
                m_mod.uploadScreenShot();
                m_mod.saveScreenshotToHD();
                mc.displayGuiScreen(null);
                break;
            }

            case REMEMBER_CHOICE:
            {
            	mod_bUpload.SHOULD_REMEMBER_CHOICE = m_rememberCheckBox.getChecked();
                break;
            }

            case HISTORY:
            {
                mc.displayGuiScreen(new UploadHistoryGUI(m_mod));
                break;
            }

            case EXIT:
            {
                mc.displayGuiScreen(null);
                break;
            }

            case SETTINGS:
            {
                mc.displayGuiScreen(new SettingsGUI(m_mod, this));
                break;
            }
            
            case COPY_TO_CLIPBOARD:
            {
            	mod_bUpload.SHOULD_COPY_TO_CLIPBOARD = m_copyToClipboard.getChecked();
                break;
            }
            
            default:
            {
                break;
            }
        }
    }
    
    private void updatedSettings()
    {
    	mod_bUpload.CONFIG.load();
        
        mod_bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "RememberSaveChoice", mod_bUpload.SHOULD_REMEMBER_CHOICE).set(mod_bUpload.SHOULD_REMEMBER_CHOICE);
        mod_bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "ChoiceToRemember", mod_bUpload.CHOICE_TO_REMEMBER).set(mod_bUpload.CHOICE_TO_REMEMBER);
        mod_bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "CopyToClipboard", mod_bUpload.SHOULD_COPY_TO_CLIPBOARD).set(mod_bUpload.SHOULD_COPY_TO_CLIPBOARD);
        
        mod_bUpload.CONFIG.save();     	
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "bUpload - Advanced Screenshot Manager", width / 2, height / 5 - 20, 0xffffff);
        super.drawScreen(i, j, f);
    }
}
