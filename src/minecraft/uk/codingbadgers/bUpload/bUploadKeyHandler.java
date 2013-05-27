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

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class bUploadKeyHandler extends KeyHandler
{
    private mod_bUpload m_mod = null;

    private boolean m_pressed = false;

    static KeyBinding onScreenShot = new KeyBinding("Take Advanced Screenshot", Keyboard.KEY_F12);
    static KeyBinding onUploadHistory = new KeyBinding("Screenshot History", Keyboard.KEY_EQUALS);

    public bUploadKeyHandler(mod_bUpload mod)
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
        m_mod = mod;
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
            minecraft.displayGuiScreen(new AdvancedScreenshotGUI(m_mod));
        }
        else if (m_pressed && kb.keyCode == onUploadHistory.keyCode && minecraft.currentScreen == null)
        {
            minecraft.displayGuiScreen(new UploadHistoryGUI(m_mod));
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }
}
