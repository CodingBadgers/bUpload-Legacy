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

    private GuiCheckBox m_rememberCheckBox = null;

    private static boolean SHOULD_REMEMBER_CHOICE = false;
    private static int CHOICE_TO_REMEMBER = 0;

    private bUpload m_mod = null;

    public AdvancedScreenshotGUI(bUpload mod)
    {
        m_mod = mod;
        m_mod.createScreenshot();
        
        bUpload.CONFIG.load();
       
        SHOULD_REMEMBER_CHOICE = bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "RememberSaveChoice", false).getBoolean(false);
        if (SHOULD_REMEMBER_CHOICE) {
        	CHOICE_TO_REMEMBER = bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "ChoiceToRemember", 1).getInt(1);
        }
        
        bUpload.CONFIG.save(); 
    }

    @SuppressWarnings("unchecked")
	public void initGui()
    {
        if (SHOULD_REMEMBER_CHOICE)
        {
            performScreenshotAction(CHOICE_TO_REMEMBER);
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
        m_rememberCheckBox.setChecked(SHOULD_REMEMBER_CHOICE);
        buttonList.add(m_rememberCheckBox);
        ypos += (24 * 2);
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
                CHOICE_TO_REMEMBER = SAVE_TO_HD;
                updatedSettings();
                m_mod.saveScreenshotToHD();
                mc.displayGuiScreen(null);
            }
            break;

            case SAVE_TO_IMGUR:
            {
                CHOICE_TO_REMEMBER = SAVE_TO_IMGUR;
                updatedSettings();
                m_mod.uploadScreenShot();
                mc.displayGuiScreen(null);
            }
            break;

            case SAVE_TO_BOTH:
            {
                CHOICE_TO_REMEMBER = SAVE_TO_BOTH;
                updatedSettings();
                m_mod.uploadScreenShot();
                m_mod.saveScreenshotToHD();
                mc.displayGuiScreen(null);
            }
            break;

            case REMEMBER_CHOICE:
            {
                SHOULD_REMEMBER_CHOICE = m_rememberCheckBox.getChecked();
            }
            break;

            case HISTORY:
            {
                mc.displayGuiScreen(new UploadHistoryGUI(m_mod));
            }
            break;

            case EXIT:
            {
                mc.displayGuiScreen(null);
            }

            case SETTINGS:
            {
                mc.displayGuiScreen(new SettingsGUI(m_mod, this));
            }
            break;
        }
    }
    
    private void updatedSettings()
    {
    	bUpload.CONFIG.load();
        
        bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "RememberSaveChoice", SHOULD_REMEMBER_CHOICE).set(SHOULD_REMEMBER_CHOICE);
        bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "ChoiceToRemember", CHOICE_TO_REMEMBER).set(CHOICE_TO_REMEMBER);
        
        bUpload.CONFIG.save();     	
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "bUpload - Advanced Screenshot Manager", width / 2, height / 5 - 20, 0xffffff);
        super.drawScreen(i, j, f);
    }
}
