package uk.codingbadgers.bUpload;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_bUpload;

public class SettingsGUI extends GuiScreen {
	
	/** Access to the core mod */
	private mod_bUpload m_mod = null;
	
	/** Imgur account name */
	private GuiTextField m_accountName = null;
	
	private GuiScreen m_parent = null;
	
	private static final int BACK = 1;
	private static final int SAVE = 2;
	
	/**
	 * Default constructor
	 * @param mod 		Access to our main mod instance
	 */
	public SettingsGUI(mod_bUpload mod, GuiScreen parent) {
		m_mod = mod;
		m_parent = parent;
	}
	
	/**
	 * Initialise the gui, adding buttons to the screen
	 */
	public void initGui() {
		
		controlList.clear();
		
		m_accountName = new GuiTextField(mc.fontRenderer, this.width / 2 - 100, 66, 200, 20);
		m_accountName.setFocused(true);
		m_accountName.setText("Account Name");
		
		int buttonwidth = 75;
		int ypos = (height / 5) + 140;
		controlList.add(new GuiButton(BACK, width / 2 - 80, ypos, buttonwidth, 20, "Back"));
		controlList.add(new GuiButton(SAVE, width / 2 + 5, ypos, buttonwidth, 20, "Save"));
		
	}
	
	/**
	 * Called when a button is pressed by a user
	 */
	public void actionPerformed(GuiButton button){
		
		switch (button.id)
		{
			case BACK:
			{
				mc.displayGuiScreen(m_parent);
			}
			break;
			case SAVE:
			{
				mc.displayGuiScreen(m_parent);
			}
			break;
		}
	}
	
    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2) {
    	m_accountName.textboxKeyTyped(par1, par2);
    }
	
	/**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int par1, int par2, int par3) {
    	super.mouseClicked(par1, par2, par3);
        m_accountName.mouseClicked(par1, par2, par3);
    }
	
	/**
	 * Draw the container image to the screen
	 * @param
	 * @param
	 * @param
	 */
	public void drawScreen(int i, int j, float f) {
		
		drawDefaultBackground();		
		
		this.drawCenteredString(mc.fontRenderer, "bUpload Settings", width / 2, height / 5 - 20, 0xffffff);
        
		m_accountName.drawTextBox();
		
		super.drawScreen(i, j, f);
		
	}

}
