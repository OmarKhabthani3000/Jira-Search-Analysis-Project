/**
 * Created by User: Sharenkov
 * Date: 01.10.2003
 * Time: 11:33:41
 */
package com.enterra.surv.po.enum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.ObjectStreamException;

/**
 *
 *
 * @author Konstantin Sharenkov
 */
public final class UserType extends GenericEnum
{
    private static List enum = null;

    public static final UserType Respondent =
        new UserType( 0, "UserType.Respondent", "img/enum/user_type.respondent.png" );
    public static final UserType Client =
        new UserType( 1, "UserType.Client", "img/enum/user_type.client.png" );
    public static final UserType Admin =
        new UserType( 2, "UserType.Admin", "img/enum/user_type.admin.png" );

    private UserType( final int value, final String text, final String assetPath )
    {
        super( value, text, assetPath );
    }

    public static UserType fromInt( final int value )
    {
        return (UserType) fromListByValue( getEnum(), value );
    }

    public static synchronized List getEnum()
    {
        if( enum == null )
        {
            enum = new ArrayList();
            enum.add( Respondent );
            enum.add( Client );
            enum.add( Admin );
            enum = Collections.unmodifiableList( enum );
        }
        return enum;
    }

    private Object readResolve() throws ObjectStreamException
    {
        return fromInt(getValue());
    }
}
