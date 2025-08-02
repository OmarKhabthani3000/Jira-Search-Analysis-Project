package biz.mbisoftware.fn.ejb.entity;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;


import biz.mbisoftware.common.BeanHelper;


/**
 * Variantenverweise.
 */
@Entity
@IdClass( value = MbiVarvw.PK.class )
@Table( name = "mbi_varvw" )
@Cacheable( true )
public class MbiVarvw implements Serializable, Comparable<MbiVarvw>
{
    private static final long serialVersionUID = 1L;

    /** The length of the char column bereich (1). */
    public static final int LEN_BEREICH = 1;

    /** Variantenfamilien-Nummer. */
    @Id
    @Column( name = "var_fam_nr", nullable = false )
    private Integer varFamNr;

    /** Id.-Nr. der uebergeord. Varian.. */
    @Id
    @Column( name = "var_id_1", nullable = false )
    private Integer varId1;

    /** Bereich. */
    @Id
    @Column( name = "bereich", nullable = false, length = MbiVarvw.LEN_BEREICH )
    private String bereich;

    /** Id.-Nr. d.untergeord. Varian.. */
    @Column( name = "var_id_2", nullable = false )
    private Integer varId2;

    /**
     * Initialize all non-nullable fields.
     */
    @PrePersist
    @PreUpdate
    public void avoidNulls()
    {
        if ( this.varFamNr == null ) {
            this.varFamNr = Integer.valueOf( 0 );
        }
        if ( this.varId1 == null ) {
            this.varId1 = Integer.valueOf( 0 );
        }
        if ( this.bereich == null || this.bereich.length() == 0 ) {
            this.bereich = " ";
        }
        if ( this.varId2 == null ) {
            this.varId2 = Integer.valueOf( 0 );
        }
    }

    /**
     * @return Returns the varFamNr (Variantenfamilien-Nummer).
     */
    public Integer getVarFamNr()
    {
        return this.varFamNr;
    }

    /**
     * @param varFamNr  The varFamNr to set (Variantenfamilien-Nummer).
     */
    public void setVarFamNr( final Integer varFamNr )
    {
        this.varFamNr = varFamNr;
    }

    /**
     * @return Returns the varId1 (Id.-Nr. der uebergeord. Varian.).
     */
    public Integer getVarId1()
    {
        return this.varId1;
    }

    /**
     * @param varId1  The varId1 to set (Id.-Nr. der uebergeord. Varian.).
     */
    public void setVarId1( final Integer varId1 )
    {
        this.varId1 = varId1;
    }

    /**
     * @return Returns the bereich (Bereich).
     */
    public String getBereich()
    {
        String rv;
        if ( this.bereich == null ) {
            rv = null;
        } else {
            if ( this.bereich.trim().length() == 0 ) {
                rv = this.bereich.trim();
            } else {
                rv = this.bereich;
            }
        }
        return rv;
    }

    /**
     * @param bereich  The bereich to set (Bereich).
     */
    public void setBereich( final String bereich )
    {
        this.bereich = bereich;
    }

    /**
     * @return Returns the varId2 (Id.-Nr. d.untergeord. Varian.).
     */
    public Integer getVarId2()
    {
        return this.varId2;
    }

    /**
     * @param varId2  The varId2 to set (Id.-Nr. d.untergeord. Varian.).
     */
    public void setVarId2( final Integer varId2 )
    {
        this.varId2 = varId2;
    }

    @Override
    public final boolean equals( final Object obj )
    {
        return BeanHelper.equals( this, obj, "VarId1,VarFamNr,Bereich" );
    }

    @Override
    public final int hashCode()
    {
        return BeanHelper.hashCode( this, "VarId1,VarFamNr,Bereich" );
    }

    @Override
    public final String toString()
    {
        return BeanHelper.toString( this, "VarId1,VarFamNr,Bereich" );
    }

    @Override
    public final int compareTo( final MbiVarvw obj )
    {
        return BeanHelper.compareTo( this, obj, "VarId1,VarFamNr,Bereich" );
    }

    /**
     * The Primary Key class.
     */
    public static class PK implements Serializable
    {
        private static final long serialVersionUID = 1L;

        /** Variantenfamilien-Nummer. */
        private Integer varFamNr;

        /** Id.-Nr. der uebergeord. Varian.. */
        private Integer varId1;

        /** Bereich. */
        private String bereich;

        /**
         * Default constructor.
         */
        public PK()
        {
        }

        /**
         * Constructor with all fields.
         * @param varFamNr  The varFamNr to set (Variantenfamilien-Nummer).
         * @param varId1  The varId1 to set (Id.-Nr. der uebergeord. Varian.).
         * @param bereich  The bereich to set (Bereich).
         */
        public PK( final Integer varId1, final Integer varFamNr, final String bereich )
        {
            this.varFamNr = varFamNr;
            this.varId1 = varId1;
            this.bereich = bereich;
        }

        /**
         * @return Returns the varFamNr (Variantenfamilien-Nummer).
         */
        public Integer getVarFamNr()
        {
            return this.varFamNr;
        }

        /**
         * @param varFamNr  The varFamNr to set (Variantenfamilien-Nummer).
         */
        public void setVarFamNr( final Integer varFamNr )
        {
            this.varFamNr = varFamNr;
        }

        /**
         * @return Returns the varId1 (Id.-Nr. der uebergeord. Varian.).
         */
        public Integer getVarId1()
        {
            return this.varId1;
        }

        /**
         * @param varId1  The varId1 to set (Id.-Nr. der uebergeord. Varian.).
         */
        public void setVarId1( final Integer varId1 )
        {
            this.varId1 = varId1;
        }

        /**
         * @return Returns the bereich (Bereich).
         */
        public String getBereich()
        {
            String rv;
            if ( this.bereich == null ) {
                rv = null;
            } else {
                if ( this.bereich.trim().length() == 0 ) {
                    rv = this.bereich.trim();
                } else {
                    rv = this.bereich;
                }
            }
            return rv;
        }

        /**
         * @param bereich  The bereich to set (Bereich).
         */
        public void setBereich( final String bereich )
        {
            this.bereich = bereich;
        }

        @Override
        public final boolean equals( final Object obj )
        {
            return BeanHelper.equals( this, obj );
        }

        @Override
        public final int hashCode()
        {
            return BeanHelper.hashCode( this );
        }

        @Override
        public final String toString()
        {
            return BeanHelper.toString( this );
        }
    }
}

