
package org.profsoftsvcs.dbutils.DVD;

import  java.io.*;
import  java.util.*;

import  javax.swing.*;

import  org.profsoftsvcs.format.*;


/**
 *
 *  <p>
 *  This class is an EJB Entity Bean used to store data for a
 *  specific DVD offering.  Offerings are identified by various
 *  items of information, such as title, seasons, and case numbers.
 *  Note that multiple offerings can share the same physical disk,
 *  and multiple disks can share the same physical case.  Single
 *  offerings may also reside on multiple disks and may occupy
 *  multiple physical cases.
 *  </p>
 *  <p>
 *  Each bean instance represents an offering, but this doesn't
 *  mean that the offering is unique as to title.  It is quite
 *  possible that a DVD collection may contain multiple instances
 *  of the same offering.  Thus, we may have multiple beans for
 *  the same title.  For this reason, the entity primary key is
 *  auto-generated to be unique.  The title itself is identified
 *  by the key assigned by the Internet Movie Database (IMDB).
 *  </p>
 *  <p>
 *  Other information from IMDB is also collected for the title,
 *  namely the <i>genre(s)</i>, any applicable <i>keywords</i>,
 *  and the <i>runlength</i>.  While the genre and keyword lists
 *  are initialized from IMDB data, they may be added to by a
 *  user.  The data is kept as <b>String</b>s, so users may
 *  invent values of their own.
 *  </p>
 *  <p>
 *  Each bean contains an <b>ArrayList</b> of <b>DBUtDVDSeason</b>
 *  objects, which is <code>null</code> if the title is not
 *  seasonal.  Each season contains an array of case numbers for
 *  that season, and the bean itself contains an array of case
 *  numbers which is the union of all of the season ranges, or
 *  just the range for the offering if it's not seasonal.
 *  uniquely identifies the title.
 *  </p> <br> <br>
 *  <h4>
 *  Usage
 *  </h4>
 *  <p>
 *  The class is used primarily by the <b>DBUtDVDSessionBeanImpl</b>
 *  class on behalf of the <b>DBUtDVDDisks</b> class.
 *  </p> <br> <br>
 *
 *  @author   Frank Griffin
 *  @version  1.0
 *  @see DBUtDVDSessionBeanImpl
 *
 */

public class DBUtDVDTitleEntityBean
             implements Serializable,
                        Cloneable
 {
  /**
   *
   *  Entity Bean "id" field.
   *
   */

  private
   long                           id;


  /**
   *
   *  The title of the offering.
   *
   */

  private
   String                         szTitle;


  /**
   *
   *  The IMDB title key of the offering.
   *
   */

  private
   String                         szTitleKey;


  /**
   *
   *  The season of the offering (0 if the offering is not
   *  seasonal).
   *
   */

  private
   ArrayList<DBUtDVDSeason>       alSeasons;


  /**
   *
   *  The case numbers of the physical disks for this offering.
   *
   */

  private
   int[]                          aiCaseNumbers;


  /**
   *
   *  The set of name keys for the actors in this offering.
   *
   */

  private
   HashSet<String>                hsActors = new HashSet<String>();


  /**
   *
   *  The set of name keys for the genres represented by this
   *  offering.
   *
   */

  private
   HashSet<String>                hsGenres = new HashSet<String>();


  /**
   *
   *  The set of name keys for the keywords represented by this
   *  offering.
   *
   */

  private
   HashSet<String>                hsKeywords = new HashSet<String>();


  /**
   *
   *  The run length of this offering in minutes.  This is
   *  meaningful only for single titles - not for offerings
   *  that include several performances or episodes.
   *
   */

  private
   long                           lRunLength = 0L;


  /**
   *
   *  The URL of the offering's entry in the Internet Movie
   *  Database (IMDB).
   *
   */

  private
   String                         szURL;


  /**
   *
   *  A <b>JTextField</b> to be associated with this title during
   *  application processing.
   *
   */

  private transient
   JTextField                     oAssociated;


  /**
   *
   *  A <b>DBUtDVDDisks</b> to be associated with this title during
   *  application processing.
   *
   */

  private transient
   Object                         oDisks;


  /**
   *
   *  A <b>HashMap</b> mapping case number ranges to associated
   *  keywords.
   *
   */

  public static final
   HashMap<int[][],
           List<String>>          KEYWORD_BY_CASE;

  static
   {
    ArrayList<int[]>              alRanges;
    ArrayList<String>             alKeys;
    String[]                      aszKeys;
    int[]                         aiRange;


    KEYWORD_BY_CASE = new HashMap<int[][],
                                 List<String>>();

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1000;
    aiRange[1] = 1099;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Bette Davis"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1100;
    aiRange[1] = 1199;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Miss Marple"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1200;
    aiRange[1] = 1299;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Stephen King"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1300;
    aiRange[1] = 1499;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Horror"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1500;
    aiRange[1] = 1599;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Alfred Hitchcock"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1600;
    aiRange[1] = 1649;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Abbot and Costello"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1650;
    aiRange[1] = 1699;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Laurel and Hardy"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1700;
    aiRange[1] = 1799;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Christmas",
                    "Holiday"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1800;
    aiRange[1] = 1899;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Historical Documentary"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 1900;
    aiRange[1] = 1999;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Scientific Documentary"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 2000;
    aiRange[1] = 2099;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Religous Documentary"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 4000;
    aiRange[1] = 4099;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "Homemade"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );

    alRanges = new ArrayList<int[]>();
    aiRange = new int[2];
    aiRange[0] = 6000;
    aiRange[1] = 6099;
    alRanges.add( aiRange );
    aszKeys = new String[]
                   {
                    "TV Series"
                   };
    alKeys = new ArrayList<String>( 2 );
    for  ( String  szKey : aszKeys )
     { alKeys.add( szKey ); }
    KEYWORD_BY_CASE
     .put( alRanges.toArray( new int[alRanges.size()][2] ),
           alKeys );
  }


  /**
   *
   *  Define the serialVersionUID.
   *
   */

  protected static final
   long                           serialVersionUID = 1L;



/**
 *
 *  <p>
 *  A no-parameter constructor.
 *  </p><br><br>
 *
 */

public
 DBUtDVDTitleEntityBean()
  {
  }




/**
 *
 *  <p>
 *  This method is the getter for the EJB key.
 *  </p> <br> <br>
 *
 *  @return                       the ID
 *
 */

public
 long
 getId()
  {
   return( this.id );
  }



/**
 *
 *  <p>
 *  This method is the setter for the EJB key.
 *  </p> <br> <br>
 *
 *  @param  lID                   <code>long</code> containing ID
 *
 */

public
 void
 setId( long  lID )
  {
   this.id = lID;
  }



/**
 *
 *  <p>
 *  This method is the getter for the Title.
 *  </p> <br> <br>
 *
 *  @return                       The offering title
 *
 */

public
 String
 getTitle()
  {
   return( this.szTitle );
  }



/**
 *
 *  <p>
 *  This method is the setter for the Title.
 *  </p> <br> <br>
 *
 *  @param  szTitle               <b>String</b> containing title
 *
 */

public
 void
 setTitle( String  szTitle )
  {
   this.szTitle = szTitle;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the Title Key.
 *  </p> <br> <br>
 *
 *  @return                       The offering title key
 *
 */

public
 String
 getTitleKey()
  {
   return( this.szTitleKey );
  }



/**
 *
 *  <p>
 *  This method is the setter for the Title Key.
 *  </p> <br> <br>
 *
 *  @param  szTitleKey            <b>String</b> containing title
 *                                 key
 *
 */

public
 void
 setTitleKey( String  szTitleKey )
  {
   this.szTitleKey = szTitleKey;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the Season list.
 *  </p> <br> <br>
 *
 *  @return                       The offering's list of
 *                                 <b>DBUtDVDSeason</b> objects
 *                                 or <code>null</code> if not
 *                                 seasonal
 *
 */

public
 ArrayList<DBUtDVDSeason>
 getSeasons()
  {
   return( this.alSeasons );
  }



/**
 *
 *  <p>
 *  This method is the setter for the Season list.
 *  </p> <br> <br>
 *
 *  @param  alSeasons             <b>ArrayList</b> containing
 *                                 the <b>DBUtDVDSeason<b> objects
 *                                 or <code>null</code> if not
 *                                 seasonal
 *
 */

public
 void
 setSeasons( ArrayList<DBUtDVDSeason>  alSeasons )
  {
   this.alSeasons = alSeasons;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the Case Numbers.
 *  </p> <br> <br>
 *
 *  @return                       The case numbers for the
 *                                 offering
 *
 */

public
 int[]
 getCaseNumbers()
  {
   return( this.aiCaseNumbers );
  }



/**
 *
 *  <p>
 *  This method is the setter for the Case Numbers.
 *  </p> <br> <br>
 *
 *  @param  aiCaseNumbers         <code>int[]</code>
 *                                 containing the case numbers
 *                                 for the offering
 *
 */

public
 void
 setCaseNumbers( int[]  aiCaseNumbers )
  {
   this.aiCaseNumbers = aiCaseNumbers;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the Actor Name keys.
 *  </p> <br> <br>
 *
 *  @return                       The IMDB name keys for the
 *                                 actors in this offering
 *                                 (set only for the first
 *                                  season of a seasonal
 *                                  offering and non-seasonal
 *                                  offerings, <code>null</code>
 *                                  otherwise)
 *
 */

public
 HashSet<String>
 getActors()
  {
   return( this.hsActors );
  }



/**
 *
 *  <p>
 *  This method is the setter for the Actor Name keys.
 *  </p> <br> <br>
 *
 *  @param  hsActors              <b>HashSet&lt;String&gt;</b>
 *                                 containing the name keys
 *                                 for the actors involved
 *
 */

public
 void
 setActors( HashSet<String>  hsActors )
  {
   this.hsActors = hsActors;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the Genre Name keys.
 *  </p> <br> <br>
 *
 *  @return                       The genre name keys for this
 *                                 offering
 *                                 (set only for the first
 *                                  season of a seasonal
 *                                  offering and non-seasonal
 *                                  offerings, <code>null</code>
 *                                  otherwise)
 *
 */

public
 HashSet<String>
 getGenres()
  {
   return( this.hsGenres );
  }



/**
 *
 *  <p>
 *  This method is the setter for the Genre keys.
 *  </p> <br> <br>
 *
 *  @param  hsGenres              <b>HashSet&lt;String&gt;</b>
 *                                 containing the name keys
 *                                 for the genres involved
 *
 */

public
 void
 setGenres( HashSet<String>  hsGenres )
  {
   this.hsGenres = hsGenres;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the Keyword Name keys.
 *  </p> <br> <br>
 *
 *  @return                       The keyword name keys for this
 *                                 offering
 *                                 (set only for the first
 *                                  season of a seasonal
 *                                  offering and non-seasonal
 *                                  offerings, <code>null</code>
 *                                  otherwise)
 *
 */

public
 HashSet<String>
 getKeywords()
  {
   return( this.hsKeywords );
  }



/**
 *
 *  <p>
 *  This method is the setter for the Keyword keys.
 *  </p> <br> <br>
 *
 *  @param  hsKeywords            <b>HashSet&lt;String&gt;</b>
 *                                 containing the keyword keys
 *                                 for the keywords involved
 *
 */

public
 void
 setKeywords( HashSet<String>  hsKeywords )
  {
   this.hsKeywords = hsKeywords;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the RunLength.
 *  </p> <br> <br>
 *
 *  @return                       The run length in minutes for this
 *                                 offering
 *                                 (set only for the first
 *                                  season of a seasonal
 *                                  offering and non-seasonal
 *                                  offerings, <code>null</code>
 *                                  otherwise)
 *
 */

public
 long
 getRunLength()
  {
   return( this.lRunLength );
  }



/**
 *
 *  <p>
 *  This method is the setter for the RunLength.
 *  </p> <br> <br>
 *
 *  @param  lRunLength            <code>long</code> giving the
 *                                 run length in minutes
 *
 */

public
 void
 setRunLength( long  lRunLength )
  {
   this.lRunLength = lRunLength;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the IMDB URL.
 *  </p> <br> <br>
 *
 *  @return                       The offering IMDB URL
 *
 */

public
 String
 getUrl()
  {
   return( this.szURL );
  }



/**
 *
 *  <p>
 *  This method is the setter for the IMDB URL.
 *  </p> <br> <br>
 *
 *  @param  szURL                 <b>String</b> containing URL
 *
 */

public
 void
 setUrl( String  szURL )
  {
   this.szURL = szURL;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the associated <b>JTextField</b>.
 *  This field is valid only during client application processing.
 *  </p> <br> <br>
 *
 *  @return                       The associated <b>JTextField</b>
 *
 */

public
 JTextField
 getAssociated()
  {
   return( this.oAssociated );
  }



/**
 *
 *  <p>
 *  This method is the setter for the associated <b>JTextField</b>.
 *  </p> <br> <br>
 *
 *  @param  oAssociated           <b>JTextField</b> to be associated
 *                                 with this title
 *
 */

public
 void
 setAssociated( JTextField  oAssociated )
  {
   this.oAssociated = oAssociated;
   return;
  }



/**
 *
 *  <p>
 *  This method is the getter for the associated
 *  <b>DBUtDVDDisks</b>.  This field is valid only during client
 *  application processing.  It is typed here as <b>Object</b>
 *  to avoid a circular dependency between the client and ejb
 *  Maven projects.
 *  </p> <br> <br>
 *
 *  @return                       The associated <b>DBUtDVDDisks</b>
 *
 */

public
 Object
 getDisks()
  {
   return( this.oDisks );
  }



/**
 *
 *  <p>
 *  This method is the setter for the associated <b>DBUtDVDDisks</b>.
 *  </p> <br> <br>
 *
 *  @param  oDisks                <b>DBUtDVDDisks</b> to be associated
 *                                 with this title
 *
 */

public
 void
 setDisks( Object  oDisks )
  {
   this.oDisks = oDisks;
   return;
  }



/**
 *
 *  <p>
 *  This method overrides the parent method to make us cloneable.
 *  </p> <br> <br>
 *
 *  @return                       a copy of this <b>Object</b>
 *
 */

@Override
public
 Object
 clone()
  {
   Object   oTitle = null;
   try
    {
     oTitle = super.clone();
    }
    catch( Throwable  tE )
     {}

   return( oTitle );
  }



/**
 *
 *  <p>
 *  This method overrides the parent method to compare
 *  individual fields.
 *  </p> <br> <br>
 *
 *  @param  o                     <b>Object</b> for comparison
 *  @return                       <code>true</code> if it
 *                                 represents the same title
 *
 */

@Override
public
 boolean
 equals( Object  o )
  {
   DBUtDVDTitleEntityBean   oTitle;
   int                      iLimit;


   if  ( !(o instanceof DBUtDVDTitleEntityBean) )
    { return( false ); }
   oTitle = (DBUtDVDTitleEntityBean)o;

   if  ( (this.szTitle == null) ^ (oTitle.szTitle == null) )
    { return( false ); }

   if  ( this.szTitle != null )
    {
     if  ( !this.szTitle.equals( oTitle.szTitle ) )
      { return( false ); }
    }

   if  ( (this.szURL == null) ^ (oTitle.szURL == null) )
    { return( false ); }

   if  ( this.szURL != null )
    {
     if  ( !this.szURL.equals( oTitle.szURL ) )
      { return( false ); }
    }

   if  ( (this.aiCaseNumbers == null)
           ^ (oTitle.aiCaseNumbers == null) )
    { return( false ); }

   if  ( this.aiCaseNumbers != null )
    {
     if  ( !Arrays.equals( this.aiCaseNumbers,
                           oTitle.aiCaseNumbers ) )
      { return( false ); }
    }

   if  ( (this.alSeasons == null) ^ (oTitle.alSeasons == null) )
    { return( false ); }

   if  ( this.alSeasons != null )
    {
     iLimit = this.alSeasons.size();
     if  ( iLimit != oTitle.alSeasons.size() )
      { return( false ); }
     for  ( int  i = 0;
            i < iLimit;
            i++ )
      {
       if  ( !this.alSeasons.get( i )
                  .equals( oTitle.alSeasons.get( i ) ) )
        { return( false ); }
      }
    }

   if  ( (this.hsActors == null) ^ (oTitle.hsActors == null) )
    { return( false ); }

   if  ( this.hsActors != null )
    {
     if  ( !this.hsActors.equals( oTitle.hsActors ) )
      { return( false ); }
    }

   if  ( (this.hsGenres == null) ^ (oTitle.hsGenres == null) )
    { return( false ); }

   if  ( this.hsGenres != null )
    {
     if  ( !this.hsGenres.equals( oTitle.hsGenres ) )
      { return( false ); }
    }

   if  ( (this.hsKeywords == null) ^ (oTitle.hsKeywords == null) )
    { return( false ); }

   if  ( this.hsKeywords != null )
    {
     if  ( !this.hsKeywords.equals( oTitle.hsKeywords ) )
      { return( false ); }
    }

   if  ( this.lRunLength != oTitle.lRunLength )
    { return( false ); }

   return( true );
  }



/**
 *
 *  <p>
 *  This method adds a formatted display of the class data
 *  to a <b>FmtIndentedStringBuffer</b>.
 *  </p> <br> <br>
 *
 *  @param  szb                   <b>FmtIndentedStringBuffer</b>
 *                                 to receive the formatted data
 *  @param  szIndent              <b>String</b> giving indent
 *                                 string to use; if this is
 *                                 <code>null</code>, no indent
 *                                 is performed
 *  @param  iIndent               <code>int</code> giving the
 *                                 number of times to apply
 *                                 the indent; ignored if indent
 *                                 string is <code>null</code>
 *
 */

public
 void
 formatData( FmtIndentedStringBuffer  szb,
             String                   szIndent,
             int                      iIndent )
  {
   String                    szWork;
   ArrayList<DBUtDVDSeason>  alSeasons;



   szb.pushIndentEntry( szIndent,
                        iIndent );

   FmtMisc.formatObject
    ( (FmtRes.locateResource
               ( "%ID" )).getResource(),
      null,
      0,
      new Long( this.getId() ),
      szb,
      "  ",
      1 );
   szb.appendNoIndent( "\n" );

   FmtMisc.formatObject
    ( (FmtRes.locateResource
               ( "%OfferingTitle" )).getResource(),
      null,
      0,
      this.getTitle(),
      szb,
      "  ",
      1 );
   szb.appendNoIndent( "\n" );

   FmtMisc.formatObject
    ( (FmtRes.locateResource
               ( "%OfferingTitleKey" )).getResource(),
      null,
      0,
      this.getTitleKey(),
      szb,
      "  ",
      1 );
   szb.appendNoIndent( "\n" );

   alSeasons = this.getSeasons();
   if  ( alSeasons != null )
    {
     FmtMisc.formatObject
      ( (FmtRes.locateResource
                 ( "%OfferingSeasons" )).getResource(),
        null,
        0,
        alSeasons,
        szb,
        "  ",
        1 );
    }
    else
    {
     szWork = (FmtRes.locateResource
               ( "%OfferingNotSeasonal" )).getResource();
     FmtMisc.formatObject
      ( (FmtRes.locateResource
                 ( "%OfferingSeasons" )).getResource(),
        null,
        0,
        szWork,
        szb,
        "  ",
        1 );
    }
   szb.appendNoIndent( "\n" );

   FmtMisc.formatObject
    ( (FmtRes.locateResource
                ( "%OfferingCaseNumbers" )).getResource(),
      null,
      0,
      this.getCaseNumbers(),
      szb,
      "  ",
      1 );
   szb.appendNoIndent( "\n" );

   FmtMisc.formatObject
    ( (FmtRes.locateResource
                ( "%OfferingIMDBURL" )).getResource(),
      null,
      0,
      this.getUrl(),
      szb,
      "  ",
      1 );
   szb.appendNoIndent( "\n" );

   FmtMisc.formatObject
    ( (FmtRes.locateResource
                ( "%OfferingActorKeys" )).getResource(),
      null,
      0,
      this.getActors(),
      szb,
      "  ",
      1 );
   szb.appendNoIndent( "\n" );

   szb.popIndentEntry();

   return;
  }



/**
 *
 *  <p>
 *  This method provides a description of the title.
 *  </p><br><br>
 *
 *  @return                        the title description
 *
 */

public
 String
 display()
  {
   FmtIndentedStringBuffer     szb;


   szb = new FmtIndentedStringBuffer();
   this.formatData( szb,
                    null,
                    0 );

   return( szb.toString() );
  }



/**
 *
 *  <p>
 *  This method provides a description of the title, and will
 *  be used in its display.
 *  </p><br><br>
 *
 *
 */

@Override
public
 String
 toString()
  {
   StringBuilder      szb = new StringBuilder();


   szb.append( this.szTitle );
   szb.append( " (" );
   szb.append( FmtCnv.rangeArrayToRangeList
                ( FmtCnv.arrayToRangeArray( this.aiCaseNumbers ) ) );
   szb.append( ')' );

   return( szb.toString() );
  }
 }
