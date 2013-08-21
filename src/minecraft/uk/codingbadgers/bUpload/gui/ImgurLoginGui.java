package uk.codingbadgers.bUpload.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import uk.codingbadgers.bUpload.ImgurProfile;
import uk.codingbadgers.bUpload.bUpload;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class ImgurLoginGui extends GuiScreen {

	private static final String URL = "https://api.imgur.com/oauth2/authorize?client_id=" + ImgurProfile.CLIENT_ID + "&response_type=pin&state=bUpload";
	private static final int ACCEPT = 0;
	private static final int CANCEL = 1;
	private GuiTextField pinCode;
	private boolean linkGiven = false;
	private bUploadGuiScreen parent;

	public ImgurLoginGui(bUploadGuiScreen parent) {
		this.parent = parent;
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if (!pinCode.isFocused()) {
			pinCode.setFocused(true);
		}

		pinCode.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch (par1GuiButton.id) {
		case ACCEPT:
			try {
				getAccessToken();
			} catch (Exception e) {
				e.printStackTrace();
			}
			parent.updateLogin();
			mc.displayGuiScreen(parent);
			break;
		case CANCEL:
			parent.updateLogin();
			mc.displayGuiScreen(parent);
			break;
		default:
			break;
		}
	}

	private void getAccessToken() throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost("https://api.imgur.com/oauth2/token");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("client_id", ImgurProfile.CLIENT_ID));
		nameValuePairs.add(new BasicNameValuePair("client_secret", "d435f03cf62b7ec5589ae4f122354d4a435105d7"));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "pin"));
		nameValuePairs.add(new BasicNameValuePair("pin", pinCode.getText()));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(post);
		String result = EntityUtils.toString(resp.getEntity());

		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(result).getAsJsonObject();

		if (json.has("success") && !json.get("success").getAsBoolean()) {
			bUpload.sendChatMessage(json.get("data").getAsJsonObject().get("error").getAsString());
			return;
		}

		String refresh = json.get("refresh_token").getAsString();
		ImgurProfile.setTokens(refresh);
	}

	@Override
	public void confirmClicked(boolean accepted, int par2) {
		if (accepted) {
			openLink();
			mc.displayGuiScreen(this);
		} else {
			mc.displayGuiScreen(null);
		}
	}

	private void openLink() {
		try {
			Desktop dt = Desktop.getDesktop();
			dt.browse(URI.create(URL));
			linkGiven = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		if (!linkGiven) {
			if (mc.gameSettings.chatLinksPrompt) {
				mc.displayGuiScreen(new GuiConfirmOpenLink(this, URL, 0, false));
			} else {
				openLink();
			}
		}

		int buttonWidth = 100;
		int buttonHeight = 20;

		buttonList.add(new GuiButton(ACCEPT, this.width / 2 - buttonWidth - 8, this.height / 6 + 96, buttonWidth, buttonHeight, "Accept"));
		buttonList.add(new GuiButton(CANCEL, this.width / 2 + 8, this.height / 6 + 96, buttonWidth, buttonHeight, "Cancel"));

		pinCode = new GuiTextField(mc.fontRenderer, this.width / 2 - buttonWidth - 8, this.height / 6 + 64, buttonWidth * 2 + 16, buttonHeight);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		drawCenteredString(mc.fontRenderer, "Please enter the pin you get", this.width / 2, this.height / 6 + 30, 0xFFFFFF);
		drawCenteredString(mc.fontRenderer, "given from the imgur", this.width / 2, this.height / 6 + 40, 0xFFFFFF);
		pinCode.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
}
