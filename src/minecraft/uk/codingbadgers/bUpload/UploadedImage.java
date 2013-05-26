package uk.codingbadgers.bUpload;

public class UploadedImage
{
    /** */
    private final String m_name;

    /** */
    private final String m_url;

    /** */
    private int m_imageID;
    
    /** */
    private boolean m_localFile;

    /**
     *
     * @param name
     * @param url
     * @param image
     */
    public UploadedImage(
            String name,
            String url,
            bUploadScreenShot image,
            boolean localFile
    )
    {
        m_name = name;
        m_url = url;
        m_imageID = image.imageID;
        m_localFile = localFile;
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
    public int getImageID()
    {
        return m_imageID;
    }
    
    /**
    *
    * @return
    */
   public boolean isLocal()
   {
       return m_localFile;
   }
}
