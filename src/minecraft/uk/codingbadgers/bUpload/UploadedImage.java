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

public class UploadedImage
{
    private final String m_name;
    private final String m_url;
    private int m_imageID;
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
            Screenshot image,
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
