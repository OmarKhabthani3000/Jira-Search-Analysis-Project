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
        setId( 0 );
        setArchived( false );
    }

    public int getId()
    {
        return id;
    }

    void setId( int id )
    {
        this.id = id;
    }

    boolean isArchived()
    {
        return archived;
    }

    void setArchived( boolean archived )
    {
        this.archived = archived;
    }

    public boolean equals( Object o )
    {
        if( this == o ) return true;
        if( !(o instanceof Persistent) ) return false;

        final Persistent persistent = (Persistent) o;

        if( getId() != persistent.getId() ) return false;

        return true;
    }

    public int hashCode()
    {
        return getId();
    }

    public String toString()
    {
        return getClass().getName() + "[" + getId() + "]";
    }

}
