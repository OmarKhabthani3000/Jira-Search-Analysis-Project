/*
 * Image.java
 *
 * Created on April 10, 2004, 7:58 PM
 */

package com.shadowcraft.desktopbeautifier;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import java.awt.Color;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javax.media.jai.RenderedImageAdapter;


/**
 * Represents an image.
 *
 * @author Gili Tzabari
 * @hibernate.class table="image"
 */
public class Image
{
  /**
   * Image URL.
   */
  private URL url;
  /**
   * Median color of image.
   */
  private Color medianColor = null;
  /**
   * Brief information text associated with image.
   */
  private String information;
  /**
   * Keywords associated with image.
   */
  private List keywords;
  /**
   * BufferedImage associate with Image.
   */
  private transient SoftReference bufferedImage = new SoftReference(null);
  
  
  /** Creates a new instance of Image */
  public Image()
  {
  }
  
  /**
   * Returns BufferedImage representation of image.
   *
   * @throws IOException
   */
  public BufferedImage getBufferedImage() throws IOException
  {
    BufferedImage result = (BufferedImage) bufferedImage.get();
    if (result == null)
    {
      try
      {
        String urlString = url.toString();
        int beginning = urlString.lastIndexOf(".");
        String extension = urlString.substring(beginning+1).toUpperCase();
        String encoding;
        
        if (extension.equalsIgnoreCase("jpg"))
          encoding = "JPEG";
        else
          encoding = extension;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        ImageDecoder decoder = ImageCodec.createImageDecoder(encoding, in, null);
        if (decoder==null)
          throw new IllegalStateException("Cannot find " + encoding + " decoder");
        RenderedImage image = decoder.decodeAsRenderedImage();
        RenderedImageAdapter ria = new RenderedImageAdapter(image);
        result = ria.getAsBufferedImage();
        bufferedImage = new SoftReference(result);
      }
      catch (FileNotFoundException e)
      {
        return null;
      }
      catch (NoRouteToHostException e)
      {
        return null;
      }
    }
    return result;
  }
  
  /**
   * Returns median color of image.
   *
   * @throws IOException
   */
  public Color getMedianColor() throws IOException
  {
    if (medianColor==null)
    {
      final HashMap colorFrequencyMap = new HashMap();
      final BufferedImage image = getBufferedImage();
      final int width = image.getWidth();
      final int height = image.getHeight();
      final int[] array = new int[width*height];
      Integer value;
      Color key;
      
      for (int y=0; y<height; ++y)
      {
        image.getRGB(0, y, width, 1, array, 0, width);
        for (int x=0; x<width; ++x)
        {
          key = new Color(array[x]);
          if (!colorFrequencyMap.containsKey(key))
            value = new Integer(1);
          else
            value = new Integer(((Integer) colorFrequencyMap.get(key)).intValue() + 1);
          colorFrequencyMap.put(key, value);
        }
      }
      Color result = (Color) Collections.max(colorFrequencyMap.keySet(), new Comparator()
      {
        public int compare(Object o1, Object o2)
        {
          return ((Integer) colorFrequencyMap.get(o1)).compareTo(colorFrequencyMap.get(o2));
        }
        
        public boolean equals(Object obj)
        {
          return obj==this;
        }
      });
    }
    return medianColor;
  }
  
  /**
   * Returns URL associated with image.
   *
   * @hibernate.id generator-class="native"
   */
  public URL getURL()
  {
    return url;
  }
  
  /**
   * Sets URL associated with image.
   */
  public void setURL(URL url)
  {
    this.url = url;
  }
  
  /**
   * Returns brief information text associated with image.
   *
   * @hibernate.property
   */
  public String getInformation()
  {
    return information;
  }
  
  /**
   * Sets brief information text associated with image.
   */
  public void setInformation(String information)
  {
    this.information = information;
  }
  
  /**
   * Get keywords associated with Image.
   *
   * @hibernate.list
   */
  public List getKeywords()
  {
    return keywords;
  }
  
  /**
   * Set keywords associated with Image.
   */
  public void setKeywords(List keywords)
  {
    this.keywords = keywords;
  }
}