package uk.codingbadgers.bUpload.handlers.upload;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uk.codingbadgers.bUpload.Screenshot;
import uk.codingbadgers.bUpload.UploadedImage;
import uk.codingbadgers.bUpload.bUpload;
import uk.codingbadgers.bUpload.handlers.ConfigHandler;
import uk.codingbadgers.bUpload.handlers.HistoryHandler;
import uk.codingbadgers.bUpload.handlers.MessageHandler;
import uk.codingbadgers.bUpload.handlers.auth.ImgurAuthHandler;

public class ImgurUploadHandler extends UploadHandler {

    public ImgurUploadHandler(Screenshot screen) {
        super(screen);
    }

    @Override
    public boolean run(Screenshot screen) {
        try {
            String title = ConfigHandler.SAVE_DATE_FORMAT.format(new Date());
            String description = "A minecraft screenshot ";
            
            if (Minecraft.getMinecraft().isSingleplayer()) {
                description += "in " + Minecraft.getMinecraft().getIntegratedServer().getFolderName();
            } else {
                description += "on " + bUpload.server + (bUpload.port != 25565 ? ":" + bUpload.port : "");
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screen.image, "png", baos);
            String data = Base64.encodeBase64String(baos.toByteArray());

            List<NameValuePair> arguments = new ArrayList<NameValuePair>(3);
            arguments.add(new BasicNameValuePair("client_id", ImgurAuthHandler.CLIENT_ID));
            arguments.add(new BasicNameValuePair("image", data));
            arguments.add(new BasicNameValuePair("type", "base64"));
            arguments.add(new BasicNameValuePair("title", title));
            arguments.add(new BasicNameValuePair("description", description));

            HttpPost hpost = new HttpPost("https://api.imgur.com/3/upload");
            hpost.setEntity(new UrlEncodedFormEntity(arguments));

            if (ImgurAuthHandler.getInstance().getAccessToken() != null) {
                hpost.addHeader("Authorization", "Bearer " + ImgurAuthHandler.getInstance().getAccessToken());
            } else {
                hpost.addHeader("Authorization", "Client-ID " + ImgurAuthHandler.CLIENT_ID);
            }

            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(hpost);
            String result = EntityUtils.toString(resp.getEntity());

            JsonObject responce = new JsonParser().parse(result).getAsJsonObject();
            JsonObject responceData = responce.get("data").getAsJsonObject();

            if (responce.has("success") && responce.get("success").getAsBoolean()) {
                final String uploadUrl = responceData.get("link").getAsString();

                HistoryHandler.addUploadedImage(new UploadedImage(title, uploadUrl, screen, false));
                
                ChatMessageComponent message = new ChatMessageComponent();
                message.addFormatted("image.upload.success");
                ChatMessageComponent url = new ChatMessageComponent();
                url.addText(uploadUrl).setColor(EnumChatFormatting.GOLD);
                message.appendComponent(url);
                
                MessageHandler.sendChatMessage(message);

                if (ConfigHandler.COPY_URL_TO_CLIPBOARD) {
                    GuiScreen.setClipboardString(uploadUrl);
                    MessageHandler.sendChatMessage("image.upload.copy");
                }
            } else {
                MessageHandler.sendChatMessage("image.upload.fail", "Imgur", responce.get("status").getAsInt());
                MessageHandler.sendChatMessage(responceData.get("error").getAsString());
                return false;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageHandler.sendChatMessage("image.upload.fail", "Imgur", ex.getMessage());
            return false;
        }
    }

}
