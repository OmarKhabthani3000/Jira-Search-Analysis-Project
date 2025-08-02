/**
 * Created by User: Sharenkov
 * Date: 01.10.2003
 * Time: 10:59:16
 */
package com.enterra.surv.po;

import java.io.Serializable;

/**
 *
 *
 * @author Konstantin Sharenkov
 */
public class Persistent implements Serializable
{
    private int id;
    private boolean archived;

    public Persistent()
    {
        super();
        // setId( 0 ); romove to compatible 2.1 rc1
        // setArchived( false );
    }

    public final int getId()
    {
        return id;
    }

    final void setId( int id )
    {
        this.id = id;
    }

    final boolean isArchived()
    {
        return archived;
    }

    final void setArchived( boolean archived )
    {
        this.archived = archived;
    }

    public final boolean equals( Object o )
    {
        if( this == o ) return true;
        if( !(o instanceof Persistent) ) return false;

        final Persistent persistent = (Persistent) o;

        if( getId() != persistent.getId() ) return false;

        return true;
    }

    public final int hashCode()
    {
        return getId();
    }

    public final String toString()
    {
        return getClass().getName() + "[" + getId() + "]";
    }

}
