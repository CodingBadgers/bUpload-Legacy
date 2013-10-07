package uk.codingbadgers.bUpload.gui;

import net.minecraft.client.gui.GuiScreen;

public class bUploadGuiScreen extends GuiScreen {

	protected bUploadGuiScreen parent;

    public bUploadGuiScreen(bUploadGuiScreen screen) {
        this.parent = screen;
    }

    public void updateLogin() {}
	
}
