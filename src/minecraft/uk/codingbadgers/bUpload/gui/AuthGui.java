package uk.codingbadgers.bUpload.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import uk.codingbadgers.bUpload.handlers.auth.AuthTypes;

public class AuthGui extends bUploadGuiScreen {

	protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

	private static final int ADD_AUTH_OFFSET = 2 << 2;
	private static final int CANCEL = 1;
	
	public AuthGui(bUploadGuiScreen screen) {
		super(screen);
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, I18n.getString("image.auth.title"), width / 2, height / 5 - 20, 0xffffff);
		super.drawScreen(i, j, f);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		int ypos = (height / 5);
		int buttonwidth = 160;

		for (AuthTypes handler : AuthTypes.values()) {
			GuiButton button = new GuiButton(handler.ordinal() + ADD_AUTH_OFFSET, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.getString("image.auth.type." + handler.toString().toLowerCase()));
	        buttonList.add(button);
	        ypos += 24;
		}

		ypos = (height / 5) * 4;
		GuiButton exit = new GuiButton(CANCEL, width / 2 - (buttonwidth / 2), ypos, buttonwidth, 20, I18n.getString("image.auth.cancel"));
		buttonList.add(exit);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == CANCEL) {
			Minecraft.getMinecraft().displayGuiScreen(parent);
			return;
		}
		
		try {
			AuthTypes type = AuthTypes.getByID(par1GuiButton.id - ADD_AUTH_OFFSET);
			AddAuthGui gui = type.getAuthGui(this);
			
			if (gui == null) {
				return;
			}
			
			Minecraft.getMinecraft().displayGuiScreen(gui);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
