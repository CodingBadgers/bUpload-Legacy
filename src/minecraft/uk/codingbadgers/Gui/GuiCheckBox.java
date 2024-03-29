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
package uk.codingbadgers.Gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class GuiCheckBox extends GuiButton {
	/** The current checked state of the check box */
	private boolean m_checked = false;

	/** The amount of space between the check box and its label */
	private final static int BOX_LABEL_SPACER = 5;

	/**
	 * Default check box constructor
	 * 
	 * @param id
	 *            The id of the callback used in actionPerformed.
	 * @param xPosition
	 *            The x coordinate of the position of the check box
	 * @param yPosition
	 *            The y coordinate of the position of the check box
	 * @param width
	 *            The width of the check box
	 * @param height
	 *            The height of the check box
	 * @param label
	 *            The label of the check box
	 */
	public GuiCheckBox(int id, int xPosition, int yPosition, int width, int height, String label) {
		super(id, xPosition, yPosition, width, height, label);
	}

	/**
	 * Returns 0 if the check box is disabled, 1 if the mouse is NOT hovering
	 * over this check box and 2 if it is hovering over this check box.
	 * 
	 * @param isMouseOver
	 *            if the mouse is over the check box
	 */
	protected int getHoverState(boolean isMouseOver) {
		if (!enabled) {
			return 0;
		}

		if (isMouseOver) {
			return 2;
		}

		return 1;
	}

	/**
	 * Sets the current checked state of the check box
	 * 
	 * @param check
	 *            True to set the checked state to true, false otherwise
	 */
	public void setChecked(boolean check) {
		m_checked = check;
	}

	/**
	 * Get the current checked state of the check box
	 * 
	 * @return True if checked, false otherwise
	 */
	public boolean getChecked() {
		return m_checked;
	}

	/**
	 * Draws the check box to the screen.
	 * 
	 * @param minecraft
	 *            The minecraft instance
	 * @param mouseX
	 *            The x coordinate of the mouse
	 * @param mouseY
	 *            The y coordinate of the mouse
	 */
	public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
		if (!drawButton) {
			return;
		}

		// field_82253_i represents if the mouse is over the check box region
		field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		// get the hover state of the mouse and check box
		final int hoverState = getHoverState(field_82253_i);
		// work out the local offset into the image atlas
		int localYoffset = 166;

		if (hoverState == 2) {
			localYoffset = 186;
		}

		if (m_checked) {
			localYoffset = 146;
		}

		final int hoverColor = enabled == false ? -6250336 : hoverState == 2 ? 16777120 : 14737632;
		final int labelWidth = minecraft.fontRenderer.getStringWidth(displayString);
		final int checkboxImageSize = 20;
		final int xOffset = xPosition + ((width / 2) - ((labelWidth + checkboxImageSize + BOX_LABEL_SPACER) / 2));

		drawString(minecraft.fontRenderer, displayString, xOffset + checkboxImageSize + BOX_LABEL_SPACER, yPosition + (height - 8) / 2, hoverColor);
		minecraft.renderEngine.func_110577_a(new ResourceLocation("bUpload:textures/gui/tcb-gui.png"));
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(xOffset, yPosition, 0, localYoffset, checkboxImageSize, checkboxImageSize);
	}

	/**
	 * Handle a mouse pressed event
	 * 
	 * @param minecraft
	 *            The minecraft instance
	 * @param mouseX
	 *            The x coordinate of the mouse
	 * @param mouseY
	 *            The y coordinate of the mouse
	 * 
	 * @return True if the mouse was pressed whilst over the check box, false
	 *         otherwise
	 */
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
		if (!enabled) {
			return false;
		}

		if (!drawButton) {
			return false;
		}

		boolean pressed = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;

		// Change the check state as the check box has been clicked
		if (pressed) {
			m_checked = !m_checked;
		}

		return pressed;
	}

	/**
	 * @return if the mouse is currently over the check box
	 */
	public boolean func_82252_a() {
		return this.field_82253_i;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of
	 * MouseListener.mouseDragged(MouseEvent e).
	 */
	protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
	}

	/**
	 * Fired when the mouse button is released. Equivalent of
	 * MouseListener.mouseReleased(MouseEvent e).
	 */
	public void mouseReleased(int par1, int par2) {
	}

	/**
     *
     */
	public void func_82251_b(int par1, int par2) {
	}
}
