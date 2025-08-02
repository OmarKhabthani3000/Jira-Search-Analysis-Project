/**
 * Created by User: Sharenkov
 * Date: 01.10.2003
 * Time: 12:43:00
 */
package com.enterra.surv.po;

import com.enterra.surv.po.enum.StyleType;


/**
 *
 *
 * @author Konstantin Sharenkov
 */
public class Style extends Persistent
{
    private String name;
    private String path;

    private StyleType styleType;

    public transient static Style DEFAULT_STYLE = new Style( "default" , "style/default" );

    public Style()
    {
        super();
    }

    private Style(String name , String path)
    {
        super();
        this.name = name;
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }


    public void remove()
    {
        setArchived( true );
    }

    public StyleType getStyleType()
    {
        return styleType;
    }

    public void setStyleType(StyleType styleType)
    {
        this.styleType = styleType;
    }
}
