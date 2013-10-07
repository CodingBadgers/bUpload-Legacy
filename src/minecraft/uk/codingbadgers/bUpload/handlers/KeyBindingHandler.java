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
package uk.codingbadgers.bUpload.handlers;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import uk.codingbadgers.bUpload.gui.UploadHistoryGUI;
import uk.codingbadgers.bUpload.gui.bUploadGuiScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyBindingHandler extends KeyHandler
{
    private boolean m_pressed = false;

    static KeyBinding onScreenShot = new KeyBinding(I18n.getString("image.binding.screenshot"), Keyboard.getKeyIndex(ConfigHandler.KEYBIND_ADV_SS));
    static KeyBinding onUploadHistory = new KeyBinding(I18n.getString("image.binding.history"), Keyboard.getKeyIndex(ConfigHandler.KEYBIND_HISTORY));

    public KeyBindingHandler()
    {
        super(
                new KeyBinding[]
                {
                    onScreenShot,
                    onUploadHistory
                },
                new boolean[]
                {
                    false,
                    false
                }
        );
    }

    @Override
    public String getLabel()
    {
        return "bUploadKeyHandler";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {
        m_pressed = !m_pressed;
        Minecraft minecraft = ModLoader.getMinecraftInstance();
        
        if (m_pressed && kb.keyCode == onScreenShot.keyCode)
        {
            ScreenshotHandler.handleScreenshot();
        }
        else if (m_pressed && kb.keyCode == onUploadHistory.keyCode && minecraft.currentScreen == null)
        {
            minecraft.displayGuiScreen(new UploadHistoryGUI(minecraft.currentScreen instanceof bUploadGuiScreen ? (bUploadGuiScreen) minecraft.currentScreen : null));
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }
}
