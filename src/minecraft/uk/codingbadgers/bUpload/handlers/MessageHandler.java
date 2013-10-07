package uk.codingbadgers.bUpload.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class MessageHandler {

	private static final Minecraft mc = Minecraft.getMinecraft();
	
    public static void sendChatMessage(String message) {
    	MessageHandler.sendChatMessage(message, true);
    }

    @Deprecated
    public static void sendChatMessage(String key, boolean translate) {
    	String message = key;
    	
    	if (translate) {
    		message = I18n.getString(key);
    	}

    	if (mc == null || mc.thePlayer == null || mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) {
    		System.err.println(message);
    		return;
    	}
    	
    	ChatMessageComponent chat = new ChatMessageComponent();
    	ChatMessageComponent prefix = new ChatMessageComponent();
    	prefix.addText("[bUpload] ").setColor(EnumChatFormatting.GOLD).setBold(true);
    	ChatMessageComponent main = new ChatMessageComponent();
    	main.addText(message).setColor(EnumChatFormatting.WHITE);
    	chat.appendComponent(prefix);
    	chat.appendComponent(main);
    	
    	mc.ingameGUI.getChatGUI().printChatMessage(chat.toStringWithFormatting(true));
    }

    public static void sendChatMessage(String key, Object... object) {
        sendChatMessage(key, true, object);
    }
    
    @Deprecated
    public static void sendChatMessage(String key, boolean translate, Object... object) {
    	String message = key;
    	
    	if (translate) {
    		message = I18n.getStringParams(key, object);
    	}
    	
    	if (mc == null || mc.thePlayer == null || mc.ingameGUI == null || mc.ingameGUI.getChatGUI() == null) {
    		System.err.println(message);
    		return;
    	}

    	ChatMessageComponent chat = new ChatMessageComponent();
    	ChatMessageComponent prefix = new ChatMessageComponent();
    	prefix.addText("[bUpload] ").setColor(EnumChatFormatting.GOLD).setBold(true);
    	ChatMessageComponent main = new ChatMessageComponent();
    	main.addText(message).setColor(EnumChatFormatting.WHITE);
    	chat.appendComponent(prefix);
    	chat.appendComponent(main);

    	System.out.println(chat.toJson());
    	mc.ingameGUI.getChatGUI().printChatMessage(chat.toStringWithFormatting(true));
    }
    
    public static void sendChatMessage(ChatMessageComponent message) {
    	if (message.getColor() == null) {
    		message.setColor(EnumChatFormatting.WHITE);
    	}
    	
    	ChatMessageComponent chat = new ChatMessageComponent();
    	ChatMessageComponent prefix = new ChatMessageComponent();
    	prefix.addText("[bUpload] ").setColor(EnumChatFormatting.GOLD).setBold(true);
    	chat.appendComponent(prefix);
    	chat.appendComponent(message);

    	mc.ingameGUI.getChatGUI().printChatMessage(chat.toStringWithFormatting(true));
    }

}
