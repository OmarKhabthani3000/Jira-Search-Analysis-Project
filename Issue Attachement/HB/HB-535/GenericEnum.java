/**
 * Created by User: Sharenkov
 * Date: 01.10.2003
 * Time: 11:33:56
 */
package com.enterra.surv.po.enum;

import com.enterra.surv.resource.ResourceStore;
import com.enterra.surv.system.SessionContext;
import net.sf.hibernate.PersistentEnum;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.asset.ExternalAsset;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Root class for all persistent enum classes.
 * Contains the base functionality
 *
 * Each instance of Generic Enum or derived classes represens
 * one typized constans.
 * <ul>
 * <li>has a integer numer inside type
 * <li>has a unique in system text constant (use for localized text
 * description of value and so on)
 * <li>has Asset to represent constant as image (image should be size
 * of 16x16 pixels
 * </ul>
 * @author Konstantin Sharenkov
 */
public class GenericEnum implements PersistentEnum, Serializable
{
    private static final String DEFAULT_PICTURE_PATH = "img/no_picture.png";

    private int value;
    private final String text;
    private final String assetPath;
    private transient IAsset asset;

    /**
     * Create instance with default assest
     *
     * @param value - numeric value
     * @param text - unique text constant
     */
    public GenericEnum( final int value, final String text )
    {
        this( value, text, null );
    }

    /**
     * Create instance with spacified path to asset resource
     *
     * @param value - numeric value
     * @param text - unique text constant
     * @param assetPath - path to picture
     */
    public GenericEnum( final int value, final String text, final String assetPath )
    {
        this.value = value;
        this.text = text;
        this.assetPath = assetPath;
        this.asset = null;
    }

    /**
     * @return unique text constant
     */
    public final String getText()
    {
        return text;
    }

    /**
     * @return int value
     */
    public final int getValue()
    {
        return value;
    }

    /**
     * @return int value
     */
    public final int toInt()
    {
        return value;
    }

    /**
     * @return path to asset (picture)
     */
    public final String getAssetPath()
    {
        return assetPath;
    }

    private void writeObject( java.io.ObjectOutputStream out ) throws IOException
    {
        out.writeInt( value );
    }

    private void readObject( java.io.ObjectInputStream in ) throws IOException
    {
        value = in.readInt();
    }

    /**
     * *Lazy initialized
     * @return picture asset
     */
    public final IAsset getAsset()
    {
        if( asset == null )
        {
            if( assetPath == null )
                asset = new ExternalAsset( DEFAULT_PICTURE_PATH, null );
            else
                asset = new ExternalAsset( assetPath, null );
        }
        return asset;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals( final Object o )
    {
        if( this == o ) return true;
        if( !(o instanceof GenericEnum) ) return false;

        final GenericEnum other = (GenericEnum) o;

        if( getValue() != other.getValue() ) return false;

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return getValue();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString()
    {
        return getText();
    }

//    /**
//     * Converts text constant to Description for current user locale
//     * @param cycle
//     * @return
//     */
//    public final String getLocaleText( final IRequestCycle cycle )
//    {
//        final IPage page = cycle.getPage( "enums_texts" );
//        return page.getMessage( getText() );
//    }
//
    /**
     * Utility function to convert int value to object
     * @param values list of all consts
     * @param value to find
     * @return
     */
    protected static GenericEnum fromListByValue( final List values, final int value )
    {
        for( int i = 0; i < values.size(); i++ )
        {
            final GenericEnum ge = (GenericEnum) values.get( i );
            if( ge.getValue() == value ) return ge;
        }
        return null;
    }


    public final String getLocalText()
    {
        Locale locale = SessionContext.getInstance().getLanguage().getLocale();
        return getLocalText( locale );
    }

    public final String getLocalText( Locale locale )
    {
        return ResourceStore.EnumResourceBundle.format( getText(), locale );
    }
}
