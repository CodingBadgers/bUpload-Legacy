package uk.codingbadgers.Gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiCheckBox extends GuiButton
{
	private boolean m_checked = false;
	
    public GuiCheckBox(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
    	super(par1, par2, par3, par4, par5, par6Str);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean par1)
    {
        byte var2 = 1;

        if (!this.enabled)
        {
            var2 = 0;
        }
        else if (par1)
        {
            var2 = 2;
        }

        return var2;
    }
    
    public void setChecked(boolean check) {
    	m_checked = check;
    }
    
    public boolean getChecked() {
    	return m_checked;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft par1Minecraft, int par2, int par3)
    {
        if (this.drawButton)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1Minecraft.renderEngine.getTexture("/gui/tcb-gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);
            
            //this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var5 * 20, this.width / 2, this.height);
            //this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var5 * 20, this.width / 2, this.height);
            
            int localYoffset = 166;
            if (var5 == 2) localYoffset = 186;
            if (m_checked) localYoffset = 146;
            
            this.mouseDragged(par1Minecraft, par2, par3);
            int var6 = 14737632;
            if (!enabled) var6 = -6250336; else if (field_82253_i) var6 = 16777120;
            
            int strlen = par1Minecraft.fontRenderer.getStringWidth(displayString);
            int extra = (width - strlen) / 4;
            
            this.drawTexturedModalRect(xPosition + extra, yPosition, 0, localYoffset, 20, 20);
            this.drawCenteredString(par1Minecraft.fontRenderer, this.displayString, (this.xPosition + this.width / 2) + extra, this.yPosition + (this.height - 8) / 2, var6);
        }
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {}

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2) 
    {
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3)
    {
    	boolean pressed = this.enabled && this.drawButton && par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;    	
        
    	if (pressed) m_checked = !m_checked;
    	
    	return pressed;
    }

    public boolean func_82252_a()
    {
        return this.field_82253_i;
    }

    public void func_82251_b(int par1, int par2) {}
}

