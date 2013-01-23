package uk.codingbadgers.bUpload;

public class UploadedImage {

	/** */
	private final String m_name;
	
	/** */
	private final String m_url;
	
	public UploadedImage(
			String name,
			String url
		) 
	{
		m_name = name;
		m_url = url;
	}

	public String getName()
	{
		return m_name;
	}
	
	public String getUrl()
	{
		return m_url;
	}
	
}
