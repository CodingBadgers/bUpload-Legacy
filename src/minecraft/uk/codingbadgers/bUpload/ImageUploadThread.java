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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class ImageUploadThread implements Runnable
{
    private bUploadScreenShot 		m_image = null;
    private Minecraft 				m_minecraft = null;

    public ImageUploadThread(bUploadScreenShot image, Minecraft minecraft)
    {
        m_image = image;
        m_minecraft = minecraft;
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
    private boolean Upload(bUploadScreenShot image)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image.image, "png", baos);
            String data = Base64.encodeBase64String(baos.toByteArray()).toString();
            HttpPost hpost = new HttpPost("http://api.imgur.com/2/upload.xml");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("key", "07f5f5e3b1a9d856f6ae4a4e5d814729"));
            nameValuePairs.add(new BasicNameValuePair("image", data));
            nameValuePairs.add(new BasicNameValuePair("type", "base64"));
            hpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(hpost);
            String result = EntityUtils.toString(resp.getEntity());
            final String uploadUrl = extractXML(result, "original");
            final String imageName = extractXML(result, "datetime");

            if (uploadUrl == null || imageName == null)
            {
            	 m_minecraft.ingameGUI.getChatGUI().printChatMessage(mod_bUpload.COLOUR + "6[bUpload] " + mod_bUpload.COLOUR + "FFailed to upload image.");
            	 return false;
            }

            mod_bUpload.addUploadedImage(new UploadedImage(imageName, uploadUrl, image, false));
            m_minecraft.ingameGUI.getChatGUI().printChatMessage(mod_bUpload.COLOUR + "6[bUpload] " + mod_bUpload.COLOUR + "FImage uploaded to " + mod_bUpload.COLOUR + "6" + uploadUrl + "!");
           
            if (mod_bUpload.SHOULD_COPY_TO_CLIPBOARD) {
            	GuiScreen.setClipboardString(uploadUrl);
            	m_minecraft.ingameGUI.getChatGUI().printChatMessage(mod_bUpload.COLOUR + "6[bUpload] " + mod_bUpload.COLOUR + "FUrl copied to clipboard!");
            }
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            m_minecraft.ingameGUI.getChatGUI().printChatMessage(mod_bUpload.COLOUR + "6[bUpload] " + mod_bUpload.COLOUR + "FFailed to upload image.");
            return false;
        }
    }

    private String extractXML(String XML, String element)
    {
        final String startElement = "<" + element + ">";
        final String endElement = "</" + element + ">";
        final int start = XML.indexOf(startElement) + startElement.length();
        final int end = XML.indexOf(endElement);

        if (start == -1 || end == -1)
        {
            return null;
        }

        return XML.substring(start, end);
    }
}
