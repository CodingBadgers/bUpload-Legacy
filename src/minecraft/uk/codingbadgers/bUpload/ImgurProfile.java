package uk.codingbadgers.bUpload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraftforge.common.Property;

public class ImgurProfile {

	public static final String CLIENT_ID = "e2d63c64042ba1a";
	private static String accessToken = null;
	private static String refreshToken = null;
	private static String username = null;
	private static Date tokenExpire = null;
	
	public static void loadRefreshToken() {
		bUpload.CONFIG.load();
		Property refreshToken = bUpload.CONFIG.get("Authentication", "refreshToken", "");
		ImgurProfile.refreshToken = refreshToken.getString();
		bUpload.CONFIG.save();
		
		try {
			refreshAccessToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveRefreshToken() {
		bUpload.CONFIG.load();
		Property refreshToken = bUpload.CONFIG.get("Authentication", "refreshToken", "");
		refreshToken.set(ImgurProfile.refreshToken);
		bUpload.CONFIG.save();
	}
	
	public static void setTokens(String refresh) {
		refreshToken = refresh;
		try {
			refreshAccessToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void refreshAccessToken() throws IOException {
		if (refreshToken == null || refreshToken.length() < 1) {
			return;
		}
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
		nameValuePairs.add(new BasicNameValuePair("client_secret", "d435f03cf62b7ec5589ae4f122354d4a435105d7"));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
		nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));

		HttpPost post = new HttpPost("https://api.imgur.com/oauth2/token");
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse resp = client.execute(post);
        String result = EntityUtils.toString(resp.getEntity());
        
        JsonObject object = new JsonParser().parse(result).getAsJsonObject();
        
        if (object.has("access_token")) {
        	accessToken = object.get("access_token").getAsString();
        	refreshToken = object.get("refresh_token").getAsString();
        	username = object.get("account_username").getAsString();
        	tokenExpire = new Date(new Date().getTime() + 3600000);
        	
        	saveRefreshToken();
        	bUpload.sendChatMessage("Logged in on Imgur as " + username);
        } else {
        	accessToken = null;
        	refreshToken = "";
        	tokenExpire = null;
        	username = null;
        	bUpload.sendChatMessage(object.get("data").getAsJsonObject().get("error").getAsString());
        }
	}
	
	public static String getAccessToken() {
		if (accessToken == null || tokenExpire == null || tokenExpire.before(new Date())) {
			try {
				refreshAccessToken();
			} catch (IOException e) {
				e.printStackTrace();	
				bUpload.sendChatMessage(e.getMessage());
			}
		}
		
		return accessToken;
	}
	
	public static String getUsername() {
		return username;
	}

	public static void forgetProfile() {
    	accessToken = null;
    	tokenExpire = null;
    	refreshToken = "";
    	username = null;
    	
    	saveRefreshToken();
	}

}
