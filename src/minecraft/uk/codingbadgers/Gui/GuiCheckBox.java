package uk.codingbadgers.Gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiCheckBox extends GuiButton
{
    /** The current checked state of the check box */
    private boolean m_checked = false;

    /** The amount of space between the check box and its label */
    private final static int BOX_LABEL_SPACER = 5;

    /**
     * Default check box constructor
     * @param id 				The id of the callback used in actionPerformed.
     * @param xPosition			The x coordinate of the position of the check box
     * @param yPosition			The y coordinate of the position of the check box
     * @param width				The width of the check box
     * @param height			The height of the check box
     * @param label				The label of the check box
     */
    public GuiCheckBox(int id, int xPosition, int yPosition, int width, int height, String label)
    {
        super(id, xPosition, yPosition, width, height, label);
    }

    /**
     * Returns 0 if the check box is disabled,
     * 1 if the mouse is NOT hovering over this check box
     * and 2 if it is hovering over this check box.
     * 
     * @param isMouseOver 	if the mouse is over the 
     */
    protected int getHoverState(boolean isMouseOver)
    {
        if (!enabled)
        {
            return 0;
        }

        if (isMouseOver)
        {
            return 2;
        }

        return 1;
    }

    /**
     * Sets the current checked state of the check box
     * @param check			True to set the checked state to true, false otherwise
     */
    public void setChecked(boolean check)
    {
        m_checked = check;
    }

    /**
     * Get the current checked state of the check box
     * @return True if checked, false otherwise
     */
    public boolean getChecked()
    {
        return m_checked;
    }

    /**
     * Draws the check box to the screen.
     * @param minecraft				The minecraft instance
     * @param mouseX				The x coordinate of the mouse
     * @param mouseY				The y coordinate of the mouse
     */
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (!drawButton)
        {
            return;
        }

        // field_82253_i represents if the mouse is over the check box region
        field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        // get the hover state of the mouse and check box
        final int hoverState = getHoverState(field_82253_i);
        // work out the local offset into the image atlas
        int localYoffset = 166;

        if (hoverState == 2)
        {
            localYoffset = 186;
        }

        if (m_checked)
        {
            localYoffset = 146;
        }

        // get a color offset based upon the hover state
        final int hoverColor = enabled == false ? -6250336 : hoverState == 2 ? 16777120 : 14737632;
        // work out an offset to add the check box image and center the check box
        final int labelWidth = minecraft.fontRenderer.getStringWidth(displayString);
        final int checkboxImageSize = 20;
        final int xOffset = xPosition + ((width / 2) - ((labelWidth + checkboxImageSize + BOX_LABEL_SPACER) / 2));
        // draw the check box label
        drawString(minecraft.fontRenderer, displayString, xOffset + checkboxImageSize + BOX_LABEL_SPACER, yPosition + (height - 8) / 2, hoverColor);
        // load our check box image
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.renderEngine.getTexture("/gui/tcb-gui.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        // draw the check box
        drawTexturedModalRect(xOffset, yPosition, 0, localYoffset, checkboxImageSize, checkboxImageSize);
    }

    /**
     * Handle a mouse pressed event
     * @param minecraft			The minecraft instance
     * @param mouseX			The x coordinate of the mouse
     * @param mouseY			The y coordinate of the mouse
     *
     * @return True if the mouse was pressed whilst over the check box, false otherwise
     */
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (!enabled)
        {
            return false;
        }

        if (!drawButton)
        {
            return false;
        }

        boolean pressed = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;

        // Change the check state as the check box has been clicked
        if (pressed)
        {
            m_checked = !m_checked;
        }

        return pressed;
    }

    /**
     * @return if the mouse is currently over the check box
     */
    public boolean func_82252_a()
    {
        return this.field_82253_i;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {}

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int par1, int par2) {}

    /**
     *
     */
    public void func_82251_b(int par1, int par2) {}
}
