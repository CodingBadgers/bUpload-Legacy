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
package uk.codingbadgers.bUpload;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.gui.GuiScreen;

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

public class ImageUploadThread implements Runnable
{
    private Screenshot 		m_image = null;

    public ImageUploadThread(Screenshot image)
    {
        m_image = image;
    }

    @Override
    public void run()
    {
        Upload(m_image);
    }

    /**
     * Upload the buffered image to imgur
     * @param image The image to upload
     * @return a formatted url to the uploaded image, or an error message
     */
    private boolean Upload(Screenshot image)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image.image, "png", baos);
            String data = Base64.encodeBase64String(baos.toByteArray()).toString();
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("client_id", ImgurProfile.CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("image", data));
            nameValuePairs.add(new BasicNameValuePair("type", "base64"));
            
            HttpPost hpost = new HttpPost("https://api.imgur.com/3/upload");
            hpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
           
            if (ImgurProfile.getAccessToken() != null) {
            	hpost.addHeader("Authorization", "Bearer " + ImgurProfile.getAccessToken());
            } else {
            	hpost.addHeader("Authorization", "Client-ID " + ImgurProfile.CLIENT_ID);
            }
            
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(hpost);
            String result = EntityUtils.toString(resp.getEntity());
            
            JsonObject responce = new JsonParser().parse(result).getAsJsonObject();
            JsonObject responceData = responce.get("data").getAsJsonObject();
            
            if (responce.has("success") && responce.get("success").getAsBoolean()) {           
	            final String uploadUrl = responceData.get("link").getAsString();
	            final String imageName = responceData.get("datetime").getAsString();
	            
	            bUpload.addUploadedImage(new UploadedImage(imageName, uploadUrl, image, false));
	            bUpload.sendChatMessage("image.upload.success", true, uploadUrl);
	            
	            if (bUpload.SHOULD_COPY_TO_CLIPBOARD) {
	            	GuiScreen.setClipboardString(uploadUrl);
	            	bUpload.sendChatMessage("image.upload.fail", true);
	            }
            } else {
            	bUpload.sendChatMessage("image.upload.fail", true);
            	bUpload.sendChatMessage(responceData.get("error").getAsString(), false);
           	 	return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            bUpload.sendChatMessage("image.upload.fail", true);
            return false;
        }
    }
}
