package uk.codingbadgers.bUpload.handlers.upload;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import uk.codingbadgers.bUpload.Screenshot;
import uk.codingbadgers.bUpload.UploadedImage;
import uk.codingbadgers.bUpload.handlers.ConfigHandler;
import uk.codingbadgers.bUpload.handlers.HistoryHandler;
import uk.codingbadgers.bUpload.handlers.MessageHandler;

public class HDUploadHandler extends UploadHandler {

    public HDUploadHandler(Screenshot screen) {
        super(screen);
    }

    @Override
    public boolean run(Screenshot screenshot) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (screenshot != null && screenshot.imageID != 0) {

            String path = ConfigHandler.formatImagePath(minecraft);
            File outputFile = new File(minecraft.mcDataDir, path);

            if (outputFile != null) {
                if (!outputFile.getParentFile().exists()) {
                    outputFile.getParentFile().mkdirs();
                }

                try {
                    ImageIO.write(screenshot.image, ConfigHandler.IMAGE_FORMAT, outputFile);
                } catch (IOException e) {
                    MessageHandler.sendChatMessage("image.upload.fail", e.getMessage());
                    e.printStackTrace();
                    return false;
                }

                ChatMessageComponent message = new ChatMessageComponent();
                message.addFormatted("image.upload.success");
                ChatMessageComponent url = new ChatMessageComponent();
                url.addText("Disk").setColor(EnumChatFormatting.GOLD);
                message.appendComponent(url);
                
                MessageHandler.sendChatMessage(message);
                HistoryHandler.addUploadedImage(new UploadedImage(outputFile.getParent(), path, screenshot, true));
                return true;
            }
        }
        return false;
    }

}
