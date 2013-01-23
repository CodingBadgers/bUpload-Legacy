package uk.codingbadgers.bUpload;

import uk.codingbadgers.Gui.GuiCheckBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_bUpload;
import net.minecraft.world.World;

public class AdvancedScreenshotGUI extends GuiScreen {
	
	private static final int SAVE_TO_HD = 1;
	private static final int SAVE_TO_IMGUR = 2;
	private static final int SAVE_TO_BOTH = 3;
	private static final int REMEMBER_CHOICE = 4;
	private static final int HISTORY = 5;
	private static final int EXIT = 6;
	
	private GuiCheckBox m_rememberCheckBox = null;
	
	private static boolean SHOULD_REMEMBER_CHOICE = false;
	private static int CHOICE_TO_REMEMBER = 0;
	
	private mod_bUpload m_mod = null;
	
	public AdvancedScreenshotGUI(mod_bUpload mod) {
		m_mod = mod;
		m_mod.createScreenshot();
	}

	public void initGui() {
				
		if (SHOULD_REMEMBER_CHOICE)
		{
			performScreenshotAction(CHOICE_TO_REMEMBER);
			return;
		}
		
		controlList.clear();
		int ypos = (height / 5) + 20;
		int buttonwidth = 160;
		controlList.add(new GuiButton(SAVE_TO_HD, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Save image to hard drive"));
		ypos += 24;
		controlList.add(new GuiButton(SAVE_TO_IMGUR, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Save image to imgur.com"));
		ypos += 24;
		controlList.add(new GuiButton(SAVE_TO_BOTH, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Save image to both"));
		
		ypos += 24;
		m_rememberCheckBox = new GuiCheckBox(REMEMBER_CHOICE, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, "Remember Choice");
		m_rememberCheckBox.setChecked(SHOULD_REMEMBER_CHOICE);
		controlList.add(m_rememberCheckBox);
		
		ypos += (24 * 2);
		buttonwidth = 75;
		controlList.add(new GuiButton(HISTORY, width / 2 - 80, ypos, buttonwidth, 20, "History"));
		controlList.add(new GuiButton(EXIT, width / 2 + 5, ypos, buttonwidth, 20, "Cancel"));

	}

	public void actionPerformed(GuiButton button){
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
				m_mod.saveScreenshotToHD();
				mc.displayGuiScreen(null);
			}
			break;
			case SAVE_TO_IMGUR:
			{
				CHOICE_TO_REMEMBER = SAVE_TO_IMGUR;
				m_mod.uploadScreenShot();
				mc.displayGuiScreen(null);
			}
			break;
			case SAVE_TO_BOTH:
			{
				CHOICE_TO_REMEMBER = SAVE_TO_BOTH;
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
			break;
		}
	}

	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		
		drawCenteredString(fontRenderer, "bUpload - Advanced Screenshot Manager", width / 2, height / 5 - 20, 0xffffff);
		
		super.drawScreen(i, j, f);
	}
}
