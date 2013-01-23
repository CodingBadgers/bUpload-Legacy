package uk.codingbadgers.bUpload;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_bUpload;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class bUploadKeyHandler extends KeyHandler {
	
	private mod_bUpload m_mod = null;
	
	private boolean m_pressed = false;
	
	static KeyBinding onScreenShot = new KeyBinding("Take Advanced Screenshot", Keyboard.KEY_F12);
	static KeyBinding onUploadHistory = new KeyBinding("Screenshot Upload History", Keyboard.KEY_EQUALS);

	public bUploadKeyHandler(mod_bUpload mod) {
		
		super(
			new KeyBinding[]{
				onScreenShot,
				onUploadHistory
			}, 
			new boolean[] {
				false,
				false
			}
		);
		
		m_mod = mod;				
	}

	@Override
	public String getLabel() {
		return "bUploadKeyHandler";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		m_pressed = !m_pressed;
		if (m_pressed && kb.keyCode == onScreenShot.keyCode)
		{
			Minecraft minecraft = ModLoader.getMinecraftInstance();
			minecraft.displayGuiScreen(new AdvancedScreenshotGUI(m_mod));
		}
		else if (m_pressed && kb.keyCode == onUploadHistory.keyCode)
		{
			m_mod.showUploadHistory();
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
