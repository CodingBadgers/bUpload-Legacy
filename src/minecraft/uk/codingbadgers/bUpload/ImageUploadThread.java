package uk.codingbadgers.bUpload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.src.mod_bUpload;

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
    private BufferedImage 			m_image = null;
    private Minecraft 				m_minecraft = null;

    public ImageUploadThread(BufferedImage image, Minecraft minecraft)
    {
        m_image = image;
        m_minecraft = minecraft;
    }

    @Override
    public void run()
    {
        String imageUrl = Upload(m_image);
        m_minecraft.ingameGUI.getChatGUI().printChatMessage(imageUrl);
    }

    /**
     * Upload the buffered image to imgur
     * @param image The image to upload
     * @return a formatted url to the uploaded image, or an error message
     */
    private String Upload(BufferedImage image)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
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
                return mod_bUpload.COLOUR + "6[bUpload] " + mod_bUpload.COLOUR + "FFailed to upload image.";
            }

            mod_bUpload.addUploadedImage(new UploadedImage(imageName, uploadUrl));
            
            return mod_bUpload.COLOUR + "6[bUpload] " + mod_bUpload.COLOUR + "fImage uploaded to " + mod_bUpload.COLOUR + "6" + uploadUrl;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return mod_bUpload.COLOUR + "6[bUpload] " + mod_bUpload.COLOUR + "FFailed to upload image.";
        }
    }
    
    private String extractXML(String XML, String element)
    {
    	final String startElement = "<" + element + ">";
    	final String endElement = "</" + element + ">";
    	
    	final int start = XML.indexOf(startElement) + startElement.length();
    	final int end = XML.indexOf(endElement);
    	
    	if (start == -1 || end == -1)
    		return null;
    	
    	return XML.substring(start, end);
    }
}