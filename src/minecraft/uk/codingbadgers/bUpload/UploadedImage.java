package uk.codingbadgers.bUpload;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.src.ModLoader;

public class UploadedImage {

	/** */
	private final String m_name;
	
	/** */
	private final String m_url;
	
	/** */
	private int m_imageID;
	
	/**
	 * 
	 * @param name
	 * @param url
	 * @param image
	 */
	public UploadedImage(
			String name,
			String url,
			bUploadScreenShot image
		) 
	{
		m_name = name;
		m_url = url;
		m_imageID = image.imageID;
	}

	/**
	 * 
	 * @return
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUrl()
	{
		return m_url;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getImageID() {
		return m_imageID;
	}
	
}
