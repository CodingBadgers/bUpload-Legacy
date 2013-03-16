package uk.codingbadgers.bUpload;

import java.net.URI;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
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
	
	/** The current image index into the upload history */
	private int 					m_currentImage = 0;
		
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
		
        UploadedImage imageInfo = m_mod.getUploadedImage(m_currentImage);
        if (imageInfo != null)
        {
        	// draw the image information
        	int yOffset = 132;
        	drawCenteredString(minecraft.fontRenderer, imageInfo.getName(), (width / 2), ((height / 2) - (m_containerHeight / 2)) + yOffset, 0xFFFFFFFF);
        	yOffset += 16;
        	drawCenteredString(minecraft.fontRenderer, imageInfo.getUrl(), (width / 2), ((height / 2) - (m_containerHeight / 2)) + yOffset, 0xFFFFAA00);
        
        	// draw the image preview
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, imageInfo.getImageID());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            drawTexturedModalRectSized((width / 2) - (m_containerWidth / 2) + 8, (height / 2) - (m_containerHeight / 2) + 18, 0, 0, 160, 101, 256, 256);
        }
        else
        {
        	drawCenteredString(minecraft.fontRenderer,  "No Upload History", (width / 2), ((height / 2) - (m_containerHeight / 2)) + 132, 0xFFFFFFFF);
        }
        
		super.drawScreen(i, j, f);
		
	}
	
    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height, uvwidth, uvheight
     */
    public void drawTexturedModalRectSized(int x, int y, int u, int v, int width, int height, int uvwidth, int uvheight)
    {
        final float var7 = 0.00390625F;
        final  float var8 = 0.00390625F;
        
        Tessellator quad = Tessellator.instance;
        quad.startDrawingQuads();
        quad.addVertexWithUV((double)(x), (double)(y + height), (double)zLevel, (double)(u * var7), (double)((v + uvheight) * var8));
        quad.addVertexWithUV((double)(x + width), (double)(y + height), (double)zLevel, (double)((u + uvwidth) * var7), (double)((v + uvheight) * var8));
        quad.addVertexWithUV((double)(x + width), (double)(y), (double)zLevel, (double)((u + uvwidth) * var7), (double)(v * var8));
        quad.addVertexWithUV((double)(x), (double)(y), (double)zLevel, (double)(u * var7), (double)(v * var8));
        quad.draw();
    }
	
	/**
	 * Called when a button is pressed by a user
	 */
	public void actionPerformed(GuiButton button){
		
		switch (button.id)
		{
			case BUTTON_PREVIOUS:
			{
				m_currentImage--;
				if (m_currentImage < 0) m_currentImage = m_mod.uploadHistorySize() - 1;
				if (m_currentImage < 0) m_currentImage = 0;
			}
			break;
			case BUTTON_NEXT:
			{
				m_currentImage++;
				if (m_currentImage >= m_mod.uploadHistorySize()) m_currentImage = 0;
			}
			break;
		}
	}
		
	/**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        
        if (x < (width / 2) - (m_containerWidth / 2) + 12) return;
        if (x > (width / 2) - (m_containerWidth / 2) + m_containerWidth - 12) return;
        if (y < ((height / 2) - (m_containerHeight / 2)) + 148) return;
        if (y > ((height / 2) - (m_containerHeight / 2)) + 158) return;
        
        UploadedImage imageInfo = m_mod.getUploadedImage(m_currentImage);
        if (imageInfo != null)
        {        	
        	mc.displayGuiScreen(new GuiConfirmOpenLink(this, imageInfo.getUrl(), 0));
        }
        
    }
    
    /**
     * Called when the user clicks a button on the 'should i open that link' gui
     */
    public void confirmClicked(boolean openUrl, int par2) {
    	
    	if (openUrl)
    	{
    		UploadedImage imageInfo = m_mod.getUploadedImage(m_currentImage);
    		if (imageInfo != null)
    		{
	    		try
	            {
	                Class var2 = Class.forName("java.awt.Desktop");
	                Object var3 = var2.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
	                var2.getMethod("browse", new Class[] {URI.class}).invoke(var3, new Object[] {URI.create(imageInfo.getUrl())});
	            }
	            catch (Throwable var4)
	            {
	                var4.printStackTrace();
	            }
	    	}
    	}
    	
    	mc.displayGuiScreen(this);
    	
    }

}
