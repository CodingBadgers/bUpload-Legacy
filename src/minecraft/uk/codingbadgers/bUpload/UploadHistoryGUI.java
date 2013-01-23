package uk.codingbadgers.bUpload;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.src.ModLoader;
import net.minecraft.src.mod_bUpload;

public class UploadHistoryGUI extends GuiScreen {
	
	/** Access to the core mod */
	private mod_bUpload				m_mod = null;
		
	/** The constant width of the container image */
	private static final int		m_containerWidth = 176;
	
	/** The constant height of the container image */
	private static final int		m_containerHeight = 222;
	
	/** The id of the previous button */
	private static final int		BUTTON_PREVIOUS = 1;
	
	/** The id of the next button */
	private static final int		BUTTON_NEXT = 2;
		
	/**
	 * Default constructor
	 * @param mod 		Access to our main mod instance
	 */
	public UploadHistoryGUI(mod_bUpload mod) {
		m_mod = mod;
	}
	
	/**
	 * Initialise the gui, adding buttons to the screen
	 */
	public void initGui() {
		
		controlList.clear();
		
		// add a previous button
		controlList.add(new GuiButton(BUTTON_PREVIOUS, (width / 2) - (m_containerWidth / 2) - (70), ((height / 2) - (m_containerHeight / 2) + m_containerHeight) - 25, 60, 20, "Previous"));
	
		// add a next button
		controlList.add(new GuiButton(BUTTON_NEXT, (width / 2) + (m_containerWidth / 2) + (10), ((height / 2) - (m_containerHeight / 2) + m_containerHeight) - 25, 60, 20, "Next"));
	}
	
	/**
	 * Draw the container image to the screen
	 * @param
	 * @param
	 * @param
	 */
	public void drawScreen(int i, int j, float f) {
		
		Minecraft minecraft = ModLoader.getMinecraftInstance();
		
		drawDefaultBackground();		
		
		// load our container image
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/gui/bupload-history.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        drawTexturedModalRect((width / 2) - (m_containerWidth / 2), (height / 2) - (m_containerHeight / 2), 0, 0, m_containerWidth, m_containerHeight);
		
		super.drawScreen(i, j, f);
		
	}
	
	/**
	 * Called when a button is pressed by a user
	 */
	public void actionPerformed(GuiButton button){
		
	}

}
