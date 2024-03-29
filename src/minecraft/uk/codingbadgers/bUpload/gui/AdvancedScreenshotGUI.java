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
import uk.codingbadgers.bUpload.ImgurProfile;
import uk.codingbadgers.bUpload.Screenshot;
import uk.codingbadgers.bUpload.bUpload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;

public class AdvancedScreenshotGUI extends bUploadGuiScreen
{
    private static final int SAVE_TO_HD = 1;
    private static final int SAVE_TO_IMGUR = 2;
    private static final int SAVE_TO_BOTH = 3;
    private static final int REMEMBER_CHOICE = 4;
    private static final int HISTORY = 5;
    private static final int EXIT = 6;
    private static final int LOGIN = 7;
    private static final int COPY_TO_CLIPBOARD = 8;

    private GuiCheckBox m_rememberCheckBox = null;
	private GuiCheckBox m_copyToClipboard = null;

    private bUpload m_mod = null;
    private Screenshot m_image = null;

    public AdvancedScreenshotGUI(bUpload mod)
    {
        m_mod = mod;
        m_image = m_mod.createScreenshot();
        
        bUpload.CONFIG.load();
        bUpload.CONFIG.save(); 
    }

    @SuppressWarnings("unchecked")
	public void initGui()
    {
        if (bUpload.SHOULD_REMEMBER_CHOICE)
        {
            performScreenshotAction(bUpload.CHOICE_TO_REMEMBER);
            return;
        }

        buttonList.clear();
        int ypos = (height / 5);
        int buttonwidth = 160;
        
        buttonList.add(new GuiButton(SAVE_TO_HD, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.func_135052_a("image.options.hdd")));
        ypos += 24;
        
        buttonList.add(new GuiButton(SAVE_TO_IMGUR, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.func_135052_a("image.options.imgur")));
        ypos += 24;
        
        buttonList.add(new GuiButton(SAVE_TO_BOTH, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.func_135052_a("image.options.both")));
        ypos += 24;
        
        m_rememberCheckBox = new GuiCheckBox(REMEMBER_CHOICE, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.func_135052_a("image.options.remember"));
        m_rememberCheckBox.setChecked(bUpload.SHOULD_REMEMBER_CHOICE);
        buttonList.add(m_rememberCheckBox);
        ypos += 24;
        
        m_copyToClipboard = new GuiCheckBox(COPY_TO_CLIPBOARD, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.func_135052_a("image.options.copy"));
        m_copyToClipboard.setChecked(bUpload.SHOULD_COPY_TO_CLIPBOARD);
        buttonList.add(m_copyToClipboard);
        ypos += 24;
        buttonwidth = 75;
        
        buttonList.add(new GuiButton(HISTORY, width / 2 - 80, ypos, buttonwidth, 20,  I18n.func_135052_a("image.options.history")));
        buttonList.add(new GuiButton(EXIT, width / 2 + 5, ypos, buttonwidth, 20,  I18n.func_135052_a("image.options.cancel")));
        ypos += 24;
        buttonwidth = 160;
        
        GuiButton gui = new GuiButton(LOGIN, width / 2 - (buttonwidth / 2), ypos, 160, 20,  I18n.func_135052_a("image.options.login"));
        if (ImgurProfile.isLoggedIn()) {
        	gui.displayString =  I18n.func_135052_a("image.options.loggedIn", ImgurProfile.getUsername());
        	gui.enabled = false;
        }
    	buttonList.add(gui);
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
            	bUpload.CHOICE_TO_REMEMBER = SAVE_TO_HD;
                updatedSettings();
                m_mod.saveScreenshotToHD(m_image);
                mc.displayGuiScreen(null);
                break;
            }

            case SAVE_TO_IMGUR:
            {
            	bUpload.CHOICE_TO_REMEMBER = SAVE_TO_IMGUR;
                updatedSettings();
                m_mod.uploadScreenShot(m_image);
                mc.displayGuiScreen(null);
                break;
            }

            case SAVE_TO_BOTH:
            {
            	bUpload.CHOICE_TO_REMEMBER = SAVE_TO_BOTH;
                updatedSettings();
                m_mod.uploadScreenShot(m_image);
                m_mod.saveScreenshotToHD(m_image);
                mc.displayGuiScreen(null);
                break;
            }

            case REMEMBER_CHOICE:
            {
            	bUpload.SHOULD_REMEMBER_CHOICE = m_rememberCheckBox.getChecked();
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

            case LOGIN:
            {
                mc.displayGuiScreen(new ImgurLoginGui(this));
                break;
            }
            
            case COPY_TO_CLIPBOARD:
            {
            	bUpload.SHOULD_COPY_TO_CLIPBOARD = m_copyToClipboard.getChecked();
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
    	bUpload.CONFIG.load();
        
        bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "RememberSaveChoice", bUpload.SHOULD_REMEMBER_CHOICE).set(bUpload.SHOULD_REMEMBER_CHOICE);
        bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "ChoiceToRemember", bUpload.CHOICE_TO_REMEMBER).set(bUpload.CHOICE_TO_REMEMBER);
        bUpload.CONFIG.get(Configuration.CATEGORY_GENERAL, "CopyToClipboard", bUpload.SHOULD_COPY_TO_CLIPBOARD).set(bUpload.SHOULD_COPY_TO_CLIPBOARD);
        
        bUpload.CONFIG.save();     	
    }

    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, I18n.func_135052_a("image.options.title"), width / 2, height / 5 - 20, 0xffffff);
        super.drawScreen(i, j, f);
    }
}
